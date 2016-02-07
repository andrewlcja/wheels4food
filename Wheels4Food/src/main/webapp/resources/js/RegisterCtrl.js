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
                        'description': '',
                        'role': ''
                    };

                    $scope.toggleVWO = function () {
                        $scope.VWOSelected = !$scope.VWOSelected;
                    };

                    $scope.toggleVolunteer = function () {
                        $scope.volunteerSelected = !$scope.volunteerSelected;
                    };

                    $scope.showVWOForm = function () {
                        $scope.selectionPanel = true;
                        $scope.VWOForm = true;
                        $scope.registration.role = 'VWO';
                        $scope.request = 'CreatePendingRegistrationRequest';
                    };

                    $scope.showVolunteerForm = function () {
                        $scope.selectionPanel = true;
                        $scope.volunteerForm = true;
                        $scope.registration.role = 'Volunteer';
                        $scope.request = 'CreateVolunteerPendingRegistrationRequest';
                        
                        $http({
                            url: api.endpoint + 'GetUserListByRoleRequest/VWO',
                            method: 'GET',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        }).then(function (response) {
                            $scope.VWOList = response.data;
                        });
                    };

                    $scope.submit = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/registrationPrompt.html',
                            className: 'ngdialog-theme-default dialog-registration-prompt',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + $scope.request,
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
                                        $state.go('Login');
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
