(function () {
    'use strict';
    angular
            .module('Wheels4Food.Home')
            .controller('HomeCtrl', ['$scope', '$state', '$http', 'api', 'localStorageService', '$timeout',
                function ($scope, $state, $http, api, localStorageService, $timeout) {
                    var authData = localStorageService.get('authorizationData');
                    $scope.role = authData.role;

                    if (authData.role === 'Volunteer') {
                        $scope.displayName = authData.pocName;
                    } else {
                        $scope.displayName = authData.organizationName;
                    }

                    var indexPromiseDemand = $http({
                        url: api.endpoint + 'GetDemandBreakdownRequest/' + authData.userID,
                        method: 'GET'
                    });

                    var indexPromiseJob = $http({
                        url: api.endpoint + 'GetJobBreakdownBySupplierIdRequest/' + authData.userID,
                        method: 'GET'
                    });

                    var indexPromiseSelfCollection = $http({
                        url: api.endpoint + 'GetSelfCollectionBreakdownBySupplierIdRequest/' + authData.userID,
                        method: 'GET'
                    });

                    var indexPromiseSupply = $http({
                        url: api.endpoint + 'GetSupplyListByUserIdRequest/' + authData.userID,
                        method: 'GET'
                    });

//                    var indexPromiseMonetaryValue = $http({
//                        url: api.endpoint + 'GetCompletedDemandListBySupplierIdRequest/' + authData.userID,
//                        method: 'GET'
//                    });

                    $timeout(function () {
                        indexPromiseDemand.then(function (response) {
                            if (response.data.pending === 0 && response.data.approved === 0 && response.data.rejected === 0) {
                                $scope.hideDemandChart = true;
                            }

                            $scope.demandLabels = ["Pending", "Rejected", "Approved"];
                            $scope.demandData = [response.data.pending, response.data.rejected, response.data.approved];
                            $scope.demandColours = ["#ffc04c", "#ff4040", "#66cdaa"];
                        });

                        indexPromiseJob.then(function (response) {
                            if (response.data.pending === 0 && response.data.accepted === 0 && response.data.cancelled === 0 && response.data.completed === 0) {
                                $scope.hideJobChart = true;
                            }

                            $scope.jobLabels = ["Pending", "Accepted", "Cancelled", "Completed"];
                            $scope.jobData = [response.data.pending, response.data.accepted, response.data.cancelled, response.data.completed];
                            $scope.jobColours = ["#ffc04c", "#31698a", "#ff4040", "#66cdaa"];
                        });

                        indexPromiseSelfCollection.then(function (response) {
                            if (response.data.pending === 0 && response.data.cancelled === 0 && response.data.completed === 0) {
                                $scope.hideselfCollectionChart = true;
                            }

                            $scope.selfCollectionLabels = ["Pending", "Cancelled", "Completed"];
                            $scope.selfCollectionData = [response.data.pending, response.data.cancelled, response.data.completed];
                            $scope.selfCollectionColours = ["#ffc04c", "#ff4040", "#66cdaa"];
                        });

                        indexPromiseSupply.then(function (response) {
                            $scope.supplyCount = response.data.length;

                            $scope.supplyMonetaryValue = 0;

                            for (var i = 0; i < response.data.length; i++) {
                                var supply = response.data[i];

                                $scope.supplyMonetaryValue += supply.monetaryValue * supply.quantitySupplied;
                            }

                            $scope.showSupply = true;
                        });

//                        indexPromiseMonetaryValue.then(function (response) {
//                            $scope.totalMonetaryValueGiven = 0;
//
//                            for (var i = 0; i < response.data.length; i++) {
//                                var demand = response.data[i];
//
//                                $scope.totalMonetaryValueGiven += demand.supply.monetaryValue * demand.quantityDemanded;
//                            }
//
//                            $scope.showMonetaryValue = true;
//                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromiseJob];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";

                    $scope.promiseDemand = [indexPromiseDemand];
                    $scope.promiseSelfCollection = [indexPromiseSelfCollection];
                    $scope.promiseSupply = [indexPromiseSupply];
//                    $scope.promiseMonetaryValue = [indexPromiseMonetaryValue];
                }
            ]);
})();
