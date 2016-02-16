(function () {
    'use strict';
    angular
            .module('Wheels4Food.Jobs')
            .controller('MyJobsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};

                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            proccessed = {
                                'demand': {
                                    'supply': {
                                        'itemName': input
                                    }
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

                    $scope.view = function (job) {
                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + job.demand.id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.currentDemandItemList = response.data;
                        });
                        
                        $http({
                            url: api.endpoint + 'GetJobByIdRequest/' + job.id,
                            method: 'GET',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }).then(function (response) {
                            $scope.currentJob = response.data;

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/viewAcceptedJobDetails.html',
                                className: 'ngdialog-theme-default dialog-view-job',
                                scope: $scope
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['demand.supplier.organizationName', 'demand.user.organizationName'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetJobListByUserIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        $http({
                            url: api.endpoint + 'GetDemandItemListRequest',
                            method: 'GET'
                        }).then(function (response) {
                            $scope.demandItemList = response.data;
                            
                            indexPromise.then(function (response) {
                                $scope.jobList = response.data;
                                $scope.currentPage = 1;
                                $scope.pageSize = 10;

                                $scope.$watch('searchFilter', function () {
                                    $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['demand.supply.itemName']);
                                });
                            });
                        });
                    }, 1000);

                    $scope.cancel = function (job) {
                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + job.demand.id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.currentDemandItemList = response.data;
                        });
                        
                        $scope.currentJob = job;

                        job.demand.comments = '';

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobVolunteerPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic-2',
                            scope: $scope
                        }).then(function () {
                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobReasonsVolunteer.html',
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            }).then(function () {
                                $http({
                                    url: api.endpoint + 'CancelJobByDemandIdRequest',
                                    method: 'PUT',
                                    data: job.demand,
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
                    };

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
