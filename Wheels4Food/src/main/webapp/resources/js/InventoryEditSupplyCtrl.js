(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryEditSupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$filter', '$stateParams', 'config',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $filter, $stateParams, config) {
                    $scope.config = config;
                    
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
                            var parts = $scope.supply.expiryDate.split("/");
                            $scope.expiryDate = new Date(parseInt(parts[2], 10),
                                    parseInt(parts[1], 10) - 1,
                                    parseInt(parts[0], 10));

                            $scope.showSupply = true;

                            if ($scope.supply.expiryDate === 'NA') {
                                $scope.expiryDate = '01/01/1999';
                                $scope.expiryDateNA = true;
                            }

                            $scope.$watch('expiryDate', function (newValue, oldValue) {
                                if (newValue !== '') {
                                    $scope.supply.expiryDate = $filter('date')($scope.expiryDate, 'dd/MM/yyyy');
                                }
                            });
                        });
                    }, 1000);

                    $scope.toggleExpiryValidity = function () {
                        if ($scope.expiryDateNA) {
                            $scope.supplyForm.expiryDate.$setValidity('customDate', true);
                            $scope.supplyForm.expiryDate.$setValidity('customDate2', true);
                        } else {
                            if (!$scope.expiryDate) {
                                if (new Date() >= $scope.tempExpiryDate) {
                                    $scope.supplyForm.expiryDate.$setValidity('customDate2', false);
                                } else {
                                    $scope.supplyForm.expiryDate.$setValidity('customDate', false);
                                }
                            }
                        }
                    };

                    $scope.clear = function () {
                        $scope.expiryDate = null;
                    };

                    $scope.toggleMin = function () {
                        $scope.minDate = $scope.minDate ? null : new Date();
                    };
                    $scope.toggleMin();

                    $scope.open = function ($event) {
                        $event.preventDefault();
                        $event.stopPropagation();

                        $scope.opened = true;
                    };

                    $scope.dateOptions = {
                        formatYear: 'yy',
                        startingDay: 1
                    };

                    $scope.format = 'dd/MM/yyyy';

                    $scope.update = function () {
                        if ($scope.expiryDateNA) {
                            $scope.supply.expiryDate = 'NA';
                        }

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/updateSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'UpdateSupplyRequest',
                                method: 'PUT',
                                data: $scope.supply,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isUpdated) {
                                    $state.go('Inventory.Supply');
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/updateSupplyError.html',
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
            .directive('ecustomMin', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomMin = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.maximum && modelValue > $scope.supply.maximum) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomMin2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomMin2 = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.quantitySupplied && (modelValue > $scope.supply.quantitySupplied || (modelValue > Math.ceil($scope.supply.quantitySupplied / 2) && modelValue < $scope.supply.quantitySupplied))) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomMax', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomMax = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.minimum && modelValue < $scope.supply.minimum) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomMax2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomMax2 = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.quantitySupplied && (modelValue > $scope.supply.quantitySupplied || ($scope.supply.quantitySupplied - modelValue !== 0 && modelValue > $scope.supply.quantitySupplied - $scope.supply.minimum))) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomDate', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomDate = function (modelValue) {
                            //true or false based on custom dir validation
                            if (!$scope.expiryDateNA && !modelValue) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomDate2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomDate2 = function (modelValue) {
                            $scope.tempExpiryDate = modelValue;
                            //true or false based on custom dir validation
                            if (!$scope.expiryDateNA && new Date() >= modelValue) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomQuantitySupplied', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomQuantitySupplied = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.minimum && !$scope.supply.maximum && ($scope.supply.minimum > modelValue || (modelValue > $scope.supply.minimum && modelValue < $scope.supply.minimum * 2))) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomQuantitySupplied2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomQuantitySupplied2 = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.maximum && !$scope.supply.minimum && ($scope.supply.maximum > modelValue || (modelValue > $scope.supply.maximum && modelValue < $scope.supply.maximum * 2))) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomQuantitySupplied3', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomQuantitySupplied3 = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.maximum && $scope.supply.minimum && (modelValue !== $scope.supply.maximum && modelValue < $scope.supply.minimum + $scope.supply.maximum)) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomNumber', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomNumber = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue % 1 !== 0) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('ecustomQuantity', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.ecustomQuantity = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && !$scope.supply.quantitySupplied) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            });
})();
