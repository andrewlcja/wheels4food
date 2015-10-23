(function () {
    'use strict';
    angular
            .module('Wheels4Food.User')
            .controller('ChangePasswordCtrl', ['$scope', '$state', 'localStorageService', '$http', 'api', '$timeout', 'ngDialog', '$location',
                function ($scope, $state, localStorageService, $http, api, $timeout, ngDialog, $location) {
                    var authData = localStorageService.get('authorizationData');
                    var username = authData.username;
                    
                    $scope.oldPassword = '';
                    $scope.newPassword = '';
                    $scope.confirmNewPassword = '';

                    $scope.update = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/changePasswordPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic'
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'ChangePasswordRequest',
                                method: 'PUT',
                                data: {
                                    'username': username,
                                    'oldPassword': $scope.oldPassword,
                                    'newPassword': $scope.newPassword,
                                    'confirmNewPassword': $scope.confirmNewPassword
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {                                
                                if (response.data.isChanged) {
                                    localStorageService.remove('authorizationData');
                                    
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/changePasswordSuccess.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        showClose: false
                                    }).then(function () {                                        
                                        $scope.$parent.isLoggedIn = false;
                                        $location.path('/Login');
                                    });
                                } else {
                                    $scope.errorList = response.data.errorList;
                                    
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/changePasswordError.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    });
                                }
                            });
                        });
                    };
                }
            ]);
})();
