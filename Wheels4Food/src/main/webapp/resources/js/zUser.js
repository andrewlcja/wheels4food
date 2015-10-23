(function () {
    'use strict';
    angular
            .module('Wheels4Food.User', [])
            .config(['$stateProvider',
                function ($stateProvider) {
                    $stateProvider
                            .state('Profile', {
                                url: '/Wheels4Food/Profile',
                                templateUrl: 'Wheels4Food/profile.html'
                            })
                            .state('Profile.View', {
                                url: '/View',
                                templateUrl: 'Wheels4Food/viewProfile.html',
                                controller: 'ViewProfileCtrl'
                            })
                            .state('Profile.Edit', {
                                url: '/Edit',
                                templateUrl: 'Wheels4Food/editProfile.html',
                                controller: 'EditProfileCtrl'
                            })
                            .state('ChangePassword', {
                                url: '/Wheels4Food/ChangePassword',
                                templateUrl: 'Wheels4Food/changePassword.html',
                                controller: 'ChangePasswordCtrl'
                            });
                }
            ]);
})();