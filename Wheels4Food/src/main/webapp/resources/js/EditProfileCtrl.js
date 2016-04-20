(function () {
    'use strict';
    angular
            .module('Wheels4Food.User')
            .controller('EditProfileCtrl', ['$scope', '$state', 'localStorageService', '$http', 'api', '$timeout', 'ngDialog',
                function ($scope, $state, localStorageService, $http, api, $timeout, ngDialog) {
                    var authData = localStorageService.get('authorizationData');
                    var username = authData.username;
                    
                    if (authData.role === 'Supplier' || authData.role === 'Requester' || authData.role === 'Admin') {
                        $scope.isVWO = true;
                    } else {
                        $scope.isVWO = false;
                    }

                    var indexPromise = $http({
                        url: api.endpoint + 'GetUserByUsernameRequest/' + username,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.user = response.data;
                            $scope.showUser = true;
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";

                    $scope.update = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/updateProfilePrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'UpdateUserRequest',
                                method: 'PUT',
                                data: $scope.user,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isUpdated) {
                                    $state.go('Profile.View');
                                } else {
                                    $scope.errorList = response.data.errorList;
                                    
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/updateProfileError.html',
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
