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

                    $scope.years = [];

                    for (var i = 2015; i <= new Date().getFullYear(); i++) {
                        $scope.years.push(i);
                    }

                    $scope.months = [
                        {
                            'name': 'January',
                            'value': 1
                        },
                        {
                            'name': 'Febuary',
                            'value': 2
                        },
                        {
                            'name': 'March',
                            'value': 3
                        },
                        {
                            'name': 'April',
                            'value': 4
                        },
                        {
                            'name': 'May',
                            'value': 5
                        },
                        {
                            'name': 'June',
                            'value': 6
                        },
                        {
                            'name': 'July',
                            'value': 7
                        },
                        {
                            'name': 'August',
                            'value': 8
                        },
                        {
                            'name': 'September',
                            'value': 9
                        },
                        {
                            'name': 'October',
                            'value': 10
                        },
                        {
                            'name': 'November',
                            'value': 11
                        },
                        {
                            'name': 'December',
                            'value': 12
                        }
                    ];

                    //default values
                    $scope.currentYear = new Date().getFullYear();
                    $scope.currentMonth = new Date().getMonth() + 1;
                    $scope.selectedStartMonth = $scope.months[$scope.currentMonth];
                    $scope.selectedEndMonth = $scope.months[$scope.currentMonth];

                    $scope.updateStart = function () {
                        if ($scope.selectedStartMonth.value > $scope.selectedEndMonth.value) {
                            $scope.selectedEndMonth = $scope.months[$scope.selectedStartMonth.value - 1];
                        }
                    };

                    $scope.updateEnd = function () {
                        if ($scope.selectedEndMonth.value < $scope.selectedStartMonth.value) {
                            $scope.selectedStartMonth = $scope.months[$scope.selectedEndMonth.value - 1];
                        }
                    };

                    var indexPromiseDemand = $http({
                        url: api.endpoint + 'GetDemandBreakdownRequestByDate',
                        method: 'POST',
                        data: {
                            'id': authData.userID,
                            'startMonth': $scope.currentMonth,
                            'endMonth': $scope.currentMonth,
                            'year': $scope.currentYear
                        },
                        headers: {
                            'Content-Type': 'application/json',
                        }
                    });

                    var indexPromiseJob = $http({
                        url: api.endpoint + 'GetJobBreakdownBySupplierIdAndDateRequest',
                        method: 'POST',
                        data: {
                            'id': authData.userID,
                            'startMonth': $scope.currentMonth,
                            'endMonth': $scope.currentMonth,
                            'year': $scope.currentYear
                        },
                        headers: {
                            'Content-Type': 'application/json',
                        }
                    });

                    var indexPromiseSelfCollection = $http({
                        url: api.endpoint + 'GetSelfCollectionBreakdownBySupplierIdAndDateRequest',
                        method: 'POST',
                        data: {
                            'id': authData.userID,
                            'startMonth': $scope.currentMonth,
                            'endMonth': $scope.currentMonth,
                            'year': $scope.currentYear
                        },
                        headers: {
                            'Content-Type': 'application/json',
                        }
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
                            $scope.demandData = [[response.data.pending, response.data.rejected, response.data.approved]];
                            $scope.demandColours = [{fillColor: ["#ffc04c", "#ff4040", "#66cdaa"]}];
                        });

                        indexPromiseJob.then(function (response) {
                            if (response.data.pending === 0 && response.data.accepted === 0 && response.data.cancelled === 0 && response.data.completed === 0) {
                                $scope.hideJobChart = true;
                            }

                            $scope.jobLabels = ["Pending", "Accepted", "Cancelled", "Completed"];
                            $scope.jobData = [[response.data.pending, response.data.accepted, response.data.cancelled, response.data.completed]];
                            $scope.jobColours = [{fillColor: ["#ffc04c", "#31698a", "#ff4040", "#66cdaa"]}];
                        });

                        indexPromiseSelfCollection.then(function (response) {
                            if (response.data.pending === 0 && response.data.cancelled === 0 && response.data.completed === 0) {
                                $scope.hideselfCollectionChart = true;
                            }

                            $scope.selfCollectionLabels = ["Pending", "Cancelled", "Completed"];
                            $scope.selfCollectionData = [[response.data.pending, response.data.cancelled, response.data.completed]];
                            $scope.selfCollectionColours = [{fillColor: ["#ffc04c", "#ff4040", "#66cdaa"]}];
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

                    $scope.apply = function () {
                        indexPromiseDemand = $http({
                            url: api.endpoint + 'GetDemandBreakdownRequestByDate',
                            method: 'POST',
                            data: {
                                'id': authData.userID,
                                'startMonth': $scope.selectedStartMonth.value,
                                'endMonth': $scope.selectedEndMonth.value,
                                'year': $scope.selectedYear
                            },
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        });

                        indexPromiseJob = $http({
                            url: api.endpoint + 'GetJobBreakdownBySupplierIdAndDateRequest',
                            method: 'POST',
                            data: {
                                'id': authData.userID,
                                'startMonth': $scope.selectedStartMonth.value,
                                'endMonth': $scope.selectedEndMonth.value,
                                'year': $scope.selectedYear
                            },
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        });

                        indexPromiseSelfCollection = $http({
                            url: api.endpoint + 'GetSelfCollectionBreakdownBySupplierIdAndDateRequest',
                            method: 'POST',
                            data: {
                                'id': authData.userID,
                                'startMonth': $scope.selectedStartMonth.value,
                                'endMonth': $scope.selectedEndMonth.value,
                                'year': $scope.selectedYear
                            },
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        });
                        $scope.promiseDemand = [indexPromiseDemand];
                        $scope.promise = [indexPromiseJob];
                        $scope.promiseSelfCollection = [indexPromiseSelfCollection];

                            indexPromiseDemand.then(function (response) {
                                if (response.data.pending === 0 && response.data.approved === 0 && response.data.rejected === 0) {
                                    $scope.hideDemandChart = true;
                                } else {
                                    $scope.hideDemandChart = false;
                                }

                                $scope.demandLabels = ["Pending", "Rejected", "Approved"];
                                $scope.demandData = [[response.data.pending, response.data.rejected, response.data.approved]];
                                $scope.demandColours = [{fillColor: ["#ffc04c", "#ff4040", "#66cdaa"]}];
                            });

                            indexPromiseJob.then(function (response) {
                                if (response.data.pending === 0 && response.data.accepted === 0 && response.data.cancelled === 0 && response.data.completed === 0) {
                                    $scope.hideJobChart = true;
                                } else {
                                    $scope.hideJobChart = false;
                                }

                                $scope.jobLabels = ["Pending", "Accepted", "Cancelled", "Completed"];
                                $scope.jobData = [[response.data.pending, response.data.accepted, response.data.cancelled, response.data.completed]];
                                $scope.jobColours = [{fillColor: ["#ffc04c", "#31698a", "#ff4040", "#66cdaa"]}];
                            });

                            indexPromiseSelfCollection.then(function (response) {
                                if (response.data.pending === 0 && response.data.cancelled === 0 && response.data.completed === 0) {
                                    $scope.hideselfCollectionChart = true;
                                } else {
                                    $scope.hideselfCollectionChart = false;
                                }

                                $scope.selfCollectionLabels = ["Pending", "Cancelled", "Completed"];
                                $scope.selfCollectionData = [[response.data.pending, response.data.cancelled, response.data.completed]];
                                $scope.selfCollectionColours = [{fillColor: ["#ffc04c", "#ff4040", "#66cdaa"]}];
                            });

                        
                    };

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
