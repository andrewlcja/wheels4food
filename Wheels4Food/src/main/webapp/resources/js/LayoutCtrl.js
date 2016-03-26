(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('LayoutCtrl', ['$scope', '$state', '$http', 'api', 'localStorageService', '$location', 'ngDialog', '$stateParams',
                function ($scope, $state, $http, api, localStorageService, $location, ngDialog, $stateParams) {
                    var authenticate = function () {
                        var authData = localStorageService.get('authorizationData');
                        $scope.role = authData.role;
                        $scope.userID = authData.userID;
                        
                        if (authData === null) {
                            if (!$state.is('Register') || !$state.is('ResetPassword') || !$state.is('Reset')) {
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
                    };

                    $scope.logout = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/logoutPrompt.html',
                            className: 'ngdialog-theme-default dialog-logout-prompt',
                            scope: $scope
                        }).then(function (response) {
                            $scope.notificationList = [];
                            localStorageService.remove('authorizationData');
                            $scope.$parent.isLoggedIn = false;
                            $location.path('/Login');
                        });
                    };
                    
                    $scope.goToInventory = function () {
                        if ($scope.role === 'Supplier') {
                            $state.go('Inventory.Supply');
                        } else {
                            $state.go('Inventory.Demand');
                        }
                    };

                    $scope.goToState = function (notification) {
                        var index = $scope.notificationList.indexOf(notification);

                        $http({
                            url: api.endpoint + 'DeleteNotificationRequest/' + notification.id,
                            method: 'DELETE',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }).then(function (response) {
                            if (response.data.isDeleted) {
                                $scope.notificationList.splice(index, 1);
                                $state.go(notification.state, $stateParams, {reload: true, inherit: false});
                            }
                        });
                    };

                    $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                        $scope.currentState = toState.name;

                        if (!$state.is('Register') && !$state.is('ResetPassword') && !$state.is('Reset') && !$state.is('Login')) {
                            authenticate();

                            $http({
                                url: api.endpoint + 'GetNotificationListByUserIdRequest/' + $scope.userID,
                                method: 'GET'
                            }).then(function (response) {
                                $scope.notificationList = response.data.reverse();
                            });
                        }
                    });
                }
            ]);
})();
