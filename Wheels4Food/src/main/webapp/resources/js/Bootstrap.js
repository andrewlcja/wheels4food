(function () {
    'use strict';
    angular
            .module('Wheels4Food', [
                //resources
                'ui.router',
                'ui.bootstrap',
                'ngDialog',                
                'ngAnimate',
                'ngMessages',
                'LocalStorageModule',
                'angularUtils.directives.dirPagination',
                'cgBusy',
                
                //modules
                'Wheels4Food.Principal',
                'Wheels4Food.Home',
                'Wheels4Food.PendingRegistrations',
                'Wheels4Food.UserManagement',
                'Wheels4Food.User',
                'Wheels4Food.Inventory',
                'Wheels4Food.Marketplace',
                'Wheels4Food.PendingApprovals'
            ])

            .config(['$stateProvider', '$urlRouterProvider', '$locationProvider',
                function ($stateProvider, $urlRouterProvider, $locationProvider) {
                    //Default Route to Login
                    $urlRouterProvider.otherwise("/Wheels4Food/Login");
                    //Redirections

                    //$urlRouterProvider.when("/", 'Login');
                    $locationProvider.html5Mode(true);       
                }
            ])

            .controller('MainCtrl', ['$scope',
                function ($scope) {
                    $scope.isLoggedIn = false;
                }
            ])

            .provider('api', [
                function () {
                    var apiEndpoint = '';
                    return {
                        setAPIEndpoints: function (baseEndPoint) {
                            apiEndpoint = baseEndPoint + '/rest/';
                        },
                        getAPIEndpoint: function () {
                            return apiEndpoint;
                        },
                        $get: function () {
                            return {
                                endpoint: apiEndpoint
                            };
                        }
                    };
                }
            ])

            .config(['apiProvider',
                function (apiProvider) {
                    var hostname = window.location.hostname;
                    if (hostname === "localhost") {
                        apiProvider.setAPIEndpoints('http://localhost:8084/Wheels4Food');
                    } else {
                        apiProvider.setAPIEndpoints('http://apps.greentransformationlab.com/Wheels4Food');
                    }
                }
            ]);
})();


