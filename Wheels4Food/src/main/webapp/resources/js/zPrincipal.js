(function () {
    'use strict';
    angular
            .module('Wheels4Food.Principal', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Login', {
                                url: '/Wheels4Food/Login',
                                templateUrl: '/Wheels4Food/login.html',
                                controller: 'LoginCtrl'
                            })
                            .state('Register', {
                                url: '/Wheels4Food/Register',
                                templateUrl: '/Wheels4Food/register.html',
                                controller: 'RegisterCtrl'
                            });
                }
            ]);
})();