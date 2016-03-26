(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('ResetPasswordCtrl', ['$scope', '$state', '$http', 'api', 'ngDialog', '$timeout',
                function ($scope, $state, $http, api, ngDialog, $timeout) {
                    $scope.buttonText = 'Submit';
                    $scope.isLogging = false;

                    $scope.submit = function () {
                        $scope.buttonText = 'Submitting Request';
                        $scope.isLogging = true;

                        $http({
                            url: api.endpoint + 'CreatePendingResetPasswordRequest',
                            method: 'POST',
                            data: {
                                'email': $scope.email,
                                'endPoint': api.baseUrl
                            },
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }).then(function (response) {
                            $timeout(function () {
                                $scope.isLogging = false;

                                if (response.data.isCreated) {
                                    $scope.submitFailed = false;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/resetPasswordSuccess.html',
                                        className: 'ngdialog-theme-default dialog-registration-success',
                                        scope: $scope
                                    }).then(function () {
                                        $state.go('Login');
                                    });
                                } else {
                                    $scope.error = response.data.errorList[0];
                                    $scope.submitFailed = true;
                                }
                            }, 800);
                        }, function (error) {
                            console.log(error);
                        });
                    };
                }
            ]);
})();
