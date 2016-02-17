(function () {
    'use strict';
    angular
            .module('Wheels4Food.ApprovedRequests', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('ApprovedRequests', {
                                url: '/Wheels4Food/ApprovedRequests',
                                templateUrl: 'Wheels4Food/approvedRequests.html',
                                controller: 'ApprovedRequestsCtrl'
                            });
                }
            ]);
})();