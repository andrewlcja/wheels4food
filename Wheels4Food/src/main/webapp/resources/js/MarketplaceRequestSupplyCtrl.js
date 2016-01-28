(function () {
    'use strict';
    angular
            .module('Wheels4Food.Marketplace')
            .controller('MarketplaceRequestSupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams', '$filter',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams, $filter) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;
                    $scope.quantityRequested = '';

                    $scope.request = function () {
                        $http({
                            url: api.endpoint + 'GetSupplyByIdRequest/' + $stateParams.Id,
                            method: 'GET',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }).then(function (response) {
                            var currentSupply = response.data;
                            if (currentSupply.quantitySupplied !== $scope.supply.quantitySupplied || currentSupply.quantityRemaining !== $scope.supply.quantityRemaining || currentSupply.minimum !== $scope.supply.minimum || currentSupply.maximum !== $scope.supply.maximum) {
                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/requestSupplyRefresh.html',
                                    className: 'ngdialog-theme-default dialog-generic',
                                    scope: $scope,
                                    showClose: false
                                }).then(function () {
                                    $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                });
                            } else {
                                ngDialog.openConfirm({
                                    template: '/Wheels4Food/resources/ngTemplates/requestSupplyPrompt.html',
                                    className: 'ngdialog-theme-default dialog-generic',
                                    scope: $scope
                                }).then(function () {
                                    var preferredDeliveryDate = "NA";
                                    var preferredTimeslot = "NA";
                                    var preferredSchedule = "NA";

                                    if ($scope.deliveryType === 'selfCollect') {
                                        preferredDeliveryDate = $scope.deliveryDateFinal;
                                        preferredTimeslot = $scope.timeslot;
                                    } else {
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

                                        preferredSchedule = combinedSchedule;
                                    }

                                    $http({
                                        url: api.endpoint + 'CreateDemandRequest',
                                        method: 'POST',
                                        data: {
                                            'userID': userID,
                                            'supplyID': $stateParams.Id,
                                            'quantityDemanded': $scope.quantityRequested,
                                            'preferredDeliveryDate': preferredDeliveryDate,
                                            'preferredTimeslot': preferredTimeslot,
                                            'preferredSchedule': preferredSchedule
                                        },
                                        headers: {
                                            'Content-Type': 'application/json',
                                        }
                                    }).then(function (response) {
                                        if (response.data.isCreated) {
                                            ngDialog.openConfirm({
                                                template: '/Wheels4Food/resources/ngTemplates/requestSupplySuccess.html',
                                                className: 'ngdialog-theme-default dialog-generic',
                                                scope: $scope
                                            }).then(function (response) {
                                                $state.go('Inventory.Demand');
                                            });
                                        } else {
                                            $scope.errorList = response.data.errorList;

                                            ngDialog.openConfirm({
                                                template: '/Wheels4Food/resources/ngTemplates/requestSupplyError.html',
                                                className: 'ngdialog-theme-default dialog-generic',
                                                scope: $scope
                                            });
                                        }
                                    });
                                });
                            }
                        });
                    };

                    $scope.scheduleAMList = [];
                    $scope.schedulePMList = [];

                    for (var i = 0; i < 10; i++) {
                        $scope.scheduleAMList.push({'value': false});
                        $scope.schedulePMList.push({'value': false});
                    }

                    var today = new Date();
                    today.setDate(today.getDate() + 3);

                    $scope.dates = [];

                    for (var i = 0; i < 10; i++) {
                        if (today.getDay() !== 0 && today.getDay() !== 6) {
                            $scope.dates.push({'value': new Date(today)});
                        } else {
                            i--;
                        }


                        today.setDate(today.getDate() + 1);
                    }

                    $scope.scheduleCount = 0;

                    $scope.selectSlot = function (value) {
                        if (value) {
                            $scope.scheduleCount++;
                        } else {
                            $scope.scheduleCount--;
                        }
                    };

                    $scope.showSelfCollect = function () {
                        $scope.selfCollectOptions = true;
                        $scope.volunteerOptions = false;
                    };

                    $scope.showVolunteer = function () {
                        $scope.selfCollectOptions = false;
                        $scope.volunteerOptions = true;
                    };

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
                            $scope.deliveryDateFinal = $filter('date')($scope.deliveryDate, 'dd/MM/yyyy');

                            $http({
                                url: api.endpoint + 'GetUnavailableTimeslotsByDeliveryDateRequest',
                                method: 'POST',
                                data: {
                                    'supplierID': $scope.supply.user.id,
                                    'deliveryDate': $scope.deliveryDateFinal
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                $scope.unavailableTimeslots = response.data;
                                $scope.showTimeslots = true;
                            });
                        }
                    });

                    var indexPromise = $http({
                        url: api.endpoint + 'GetSupplyByIdRequest/' + $stateParams.Id,
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        }
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.supply = response.data;
                            $scope.showSupply = true;
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
