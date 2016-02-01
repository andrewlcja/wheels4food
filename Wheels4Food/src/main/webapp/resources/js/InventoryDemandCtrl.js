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
                        if (input === null || input === undefined) {
                            proccessed = {};
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

                    $scope.selectSlot = function (value) {
                        if (value) {
                            $scope.scheduleCount++;
                        } else {
                            $scope.scheduleCount--;
                        }
                    };

                    $scope.confirm = function (demand) {
                        $scope.currentDemand = demand;

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
                                template: '/Wheels4Food/resources/ngTemplates/confirmDemandPrompt.html',
                                className: 'ngdialog-theme-default dialog-approve-request-2',
                                scope: $scope
                            }).then(function () {
                                var combinedSchedule = '';

                                for (var i = 0; i < 10; i++) {
                                    if ($scope.scheduleAMList[i].value) {
                                        combinedSchedule += '1';
                                    } else {
                                        combinedSchedule += '0';
                                    }

                                    if ($scope.schedulePMList[i].value) {
                                        combinedSchedule += '1';
                                    } else {
                                        combinedSchedule += '0';
                                    }
                                }

                                $scope.currentJob.schedule = combinedSchedule;

                                $http({
                                    url: api.endpoint + 'ConfirmJobRequest',
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

                    $scope.viewJob = function (demand) {
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
                    };

                    $scope.cancel = function (demand) {
                        $scope.currentDemand = demand;
                        var index = $scope.demandList.indexOf(demand);

                        if (demand.status === 'Pending') {
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
                        } else if (demand.status === 'Job Created') {
                            demand.comments = '';

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/cancelAcceptedJobPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic',
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
                                    className: 'ngdialog-theme-default dialog-generic',
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
                    $scope.tableColumns = ['supply.itemName', 'supply.category', 'quantityDemanded', 'supply.expiryDate'];

                    var indexPromise = $http({
                        url: api.endpoint + 'GetDemandListByUserIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.demandList = response.data;
                            console.log(response);
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
