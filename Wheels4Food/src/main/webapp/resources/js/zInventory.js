(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Inventory', {
                                url: '/Wheels4Food/Inventory',
                                templateUrl: 'Wheels4Food/inventory.html',
                                controller: 'InventoryCtrl'
                            })
                            .state('Inventory.Supply', {
                                url: '/Supply',
                                templateUrl: 'Wheels4Food/inventorySupply.html',
                                controller: 'InventorySupplyCtrl'
                            })
                            .state('Inventory.Demand', {
                                url: '/Demand',
                                templateUrl: 'Wheels4Food/inventoryDemand.html',
                                controller: 'InventoryDemandCtrl'
                            })
                            .state('Inventory.CreateSupply', {
                                url: '/Supply/Create',
                                templateUrl: 'Wheels4Food/createSupply.html',
                                controller: 'InventoryCreateSupplyCtrl'
                            })
                            .state('Inventory.EditSupply', {
                                url: '/Supply/Edit/:Id',
                                templateUrl: 'Wheels4Food/editSupply.html',
                                controller: 'InventoryEditSupplyCtrl'
                            })
                            .state('Inventory.ViewRequests', {
                                url: '/Supply/ViewRequests/:Id',
                                templateUrl: 'Wheels4Food/viewSupplyRequests.html',
                                controller: 'InventoryViewSupplyRequestsCtrl'
                            });
                }
            ]);
})();