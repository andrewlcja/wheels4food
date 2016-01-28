(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryViewSupplyRequestsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$filter', '$stateParams',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $filter, $stateParams) {
                    //set default schedule
                    $scope.schedule = {
                        'monday': false,
                        'tuesday': false,
                        'wednesday': false,
                        'thursday': false,
                        'friday': false
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

                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};

                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            proccessed['itemName'] = input;
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

                    $scope.viewJob = function (demand) {
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
                                className: 'ngdialog-theme-default dialog-generic',
                                scope: $scope
                            });
                        });
                    };

                    $scope.viewAcceptedJob = function (demand) {
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
                    };

                    $scope.viewSelfCollection = function (demand) {
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
                                className: 'ngdialog-theme-default dialog-generic',
                                scope: $scope
                            });
                        });
                    };

                    $scope.cancel = function (demand) {
                        $scope.currentDemand = demand;
                        demand.comments = '';

                        if (demand.status === 'Self Collection Created') {
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
                                    className: 'ngdialog-theme-default dialog-generic',
                                    scope: $scope
                                }).then(function () {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/cancelSelfCollectionSupplierReasons.html',
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
                        } else if (demand.status === 'Job Created') {
                            demand.comments = '';

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic',
                                scope: $scope
                            }).then(function () {
                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/cancelCreatedJobSupplierReasons.html',
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
                                    template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobSupplierReasons.html',
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
                        }
                    };

                    var validApproval = function (pendingApproval, index, comment) {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/approveRequestPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function (schedule) {
                            $http({
                                url: api.endpoint + 'CreateJobRequest',
                                method: 'POST',
                                data: {
                                    'demandID': pendingApproval.id,
                                    'monday': schedule.monday,
                                    'tuesday': schedule.tuesday,
                                    'wednesday': schedule.wednesday,
                                    'thursday': schedule.thursday,
                                    'friday': schedule.friday,
                                    'comments': comment
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isCreated) {
                                    $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                }
                            });
                        });
                    };

                    $scope.approve = function (pendingApproval, index) {
                        $scope.currentPendingApproval = pendingApproval;

                        if (pendingApproval.quantityDemanded <= pendingApproval.supply.quantitySupplied) {
                            validApproval(pendingApproval, index, '');
                        } else if (pendingApproval.supply.quantitySupplied > 0) {
                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/approveRequestPartialPrompt.html',
                                className: 'ngdialog-theme-default dialog-approve-request',
                                scope: $scope
                            }).then(function (reason) {
                                validApproval(pendingApproval, index, reason);
                            });
                        }
                    };

                    $scope.reject = function (pendingApproval, index) {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/rejectRequestPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function (reason) {
                            $http({
                                url: api.endpoint + 'RejectDemandRequest/' + pendingApproval.id,
                                method: 'PUT',
                                data: {
                                    'comments': reason
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isRejected) {
                                    $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                }
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['supply.itemName', 'user.organizationName', 'supply.quantitySupplied', 'quantityDemanded'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetDemandListBySupplyIdRequest/' + $stateParams.Id,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.demandList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;

                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['itemName']);
                            });
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";
                }
            ]);
})();
