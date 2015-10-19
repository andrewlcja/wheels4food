(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('LayoutCtrl', ['$scope', '$state', '$http', 'api', 'localStorageService', '$location',
                function ($scope, $state, $http, api, localStorageService, $location) {
                    var authenticate = function () {
                        var authData = localStorageService.get('authorizationData');

                        if (authData === null) {
                            if (!$state.is('Register')) {
                                $scope.$parent.isLoggedIn = false;
                                $location.path('/Login');
                            }
                        } else {
                            $scope.$parent.isLoggedIn = true;
                            $scope.username = authData.username;
                            $scope.role = authData.role;
                        }
                    }

                    $scope.logout = function () {
                        localStorageService.remove('authorizationData');
                        $scope.$parent.isLoggedIn = false;
                        $location.path('/Login');
                    }

                    //everytime user refreshes page
                    //authenticate();

                    $scope.$on('$stateChangeSuccess', function () {
                        if (!$state.is('Register')) {
                            authenticate();
                        }
                    });
                }
            ]);
})();
