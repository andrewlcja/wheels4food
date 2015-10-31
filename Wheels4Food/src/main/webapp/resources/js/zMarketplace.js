(function () {
    'use strict';
    angular
            .module('Wheels4Food.Marketplace', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Marketplace', {
                                url: '/Wheels4Food/Marketplace',
                                templateUrl: 'Wheels4Food/marketplace.html'
                            })
                            .state('Marketplace.All', {
                                url: '/All',
                                templateUrl: 'Wheels4Food/marketplaceAll.html',
                                controller: 'MarketplaceAllCtrl'
                            })
                            .state('Marketplace.RequestSupply', {
                                url: '/Request/:Id',
                                templateUrl: 'Wheels4Food/marketplaceRequestSupply.html',
                                controller: 'MarketplaceRequestSupplyCtrl'
                            });
                }
            ]);
})();