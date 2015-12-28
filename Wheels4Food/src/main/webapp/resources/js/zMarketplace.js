(function () {
    'use strict';
    angular
            .module('Wheels4Food.Marketplace', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Marketplace', {
                                url: '/Wheels4Food/Marketplace',
                                templateUrl: 'Wheels4Food/marketplace.html',
                                controller: 'MarketplaceCtrl'
                            })
                            .state('Marketplace.All', {
                                url: '/All',
                                templateUrl: 'Wheels4Food/viewMarketplace.html',
                                controller: 'ViewMarketplaceCtrl'
                            })
                            .state('Marketplace.Food', {
                                url: '/Food',
                                templateUrl: 'Wheels4Food/viewMarketplace.html',
                                controller: 'ViewMarketplaceCtrl'
                            })
                            .state('Marketplace.Drinks', {
                                url: '/Drinks',
                                templateUrl: 'Wheels4Food/viewMarketplace.html',
                                controller: 'ViewMarketplaceCtrl'
                            })
                            .state('Marketplace.Condiments', {
                                url: '/Condiments',
                                templateUrl: 'Wheels4Food/viewMarketplace.html',
                                controller: 'ViewMarketplaceCtrl'
                            })
                            .state('Marketplace.Toiletries', {
                                url: '/Toiletries',
                                templateUrl: 'Wheels4Food/viewMarketplace.html',
                                controller: 'ViewMarketplaceCtrl'
                            })
                            .state('Marketplace.Miscellaneous', {
                                url: '/Miscellaneous',
                                templateUrl: 'Wheels4Food/viewMarketplace.html',
                                controller: 'ViewMarketplaceCtrl'
                            })
                            .state('Marketplace.RequestSupply', {
                                url: '/Request/:Id',
                                templateUrl: 'Wheels4Food/marketplaceRequestSupply.html',
                                controller: 'MarketplaceRequestSupplyCtrl'
                            });
                }
            ]);
})();