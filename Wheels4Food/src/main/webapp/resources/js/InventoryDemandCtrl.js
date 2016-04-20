(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryDemandCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams', 'config',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams, config) {
                    $scope.config = config;
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    $scope.sort = function (sortType) {
                        $scope.sortType = sortType;
                        $scope.sortReverse = !$scope.sortReverse;
                    };

                    //default
                    $scope.sortType = 'date';

                    $scope.sortBy = function (demand) {
                        if ($scope.sortType === 'organizationName') {
                            return demand['user']['organizationName'];
                        } else if ($scope.sortType === 'date') {
                            var parts = demand.preferredDeliveryDate.split('/');
                            var date = new Date(parseInt(parts[2]), parseInt(parts[1]), parseInt(parts[0]));
                            return date;
                        }
                        return demand[$scope.sortType];
                    };


                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};
                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            proccessed = {
                                'supplier': {
                                    organizationName: input
                                }
                            };
                        }

                        return proccessed;
                    };

                    //retrieve details
                    $scope.getObj = function (component, column) {
                        var columnPath = column.split(".");
                        var obj = component;
                        for (var y = 0; y < columnPath.length; y++) {
                            if (!obj[columnPath[y]]) {
                                return '';
                            }
                            obj = obj[columnPath[y]];
                        }
                        return obj;
                    };

                    $scope.view = function (demand) {
                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + demand.id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.currentDemandItemList = response.data;
                        });

                        if (demand.status === 'Pending') {
                            $scope.currentDemand = demand;

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/viewDemandDetails.html',
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            });
                        } else if (demand.status === 'Job Created') {
                            $http({
                                url: api.endpoint + 'GetJobByDemandIdRequest/' + demand.id,
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                $scope.currentJob = response.data;

                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/viewJobDetails.html',
                                    className: 'ngdialog-theme-default dialog-approve-request-3',
                                    scope: $scope
                                });
                            });
                        } else if (demand.status === 'Self Collection Created') {
                            $http({
                                url: api.endpoint + 'GetSelfCollectionByDemandIdRequest/' + demand.id,
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                $scope.currentSelfCollection = response.data;

                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/viewSelfCollectionDetails.html',
                                    className: 'ngdialog-theme-default dialog-generic-2',
                                    scope: $scope
                                });
                            });
                        } else if (demand.status === 'Job Accepted') {
                            $http({
                                url: api.endpoint + 'GetJobByDemandIdRequest/' + demand.id,
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                $scope.currentJob = response.data;

                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/viewAcceptedJobDetailsVWO.html',
                                    className: 'ngdialog-theme-default dialog-view-job',
                                    scope: $scope
                                });
                            });
                        }
                    };

                    $scope.selectSlot = function (value) {
                        if (value) {
                            $scope.scheduleCount++;
                        } else {
                            $scope.scheduleCount--;
                        }
                    };

                    $scope.cancel = function (demand) {
                        $scope.currentDemand = demand;
                        var index = $scope.demandList.indexOf(demand);

                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + demand.id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.currentDemandItemList = response.data;
                        });

                        if (demand.status === 'Pending' || demand.status === 'Rejected') {
                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/cancelDemandPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            }).then(function () {
                                $http({
                                    url: api.endpoint + 'DeleteDemandRequest/' + demand.id,
                                    method: 'DELETE',
                                    headers: {
                                        'Content-Type': 'application/json',
                                    }
                                }).then(function (response) {
                                    if (response.data.isDeleted) {
                                        $scope.demandList.splice(index, 1);
                                    }
                                });
                            });
                        } else if (demand.status === 'Job Created') {
                            demand.comments = '';

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            }).then(function () {
                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/cancelCreatedJobReasons.html',
                                    className: 'ngdialog-theme-default dialog-generic',
                                    scope: $scope
                                }).then(function (reason) {
                                    if (demand.comments === 'other') {
                                        demand.comments = reason;
                                    }

                                    $http({
                                        url: api.endpoint + 'CancelJobByDemandIdRequest',
                                        method: 'PUT',
                                        data: demand,
                                        headers: {
                                            'Content-Type': 'application/json',
                                        }
                                    }).then(function (response) {
                                        if (response.data.isCancelled) {
                                            $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                        }
                                    });
                                });
                            });
                        } else if (demand.status === 'Job Accepted') {
                            demand.comments = '';

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic',
                                scope: $scope
                            }).then(function () {
                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobReasons.html',
                                    className: 'ngdialog-theme-default dialog-generic',
                                    scope: $scope
                                }).then(function () {
                                    $http({
                                        url: api.endpoint + 'CancelJobByDemandIdRequest',
                                        method: 'PUT',
                                        data: demand,
                                        headers: {
                                            'Content-Type': 'application/json',
                                        }
                                    }).then(function (response) {
                                        if (response.data.isCancelled) {
                                            $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                        }
                                    });
                                });
                            });
                        } else if (demand.status === 'Self Collection Created') {
                            $http({
                                url: api.endpoint + 'GetSelfCollectionByDemandIdRequest/' + demand.id,
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                $scope.currentSelfCollection = response.data;

                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/cancelSelfCollectionPrompt.html',
                                    className: 'ngdialog-theme-default dialog-generic-2',
                                    scope: $scope
                                }).then(function () {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/cancelSelfCollectionReasons.html',
                                        className: 'ngdialog-theme-default dialog-generic-2',
                                        scope: $scope
                                    }).then(function () {
                                        $http({
                                            url: api.endpoint + 'CancelSelfCollectionByDemandIdRequest',
                                            method: 'PUT',
                                            data: $scope.currentSelfCollection.demand,
                                            headers: {
                                                'Content-Type': 'application/json',
                                            }
                                        }).then(function (response) {
                                            if (response.data.isCancelled) {
                                                $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                            }
                                        });
                                    });
                                });
                            });
                        }
                    };

                    $scope.complete = function (demand) {
                        $scope.currentDemand = demand;

                        if (demand.status === 'Job Accepted') {
                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/completeJobPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic',
                                scope: $scope
                            }).then(function () {
                                $http({
                                    url: api.endpoint + 'CompleteJobByDemandIdRequest/' + demand.id,
                                    method: 'PUT',
                                    headers: {
                                        'Content-Type': 'application/json',
                                    }
                                }).then(function (response) {
                                    if (response.data.isCompleted) {
                                        $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                    }
                                });
                            });
                        } else if (demand.status === 'Self Collection Created') {
                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/completeSelfCollectionPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic',
                                scope: $scope
                            }).then(function () {
                                $http({
                                    url: api.endpoint + 'CompleteSelfCollectionByDemandIdRequest/' + demand.id,
                                    method: 'PUT',
                                    headers: {
                                        'Content-Type': 'application/json',
                                    }
                                }).then(function (response) {
                                    if (response.data.isCompleted) {
                                        $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                    }
                                });
                            });
                        }
                    };

                    //counts the days selected
                    $scope.dayCount = 0;
                    $scope.selectDay = function (day) {
                        if (day) {
                            $scope.dayCount++;
                        } else {
                            $scope.dayCount--;
                        }
                    };

                    //set up user table columns
                    $scope.tableColumns = ['supplier.organizationName', 'preferredDeliveryDate'];

                    var indexPromise = $http({
                        url: api.endpoint + 'GetDemandListByUserIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        $http({
                            url: api.endpoint + 'GetDemandItemListByRequesterIdRequest/' + userID,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.demandItemList = response.data;

                            indexPromise.then(function (response) {
                                $scope.demandList = response.data;
                                console.log(response);
                                $scope.currentPage = 1;
                                $scope.pageSize = 10;

                                $scope.$watch('searchFilter', function () {
                                    $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['supply.itemName']);
                                });
                            });
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 1000;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";
                }
            ]);
})();
