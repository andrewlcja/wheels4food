(function () {
    'use strict';
    angular
            .module('Wheels4Food.Jobs')
            .controller('JobsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog',
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
                                template: '/Wheels4Food/resources/ngTemplates/viewJobDetails.html',
                                className: 'ngdialog-theme-default dialog-generic',
                                scope: $scope
                            }).then(function (schedule) {
                                if ($scope.currentJob.monday) {
                                    $scope.currentJob.monday = schedule.monday;
                                }

                                if ($scope.currentJob.tuesday) {
                                    $scope.currentJob.tuesday = schedule.tuesday;
                                }

                                if ($scope.currentJob.wednesday) {
                                    $scope.currentJob.wednesday = schedule.wednesday;
                                }

                                if ($scope.currentJob.thursday) {
                                    $scope.currentJob.thursday = schedule.thursday;
                                }

                                if ($scope.currentJob.friday) {
                                    $scope.currentJob.friday = schedule.friday;
                                }
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['demand.supply.itemName', 'demand.supply.user.organizationName', 'demand.user.organizationName', 'demand.quantityDemanded', 'expiryDate'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetJobListRequest',
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
