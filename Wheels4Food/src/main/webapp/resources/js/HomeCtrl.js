(function () {
    'use strict';
    angular
            .module('Wheels4Food.Home')
            .controller('HomeCtrl', ['$scope', '$state', '$http', 'api', 'localStorageService',
                function ($scope, $state, $http, api, localStorageService) {
                    var authData = localStorageService.get('authorizationData');
                    $scope.role = authData.role;

                    if (authData.role === 'Volunteer') {
                        $scope.displayName = authData.pocName;
                    } else {
                        $scope.displayName = authData.organizationName;
                    }
                }
            ]);
})();
