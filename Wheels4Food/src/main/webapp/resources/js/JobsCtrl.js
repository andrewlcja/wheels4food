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
                            }).then(function (schedule) {
                                
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
