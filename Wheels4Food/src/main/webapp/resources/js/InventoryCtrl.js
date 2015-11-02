(function () {
    'use strict';
    angular
            .module('Wheels4Food.Inventory')
            .controller('InventoryCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService) {
                    $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                        $scope.currentState = toState.name;
                    });
                }
            ]);
})();
