(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryCreateSupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$filter',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $filter) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    $scope.supply = {
                        'userID': userID,
                        'itemName': '',
                        'category': '',
                        'quantitySupplied': '',
                        'minimum': '',
                        'maximum': '',
                        'expiryDate': ''
                    };

                    $scope.$watch('expiryDate', function (newValue, oldValue) {
                        if (newValue && newValue !== '') {
                            $scope.supply.expiryDate = $filter('date')($scope.expiryDate, 'dd/MM/yyyy');
                        }
                    });

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

                    $scope.create = function () {
                        if ($scope.expiryDateNA) {
                            $scope.supply.expiryDate = 'NA';
                        }

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/createSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'CreateSupplyRequest',
                                method: 'POST',
                                data: $scope.supply,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isCreated) {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/createSupplySuccess.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    }).then(function (response) {
                                        $state.go('Inventory.Supply');
                                    });
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/createSupplyError.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    });
                                }
                            });
                        });
                    };
                }
            ])
            .directive('customMin', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customMin = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.supply.maximum && modelValue > $scope.supply.maximum) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customMin2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customMin2 = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.supply.quantitySupplied && (modelValue > $scope.supply.quantitySupplied || (modelValue > Math.ceil($scope.supply.quantitySupplied / 2) && modelValue < $scope.supply.quantitySupplied))) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customMax', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customMax = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue && $scope.supply.minimum && modelValue < $scope.supply.minimum) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customMax2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customMax2 = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.supply.quantitySupplied && (modelValue > $scope.supply.quantitySupplied || ($scope.supply.quantitySupplied - modelValue !== 0 && modelValue > $scope.supply.quantitySupplied - $scope.supply.minimum))) {
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
                            if (!$scope.expiryDateNA && !modelValue) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customDate2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customDate2 = function (modelValue) {
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
            .directive('customQuantitySupplied', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customQuantitySupplied = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.supply.minimum && !$scope.supply.maximum && ($scope.supply.minimum > modelValue || (modelValue > $scope.supply.minimum && modelValue < $scope.supply.minimum * 2))) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customQuantitySupplied2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customQuantitySupplied2 = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.supply.maximum && !$scope.supply.minimum && ($scope.supply.maximum > modelValue || (modelValue > $scope.supply.maximum && modelValue < $scope.supply.maximum * 2))) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customQuantitySupplied3', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customQuantitySupplied3 = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.supply.maximum && $scope.supply.minimum && modelValue < $scope.supply.minimum + $scope.supply.maximum) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
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
            .directive('customQuantity', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customQuantity = function (modelValue) {
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
