(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventorySupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

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

                    $scope.edit = function (id) {
                        $state.go('Inventory.EditSupply', {Id: id});
                    };

                    $scope.delete = function (supply, index) {
                        $scope.currentSupply = supply;

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
                                    $scope.supplyList.splice(($scope.currentPage - 1) * 10 + index, 1);
                                }
                            });
                        });
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
