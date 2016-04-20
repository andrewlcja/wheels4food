(function () {
    'use strict';
    angular
            .module('Wheels4Food.PendingApprovals')
            .controller('PendingApprovalsEditCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams', '$filter', 'config',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams, $filter, config) {
                    $scope.config = config;
                    
                    $scope.scheduleCount = 0;

                    $scope.selectSlot = function (value) {
                        if (value) {
                            $scope.scheduleCount++;
                        } else {
                            $scope.scheduleCount--;
                        }
                    };

                    var indexPromise = $http({
                        url: api.endpoint + 'GetDemandByIdRequest/' + $stateParams.Id,
                        method: 'GET'
                    });

                    $timeout(function () {
                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + $stateParams.Id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.demandItemList = response.data;

                            indexPromise.then(function (response) {
                                $scope.demand = response.data;
                                $scope.showDemand = true;

                                if ($scope.demand.preferredSchedule === 'NA') {
                                    var parts = $scope.demand.preferredDeliveryDate.split("/");
                                    $scope.deliveryDate = new Date(parseInt(parts[2], 10),
                                            parseInt(parts[1], 10) - 1,
                                            parseInt(parts[0], 10));

                                    $scope.timeslot = $scope.demand.preferredTimeslot;
                                } else {
                                    $scope.scheduleAMList = [];
                                    $scope.schedulePMList = [];
                                    $scope.scheduleCount = 0;

                                    for (var i = 0; i < $scope.demand.preferredSchedule.length; i++) {
                                        var value = $scope.demand.preferredSchedule.charAt(i);

                                        if (i % 2 === 0) {
                                            if (value === '0') {
                                                $scope.scheduleAMList.push({'value': false});
                                            } else {
                                                $scope.scheduleAMList.push({'value': true});
                                                $scope.scheduleCount++;
                                            }
                                        } else {
                                            if (value === '0') {
                                                $scope.schedulePMList.push({'value': false});
                                            } else {
                                                $scope.schedulePMList.push({'value': true});
                                                $scope.scheduleCount++;
                                            }
                                        }
                                    }

                                    var parts = $scope.demand.dateRequested.split("/");
                                    var requestDate = new Date(parseInt(parts[2], 10),
                                            parseInt(parts[1], 10) - 1,
                                            parseInt(parts[0], 10));

                                    requestDate.setDate(requestDate.getDate() + 3);
                                    $scope.dates = [];

                                    for (var i = 0; i < 10; i++) {
                                        if (requestDate.getDay() !== 0 && requestDate.getDay() !== 6) {
                                            $scope.dates.push({'value': new Date(requestDate)});
                                        } else {
                                            i--;
                                        }


                                        requestDate.setDate(requestDate.getDate() + 1);
                                    }
                                }
                            });
                        });
                    }, 1000);

                    $scope.clear = function () {
                        $scope.deliveryDate = null;
                    };

                    var today = new Date();
                    $scope.minDate = today.setDate(today.getDate() + 3);

                    $scope.open = function ($event) {
                        $event.preventDefault();
                        $event.stopPropagation();

                        $scope.opened = true;
                    };

                    $scope.dateOptions = {
                        formatYear: 'yy',
                        startingDay: 1
                    };

                    // Disable weekend selection
                    $scope.disabled = function (date, mode) {
                        return (mode === 'day' && (date.getDay() === 0 || date.getDay() === 6));
                    };

                    $scope.format = 'dd/MM/yyyy';

                    $scope.$watch('deliveryDate', function (newValue, oldValue) {
                        if (newValue && newValue !== '') {
                            $scope.demand.preferredDeliveryDate = $filter('date')($scope.deliveryDate, 'dd/MM/yyyy');
                            $scope.deliveryDateFinal = $filter('date')($scope.deliveryDate, 'dd/MM/yyyy');

                            $http({
                                url: api.endpoint + 'GetUnavailableTimeslotsByDeliveryDateRequest',
                                method: 'POST',
                                data: {
                                    'deliveryDate': $scope.deliveryDateFinal
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                $scope.unavailableTimeslots = response.data;
                            });
                        }
                    });

                    $scope.removeDemandItem = function (demandItem) {
                        $scope.demandItemList.splice($scope.demandItemList.indexOf(demandItem), 1);
                    };

                    $scope.update = function () {
                        if ($scope.demand.preferredSchedule !== 'NA') {
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

                            $scope.demand.preferredSchedule = combinedSchedule;
                        }

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/updateDemandPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            indexPromise = $http({
                                url: api.endpoint + 'UpdateDemandRequest',
                                method: 'PUT',
                                data: {
                                    'demand': $scope.demand,
                                    'demandItemList': $scope.demandItemList
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            });

                            $scope.promise = [indexPromise];

                            indexPromise.then(function (response) {
                                if (response.data.isUpdated) {
                                    $state.go('PendingApprovals');
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/updateDemandError.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    });
                                }
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
            ])
            .directive('customNumber', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customNumber = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue % 1 !== 0) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customDate', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customDate = function (modelValue) {
                            //true or false based on custom dir validation
                            if (!modelValue) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            });
})();
