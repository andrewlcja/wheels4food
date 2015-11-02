(function () {
    'use strict';
    angular
            .module('Wheels4Food.Marketplace')
            .controller('MarketplaceRequestSupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;
                    $scope.quantityRequested = '';

                    $scope.request = function () {;
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/requestSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'CreateDemandRequest',
                                method: 'POST',
                                data: {
                                    'userID': userID,
                                    'supplyID': $stateParams.Id,
                                    'quantityDemanded': $scope.quantityRequested
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                               if (response.data.isCreated) {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/requestSupplySuccess.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    }).then(function (response) {
                                        $state.go('Inventory.Demand');
                                    });
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/requestSupplyError.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    });
                                }
                            });
                        });
                    };

                    var indexPromise = $http({
                        url: api.endpoint + 'GetSupplyByIdRequest/' + $stateParams.Id,
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        }
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.supply = response.data;                            
                            $scope.showSupply = true;
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
