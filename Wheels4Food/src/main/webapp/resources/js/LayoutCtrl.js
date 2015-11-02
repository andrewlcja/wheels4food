(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('LayoutCtrl', ['$scope', '$state', '$http', 'api', 'localStorageService', '$location', 'ngDialog',
                function ($scope, $state, $http, api, localStorageService, $location, ngDialog) {
                    var authenticate = function () {
                        var authData = localStorageService.get('authorizationData');

                        if (authData === null) {
                            if (!$state.is('Register')) {
                                $scope.$parent.isLoggedIn = false;
                                $location.path('/Login');
                            }
                        } else {
                            if (!$state.is('Login')) {
                                $scope.$parent.isLoggedIn = true;
                                $scope.username = authData.username;
                                $scope.role = authData.role;
                            }
                        }
                    }

                    $scope.logout = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/logoutPrompt.html',
                            className: 'ngdialog-theme-default dialog-logout-prompt',
                            scope: $scope
                        }).then(function (response) {
                            localStorageService.remove('authorizationData');
                            $scope.$parent.isLoggedIn = false;
                            $location.path('/Login');
                        });
                    };

                    $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                        $scope.currentState = toState.name;
                        
                        if (!$state.is('Register')) {
                            authenticate();
                        }
                    });
                }
            ]);
})();
