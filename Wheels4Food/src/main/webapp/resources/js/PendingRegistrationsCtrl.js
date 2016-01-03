(function () {
    'use strict';
    angular
            .module('Wheels4Food.PendingRegistrations')
            .controller('PendingRegistrationsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService) {
                    var authData = localStorageService.get('authorizationData');
                    
                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};
                        
                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            input = input.replace(/\s/g, '');
                            proccessed['username'] = input;

//                            tempArray = input.split(',');
//                            if (tempArray.length === 1) {
//                                return input;
//                            } else {
//                                for (var x = 0; x < sequenceArray.length; x++) {
//                                    proccessed[sequenceArray[x]] = tempArray[x];
//                                }
//                            }
                        }

                        return proccessed;
                    };

                    //retrieve details
                    $scope.getObj = function (component, column) {
                        var columnPath = column.split(".");
                        var obj = component;
                        for (var y = 0; y < columnPath.length; y++) {
                            if (!obj[columnPath[y]]) {
                                return '';
                            }
                            obj = obj[columnPath[y]];
                        }
                        return obj;
                    };
                    
                    $scope.view = function (pendingRegistration) {
                        $scope.currentRegistration = pendingRegistration;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/viewRegistrationDetails.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        });
                    };

                    $scope.approve = function (pendingRegistration, index) {
                        $scope.currentRegistration = pendingRegistration;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/approveRegistrationPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'ApprovePendingRegistrationRequest/' + pendingRegistration.id,
                                method: 'PUT',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                console.log(response);
                                if (response.data.isApproved) {                                    
                                    $scope.pendingRegistrationList.splice(($scope.currentPage - 1) * 10 + index, 1);
                                }
                            });
                        });
                    };
                    
                    $scope.delete = function (pendingRegistration, index) {
                        $scope.currentRegistration = pendingRegistration;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/deleteRegistrationPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'DeletePendingRegistrationRequest/' + pendingRegistration.id,
                                method: 'DELETE',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isDeleted) {                                    
                                    $scope.pendingRegistrationList.splice(($scope.currentPage - 1) * 10 + index, 1);
                                }
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['username', 'organizationName', 'role'];

                    var request = '';
                    if (authData.role === 'Admin') {
                        request = 'GetPendingRegistrationListByRoleRequest/VWO';
                    } else if (authData.role === 'VWO') {
                        request = 'GetVolunteerPendingRegistrationListByOrganizationRequest/' + authData.organizationName;
                    }

                    var indexPromise = $http({
                        url: api.endpoint + request,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.pendingRegistrationList = response.data;
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
