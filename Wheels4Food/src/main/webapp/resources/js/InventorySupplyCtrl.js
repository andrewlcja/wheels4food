(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventorySupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    //default sort
                    $scope.sortType = 'itemName';

                    $scope.sort = function (sortType) {
                        $scope.sortType = sortType;
                        $scope.sortReverse = !$scope.sortReverse;
                    };

                    $scope.sortBy = function (supply) {
                        if ($scope.sortType === 'organizationName') {
                            return supply['user']['organizationName'];
                        } else if ($scope.sortType === 'expiryDate') {
                            if (supply.expiryDate === 'NA') {
                                return new Date('1000', '01', '01')
                            }
                            
                            var parts = supply.expiryDate.split('/');
                            var date = new Date(parseInt(parts[2]), parseInt(parts[1]), parseInt(parts[0]));
                            return date;
                        }
                        return supply[$scope.sortType];
                    };

                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};
                        var tempArray;
                        if (input === null || input === undefined) {
                            proccessed = null;
                        } else {
                            proccessed['itemName'] = input;
                        }

                        return proccessed;
                    };

                    $scope.view = function (supply) {
                        $scope.currentSupply = supply;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/viewSupplyDetails.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        });
                    };

                    $scope.viewRequests = function (id) {
                        $state.go('Inventory.ViewRequests', {Id: id});
                    };

                    $scope.edit = function (id) {
                        $state.go('Inventory.EditSupply', {Id: id});
                    };

                    $scope.delete = function (supply) {
                        $scope.currentSupply = supply;
                        var index = $scope.supplyList.indexOf(supply);
                        
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/deleteSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'DeleteSupplyRequest/' + supply.id,
                                method: 'DELETE',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isDeleted) {
                                    $scope.supplyList.splice(index, 1);
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/deleteSupplyError.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    });
                                }
                            });
                        });
                    };

                    //retrieve details
                    $scope.getObj = function (component, column) {                        
                        return component[column];
                    };

                    //set up user table columns
                    $scope.tableColumns = ['itemName', 'category', 'quantitySupplied', 'quantityRemaining', 'expiryDate'];

                    var indexPromise = $http({
                        url: api.endpoint + 'GetSupplyListByUserIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.supplyList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;

                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['itemName']);
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
