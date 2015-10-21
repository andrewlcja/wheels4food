(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal')
            .controller('RegisterCtrl', ['$scope', '$state', '$http', 'api', 'ngDialog',
                function ($scope, $state, $http, api, ngDialog) {
                    $scope.registration = {
                        'username': '',
                        'password': '',
                        'confirmPassword': '',
                        'organizationName': '', 
                        'email': '',
                        'address': '',
                        'postalCode': '',
                        'pocName': '',
                        'pocNumber': '',
                        'licenseNumber': '',
                        'role': ''
                    };

                    $scope.submit = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/registrationPrompt.html',
                            className: 'ngdialog-theme-default dialog-registration-prompt',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'CreatePendingRegistrationRequest',
                                method: 'POST',
                                data: $scope.registration,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isCreated) {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/registrationSuccess.html',
                                        className: 'ngdialog-theme-default dialog-registration-success',
                                        scope: $scope
                                    }).then(function () {
                                        $state.go('Home');
                                    });
                                } else {
                                    $scope.errorList = response.data.errorList;
                                    
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/registrationError.html',
                                        className: 'ngdialog-theme-default dialog-registration-error',
                                        scope: $scope
                                    });
                                }
                            });
                        });
                    };
                }
            ]);
})();
