(function () {
    'use strict';
    angular
            .module('Wheels4Food.ApprovedRequests')
            .controller('ApprovedRequestsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams', '$filter',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams, $filter) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

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

                                $scope.scheduleAMList = [];
                                $scope.schedulePMList = [];
                                $scope.disabledAMList = [];
                                $scope.disabledPMList = [];
                                $scope.scheduleCount = 0;

                                for (var i = 0; i < $scope.currentJob.schedule.length; i++) {
                                    var value = $scope.currentJob.schedule.charAt(i);

                                    if (i % 2 === 0) {
                                        if (value === '0') {
                                            $scope.scheduleAMList.push({'value': false});
                                            $scope.disabledAMList.push(i / 2);
                                        } else {
                                            $scope.scheduleAMList.push({'value': true});
                                            $scope.scheduleCount++;
                                        }
                                    } else {
                                        if (value === '0') {
                                            $scope.schedulePMList.push({'value': false});
                                            $scope.disabledPMList.push(Math.floor(i / 2));
                                        } else {
                                            $scope.schedulePMList.push({'value': true});
                                            $scope.scheduleCount++;
                                        }
                                    }
                                }

                                var parts = $scope.currentJob.expiryDate.split("/");
                                var expiryDate = new Date(parseInt(parts[2], 10),
                                        parseInt(parts[1], 10) - 1,
                                        parseInt(parts[0], 10));

                                $scope.dates = [];

                                for (var i = 0; i < 10; i++) {
                                    if (expiryDate.getDay() !== 0 && expiryDate.getDay() !== 6) {
                                        $scope.dates.unshift({'value': new Date(expiryDate)});
                                    } else {
                                        i--;
                                    }

                                    expiryDate.setDate(expiryDate.getDate() - 1);
                                }

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

                    $scope.cancel = function (demand) {
                        $scope.currentDemand = demand;
                        demand.comments = '';
                        
                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + demand.id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.currentDemandItemList = response.data;
                        });

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
                                    className: 'ngdialog-theme-default dialog-generic-2',
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
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            }).then(function () {
                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/cancelCreatedJobSupplierReasons.html',
                                    className: 'ngdialog-theme-default dialog-generic-2',
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
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            }).then(function () {
                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobSupplierReasons.html',
                                    className: 'ngdialog-theme-default dialog-generic-2',
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

                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};

                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            proccessed = {
                                'user': {
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

                    //set up user table columns
                    $scope.tableColumns = ['user.organizationName'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetApprovedDemandListBySupplierIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        $http({
                            url: api.endpoint + 'GetDemandItemListBySupplierIdRequest/' + userID,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.demandItemList = response.data;

                            indexPromise.then(function (response) {
                                $scope.demandList = response.data;
                                $scope.currentPage = 1;
                                $scope.pageSize = 10;

                                $scope.$watch('searchFilter', function () {
                                    $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['itemName']);
                                });
                            });
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 900;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";
                }
            ]);
})();
