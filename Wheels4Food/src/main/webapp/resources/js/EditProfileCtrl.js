(function () {
    'use strict';
    angular
            .module('Wheels4Food.User')
            .controller('EditProfileCtrl', ['$scope', '$state', 'localStorageService', '$http', 'api', '$timeout', 'ngDialog', 'config',
                function ($scope, $state, localStorageService, $http, api, $timeout, ngDialog, config) {
                    $scope.config = config;
                    var authData = localStorageService.get('authorizationData');
                    var username = authData.username;

                    if (authData.role === 'Supplier' || authData.role === 'Requester' || authData.role === 'Admin') {
                        $scope.isVWO = true;
                    } else {
                        $scope.isVWO = false;
                    }

                    var indexPromise = $http({
                        url: api.endpoint + 'GetUserByUsernameRequest/' + username,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.user = response.data;
                            $scope.showUser = true;
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";

                    $scope.update = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/updateProfilePrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'UpdateUserRequest',
                                method: 'PUT',
                                data: $scope.user,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isUpdated) {
                                    $state.go('Profile.View');
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/updateProfileError.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    });
                                }
                            });
                        });
                    };
                }
            ])
            .directive('customPassword', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customPassword = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.registration.confirmPassword && modelValue !== $scope.registration.confirmPassword) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customPassword2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customPassword2 = function (modelValue) {
                            //true or false based on custom dir validation
                            if ($scope.registration.password && modelValue !== $scope.registration.password) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customPostal', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customPostal = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue.length !== 6 || modelValue % 1 !== 0) {
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
            .directive('customContact', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customContact = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue.length !== 8 || modelValue % 1 !== 0) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            });
})();
