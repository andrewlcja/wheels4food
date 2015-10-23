(function () {
    'use strict';
    angular
            .module('Wheels4Food.User')
            .controller('ViewProfileCtrl', ['$scope', '$state', 'localStorageService', '$http', 'api', '$timeout',
                function ($scope, $state, localStorageService, $http, api, $timeout) {
                    var authData = localStorageService.get('authorizationData');
                    var username = authData.username;

                    var indexPromise = $http({
                        url: api.endpoint + 'GetUserByUsernameRequest/' + username,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.user = response.data;
                            $scope.showUser = true;
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";
                }
            ]);
})();
