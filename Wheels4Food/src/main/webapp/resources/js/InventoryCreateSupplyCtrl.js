(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryCreateSupplyCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$filter',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $filter) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    $scope.supply = {
                        'userID': userID,
                        'itemName': '',
                        'category': '',
                        'quantitySupplied': '',
                        'minimum': '',
                        'maximum': '',
                        'expiryDate': ''
                    };

                    $scope.$watch('expiryDate', function (newValue, oldValue) {
                        if (newValue && newValue !== '') {
                            $scope.supply.expiryDate = $filter('date')($scope.expiryDate, 'dd/MM/yyyy');
                        }
                    });

                    $scope.create = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/createSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            $http({
                                url: api.endpoint + 'CreateSupplyRequest',
                                method: 'POST',
                                data: $scope.supply,
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isCreated) {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/createSupplySuccess.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    }).then(function (response) {
                                        $state.go('Inventory.Supply');
                                    });
                                } else {
                                    $scope.errorList = response.data.errorList;

                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/createSupplyError.html',
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
