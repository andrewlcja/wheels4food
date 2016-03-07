(function () {
    'use strict';
    angular
            .module('Wheels4Food.UserManagement')
            .controller('UserManagementCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams) {
                    var authData = localStorageService.get('authorizationData');
                    var role = authData.role;
                    
                    $scope.loggedInUsername = authData.username;
                    
                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};
                        
                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            input = input.replace(/\s/g, '');
                            proccessed['username'] = input;
                        }

                        return proccessed;
                    };

                    //retrieve details
                    $scope.getObj = function (component, column) {
                        return component[column];
                    };

                    $scope.view = function (user) {
                        $scope.currentUser = user;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/viewUserDetails.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        });
                    };
                    
                    $scope.suspend = function (user) {
                        $scope.currentUser = user;
                        
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/suspendUserPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'SuspendUserRequest/' + user.id,
                                method: 'PUT',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isSuspended) {
                                    $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                }
                            });
                        });
                    };
                    
                    $scope.activate = function (user) {
                        $scope.currentUser = user;
                        
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/activateUserPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'ActivateUserRequest/' + user.id,
                                method: 'PUT',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isActivated) {
                                    $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                }
                            });
                        });
                    };

                    $scope.delete = function (user, index) {
                        $scope.currentUser = user;
                        index = $scope.userList.indexOf(user);

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/deleteUserPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'DeleteUserRequest/' + user.id,
                                method: 'DELETE',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isDeleted) {
                                    $scope.userList.splice(index, 1);
                                }
                            });
                        });
                    };
                    
                    var request = 'GetUserListRequest';
                    if (role === 'Supplier') {
                        request = 'GetUserListByRoleRequest/Requester';
                    } else if (role === 'Requester') {
                        request = 'GetVolunteerListByOrganizationRequest/' + authData.organizationName;
                    }
                    

                    //set up user table columns
                    $scope.tableColumns = ['username', 'organizationName', 'role', 'demeritPoints', 'status'];

                    var indexPromise = $http({
                        url: api.endpoint + request,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.userList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;

                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['username']);
                            });
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";
                }
            ]);
})();
