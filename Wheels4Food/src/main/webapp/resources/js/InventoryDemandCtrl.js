(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryDemandCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
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
                            proccessed = {
                              'supply': {
                                  'itemName': input
                              }  
                            };
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
                    
                    $scope.view = function (demand) {
                        $scope.currentDemand = demand;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/viewDemandDetails.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        });
                    };
                    
                    $scope.cancel = function (demand, index) {
                        $scope.currentDemand = demand;

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/cancelDemandPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'DeleteDemandRequest/' + demand.id,
                                method: 'DELETE',
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                console.log(response.data);
                                if (response.data.isDeleted) {
                                    $scope.demandList.splice(($scope.currentPage - 1) * 10 + index, 1);
                                }
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['supply.itemName', 'supply.category', 'quantityDemanded', 'supply.expiryDate'];

                    var indexPromise = $http({
                        url: api.endpoint + 'GetDemandListByUserIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.demandList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;
                            
                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['supply.itemName']);
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
