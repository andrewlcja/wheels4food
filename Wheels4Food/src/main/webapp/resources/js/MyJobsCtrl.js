(function () {
    'use strict';
    angular
            .module('Wheels4Food.Jobs')
            .controller('MyJobsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService) {
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
                    $scope.tableColumns = ['demand.supply.itemName', 'demand.supply.user.organizationName', 'demand.user.organizationName', 'demand.quantityDemanded', 'expiryDate'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetJobListByUserIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.jobList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;

                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['demand.supply.itemName']);
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
