(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('LoginCtrl', ['$scope', '$state', '$http', 'api', 'localStorageService', '$timeout',
                function ($scope, $state, $http, api, localStorageService, $timeout) {
                    $scope.$parent.isLoggedIn = false;
                    $scope.signInText = 'Sign In';
                    $scope.isLogging = false;

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
                                        role: response.data.user.role
                                    });

                                    $state.go('Home');
                                    $scope.$parent.isLoggedIn = true;
                                } else {
                                    $scope.signInText = 'Sign In';

                                    $scope.error = response.data.error;
                                    $scope.loginFailed = true;
                                }
                            }, 800);
                        }, function (error) {
                            console.log(error);
                        });
                    };
                }
            ]);
})();
