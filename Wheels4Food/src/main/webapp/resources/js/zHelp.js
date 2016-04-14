(function () {
    'use strict';
    angular
            .module('Wheels4Food.Help', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Help', {
                                url: '/Wheels4Food/Help',
                                templateUrl: 'Wheels4Food/help.html',
                                controller: 'HelpCtrl'
                            });
                }
            ]);
})();