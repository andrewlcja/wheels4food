(function () {
    'use strict';
    angular
            .module('Wheels4Food.Home', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Home', {
                                url: '/Wheels4Food/Home',
                                templateUrl: 'Wheels4Food/home.html',
                                controller: 'HomeCtrl'
                            });
                }
            ]);
})();