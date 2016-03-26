(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('ResetCtrl', ['$scope', '$state', '$http', 'api', 'ngDialog', '$timeout', '$stateParams',
                function ($scope, $state, $http, api, ngDialog, $timeout, $stateParams) {
                    var token = $stateParams.Token;
                    $scope.isVerified = false;

                    $scope.request = {
                        'username': '',
                        'newPassword': '',
                        'confirmNewPassword': ''
                    };

                    $http({
                        url: api.endpoint + 'VerifyResetPasswordTokenRequest/' + token,
                        method: 'GET'
                    }).then(function (response) {
                        if (response.data.isVerified) {
                            $scope.isVerified = true;
                        } else {
                            $scope.status = response.data.errorList[0];
                        }

                        $scope.showPage = true;
                    });

                    $scope.reset = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/resetPasswordPrompt.html',
                            className: 'ngdialog-theme-default dialog-registration-success',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'ResetPasswordRequest',
                                method: 'PUT',
                                data: $scope.request,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isReset) {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/resetPasswordVerifiedSuccess.html',
                                        className: 'ngdialog-theme-default dialog-registration-success',
                                        scope: $scope
                                    }).then(function () {
                                        $state.go('Login');
                                    });
                                } else {
                                    $scope.errorList = response.data.errorList;
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/resetPasswordVerifiedError.html',
                                        className: 'ngdialog-theme-default dialog-registration-success',
                                        scope: $scope
                                    }).then(function () {
                                        $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                    });
                                }
                            });
                        });
                    };
                }
            ]);
})();

