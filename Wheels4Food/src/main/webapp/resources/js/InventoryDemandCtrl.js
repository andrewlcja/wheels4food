(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryDemandCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    //default sort
                    $scope.sortType = 'itemName';

                    $scope.sort = function (sortType) {
                        $scope.sortType = sortType;
                        $scope.sortReverse = !$scope.sortReverse;
                    };

                    $scope.sortBy = function (demand) {
                        if ($scope.sortType === 'organizationName') {
                            return demand['user']['organizationName'];
                        } else if ($scope.sortType === 'itemName') {
                            return demand['supply']['itemName'];
                        } else if ($scope.sortType === 'category') {
                            return demand['supply']['category'];
                        } else if ($scope.sortType === 'expiryDate') {
                            if (supply.expiryDate === 'NA') {
                                return new Date('1000', '01', '01')
                            }
                            
                            var parts = demand.supply.expiryDate.split('/');
                            var date = new Date(parseInt(parts[2]), parseInt(parts[1]), parseInt(parts[0]));
                            return date;
                        }
                        return demand[$scope.sortType];
                    };

                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};
                        var tempArray;
                        if (input === null || input === undefined) {
                            proccessed = null;
                        } else {
                            proccessed = {
                                'supply': {
                                    'itemName': input
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
                        $scope.currentDemand = demand;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/viewDemandDetails.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        });
                    };

                    $scope.confirm = function (demand) {
                        $http({
                            url: api.endpoint + 'GetJobByDemandIdRequest/' + demand.id,
                            method: 'GET',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }).then(function (response) {
                            $scope.currentJob = response.data;

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/confirmDemandPrompt.html',
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

                                $http({
                                    url: api.endpoint + 'UpdateJobRequest',
                                    method: 'PUT',
                                    data: $scope.currentJob,
                                    headers: {
                                        'Content-Type': 'application/json',
                                    }
                                }).then(function (response) {
                                    if (response.data.isUpdated) {
                                        $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                    }
                                });
                            });
                        });
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

                                $http({
                                    url: api.endpoint + 'UpdateJobRequest',
                                    method: 'PUT',
                                    data: $scope.currentJob,
                                    headers: {
                                        'Content-Type': 'application/json',
                                    }
                                }).then(function (response) {
                                    console.log(response.data);
                                });
                            });
                        });
                    };

                    $scope.cancel = function (demand) {
                        $scope.currentDemand = demand;
                        var index = $scope.demandList.indexOf(demand);

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/cancelDemandPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
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
                    $scope.tableColumns = ['supply.itemName', 'supply.category', 'quantityDemanded', 'supply.expiryDate'];

                    var indexPromise = $http({
                        url: api.endpoint + 'GetDemandListByUserIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.demandList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;

                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['supply.itemName']);
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
