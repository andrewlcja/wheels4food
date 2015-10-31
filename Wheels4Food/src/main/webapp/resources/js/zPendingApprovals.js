(function () {
    'use strict';
    angular
            .module('Wheels4Food.PendingApprovals', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('PendingApprovals', {
                                url: '/Wheels4Food/PendingApprovals',
                                templateUrl: 'Wheels4Food/pendingApprovals.html',
                                controller: 'PendingApprovalsCtrl'
                            });
                }
            ]);
})();