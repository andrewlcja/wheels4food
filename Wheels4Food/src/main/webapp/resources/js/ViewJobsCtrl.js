(function () {
    'use strict';
    angular
            .module('Wheels4Food.Jobs')
            .controller('ViewJobsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog',
                function ($scope, $state, $http, api, $timeout, ngDialog) {
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

                    $scope.goToAccept = function (id) {
                        $state.go('Jobs.Accept', {Id: id});
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
                                template: '/Wheels4Food/resources/ngTemplates/viewJobDetails.html',
                                className: 'ngdialog-theme-default dialog-approve-request-3',
                                scope: $scope
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['deliveryDate', 'timeslot', 'expiryDate'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetJobListRequest',
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
