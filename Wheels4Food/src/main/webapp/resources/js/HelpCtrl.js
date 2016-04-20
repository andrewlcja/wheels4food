(function () {
    'use strict';
    angular
            .module('Wheels4Food.Help')
            .controller('HelpCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams', '$filter',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams, $filter) {
                    var authData = localStorageService.get('authorizationData');
                    $scope.role = authData.role;
                }
            ]);
})();
