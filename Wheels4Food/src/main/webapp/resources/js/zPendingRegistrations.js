(function () {
    'use strict';
    angular
            .module('Wheels4Food.PendingRegistrations', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('PendingRegistrations', {
                                url: '/Wheels4Food/PendingRegistrations',
                                templateUrl: 'Wheels4Food/pendingRegistrations.html',
                                controller: 'PendingRegistrationsCtrl'
                            });
                }
            ]);
})();