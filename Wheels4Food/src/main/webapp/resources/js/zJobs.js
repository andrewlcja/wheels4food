(function () {
    'use strict';
    angular
            .module('Wheels4Food.Jobs', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Jobs', {
                                url: '/Wheels4Food/Jobs',
                                templateUrl: 'Wheels4Food/jobs.html',
                                controller: 'JobsCtrl'
                            });
                }
            ]);
})();