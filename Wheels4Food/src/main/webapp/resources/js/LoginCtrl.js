(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('LoginCtrl', ['$scope', '$state', '$http', 'api', 'localStorageService', '$timeout', 'ngDialog', 'config',
                function ($scope, $state, $http, api, localStorageService, $timeout, ngDialog, config) {
                    $scope.$parent.isLoggedIn = false;
                    $scope.signInText = 'Sign In';
                    $scope.isLogging = false;
                    $scope.config = config;
                    $scope.username = '';
                    $scope.password = '';

                    $scope.login = function () {
                        $scope.signInText = 'Authenticating';
                        $scope.isLogging = true;

                        $http({
                            url: api.endpoint + 'UserLoginRequest',
                            method: 'POST',
                            data: {
                                'username': $scope.username,
                                'password': $scope.password
                            },
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }).then(function (response) {
                            $timeout(function () {
                                $scope.isLogging = false;

                                if (response.data.isAuthenicated) {
                                    localStorageService.set('authorizationData', {
                                        userID: response.data.user.id,
                                        username: response.data.user.username,
                                        role: response.data.user.role,
                                        organizationName: response.data.user.organizationName,
                                        pocName: response.data.user.pocName
                                    });

                                    $state.go('Home');
                                    $scope.$parent.isLoggedIn = true;
                                } else {
                                    $scope.signInText = 'Sign In';
                                    
                                    if (response.data.error === 'Account suspended') {
                                        ngDialog.openConfirm({
                                            template: '/Wheels4Food/resources/ngTemplates/accountSuspendedPrompt.html',
                                            className: 'ngdialog-theme-default dialog-generic',
                                            scope: $scope
                                        });
                                    } else {
                                        $scope.error = response.data.error;
                                        $scope.loginFailed = true;
                                    }
                                }
                            }, 800);
                        }, function (error) {
                            console.log(error);
                        });
                    };
                }
            ]);
})();
