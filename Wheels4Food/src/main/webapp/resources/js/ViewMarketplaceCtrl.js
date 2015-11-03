(function () {
    'use strict';
    angular
            .module('Wheels4Food.Marketplace')
            .controller('ViewMarketplaceCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService) {
                    var authData = localStorageService.get('authorizationData');
                    $scope.userID = authData.userID;

                    //default sort
                    $scope.sortType = 'itemName';

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

                    //retrieve details
                    $scope.getObj = function (component, column) {
                        return component[column];
                    };

                    $scope.request = function (id) {
                        $state.go('Marketplace.RequestSupply', {Id: id});
                    };

                    $scope.sort = function (sortType) {
                        $scope.sortType = sortType;
                        $scope.sortReverse = !$scope.sortReverse;
                    };

                    $scope.sortBy = function (supply) {
                        if ($scope.sortType === 'organizationName') {
                            return supply['user']['organizationName'];
                        } else if ($scope.sortType === 'expiryDate') {
                            var parts = supply.expiryDate.split('/');
                            var date = new Date(parseInt(parts[2]), parseInt(parts[1]), parseInt(parts[0]));
                            return date;
                        }
                        return supply[$scope.sortType];
                    };

                    //set up user table columns
                    $scope.tableColumns = ['itemName', 'category', 'user.organizationName', 'quantityRemaining', 'expiryDate'];

                    var indexPromise;

                    if ($state.current.url === '/All') {
                        indexPromise = $http({
                            url: api.endpoint + 'GetSupplyListRequest',
                            method: 'GET'
                        });
                    } else {
                        indexPromise = $http({
                            url: api.endpoint + 'GetSupplyListByCategoryRequest' + $state.current.url,
                            method: 'GET'
                        });
                    }

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
