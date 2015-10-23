(function () {
    'use strict';
    angular
            .module('Wheels4Food.UserManagement', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('UserManagement', {
                                url: '/Wheels4Food/UserManagement',
                                templateUrl: 'Wheels4Food/userManagement.html',
                                controller: 'UserManagementCtrl'
                            });
                }
            ]);
})();