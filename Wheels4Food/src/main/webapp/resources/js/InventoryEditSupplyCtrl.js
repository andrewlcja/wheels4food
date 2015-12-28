(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryEditSupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$filter', '$stateParams',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $filter, $stateParams) {
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
                            var parts = $scope.supply.expiryDate.split("/");
                            $scope.expiryDate = new Date(parseInt(parts[2], 10),
                                    parseInt(parts[1], 10) - 1,
                                    parseInt(parts[0], 10));
                            
                            $scope.showSupply = true;
                            
                            if ($scope.supply.expiryDate === 'NA') {
                                $scope.expiryDate = 'dd/mm/yyyy';
                                $scope.expiryDateNA = true;
                            }

                            $scope.$watch('expiryDate', function (newValue, oldValue) {
                                if (newValue !== '') {
                                    $scope.supply.expiryDate = $filter('date')($scope.expiryDate, 'dd/MM/yyyy');
                                }
                            });
                        });
                    }, 1000);

                    $scope.update = function () {
                        if ($scope.expiryDateNA) {
                            $scope.supply.expiryDate = 'NA';
                        }
                        
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/updateSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'UpdateSupplyRequest',
                                method: 'PUT',
                                data: $scope.supply,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isUpdated) {
                                    $state.go('Inventory.Supply');
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/updateSupplyError.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    });
                                }
                            });
                        });
                    };

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
