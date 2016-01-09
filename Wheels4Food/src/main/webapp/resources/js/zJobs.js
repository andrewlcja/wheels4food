(function () {
    'use strict';
    angular
            .module('Wheels4Food.Jobs', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Jobs', {
                                url: '/Wheels4Food/Jobs',
                                templateUrl: 'Wheels4Food/jobs.html'
                            })
                            .state('MyJobs', {
                                url: '/Wheels4Food/MyJobs',
                                templateUrl: 'Wheels4Food/myJobs.html',
                                controller: 'MyJobsCtrl'
                            })
                            .state('Jobs.View', {
                                url: '/View',
                                templateUrl: 'Wheels4Food/viewJobs.html',
                                controller: 'ViewJobsCtrl'
                            })
                            .state('Jobs.Accept', {
                                url: '/Accept/:Id',
                                templateUrl: 'Wheels4Food/acceptJob.html',
                                controller: 'AcceptJobCtrl'
                            });
                }
            ]);
})();