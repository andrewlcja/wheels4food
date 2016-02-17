(function () {
    'use strict';
    angular
            .module('Wheels4Food.Jobs')
            .controller('AcceptJobCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', '$stateParams', '$filter', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, $stateParams, $filter, localStorageService) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;
                    var jobID = $stateParams.Id;

                    var indexPromise = $http({
                        url: api.endpoint + 'GetJobByIdRequest/' + jobID,
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json',
                        }
                    });

                    $scope.collectionTime = new Date();
                    $scope.deliveryTime = new Date();

                    $scope.showCollectionTimeError = true;
                    $scope.showDeliveryTimeError = true;

                    $scope.toggleSchedule = function (scheduleAM) {
                        scheduleAM.selected = !scheduleAM.selected;
                    };

                    $scope.accept = function (index, period) {
                        $scope.deliveryDate = $filter('date')($scope.dates[index].value, 'dd/MM/yyyy');
                        $scope.period = period;
                        $scope.showStep2 = true;
                    };

                    $scope.submit = function () {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/createSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            var collectionTime = $filter('date')($scope.collectionTime, 'HH:mm a');
                            var deliveryTime = $filter('date')($scope.deliveryTime, 'HH:mm a');

                            $http({
                                url: api.endpoint + 'AcceptJobRequest',
                                method: 'PUT',
                                data: {
                                    'jobID': jobID,
                                    'userID': userID,
                                    'collectionTime': collectionTime,
                                    'deliveryTime': deliveryTime
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isAccepted) {
                                    ngDialog.openConfirm({
                                        template: '/Wheels4Food/resources/ngTemplates/acceptJobSuccess.html',
                                        className: 'ngdialog-theme-default dialog-generic',
                                        scope: $scope
                                    }).then(function () {
                                        $state.go('MyJobs');
                                    });
                                }
                            });
                        });
                    };

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.job = response.data;

                            $http({
                                url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + $scope.job.demand.id,
                                method: 'GET'
                            }).then(function (response) {
                                $scope.demandItemList = response.data;
                                $scope.showJob = true;

                                $scope.collectionTime = new Date();
                                $scope.deliveryTime = new Date();
                                $scope.minTime = new Date();
                                $scope.maxTime = new Date();
                                
                                if ($scope.job.timeslot === '9AM-12PM') {
                                    $scope.collectionTime.setHours(9);
                                    $scope.collectionTime.setMinutes(0);
                                    $scope.deliveryTime.setHours(12);
                                    $scope.deliveryTime.setMinutes(0);
                                    
                                    $scope.minTime.setHours(9);
                                    $scope.minTime.setMinutes(0);
                                    $scope.maxTime.setHours(12);
                                    $scope.maxTime.setMinutes(0);
                                } else {
                                    $scope.collectionTime.setHours(14);
                                    $scope.collectionTime.setMinutes(0);
                                    $scope.deliveryTime.setHours(17);
                                    $scope.deliveryTime.setMinutes(0);
                                    
                                    $scope.minTime.setHours(14);
                                    $scope.minTime.setMinutes(0);
                                    $scope.maxTime.setHours(17);
                                    $scope.maxTime.setMinutes(0);
                                }
                            });
                        });
                    }, 1000);

                    //cgBusy configuration
                    $scope.delay = 0;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";
                }
            ])
            .directive('customTime', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customTime = function (modelValue) {
                            //true or false based on custom dir validation
                            var hour = modelValue.getHours();
                            var minute = modelValue.getMinutes();

                            if ($scope.period && $scope.period.indexOf('AM') !== -1) {
                                if (hour < 9 || (hour >= 12 && minute > 0)) {
                                    $scope.showCollectionTimeError = true;
                                } else {
                                    $scope.showCollectionTimeError = false;
                                }
                            } else {
                                if (hour < 14 || (hour >= 17 && minute > 0)) {
                                    $scope.showCollectionTimeError = true;
                                } else {
                                    $scope.showCollectionTimeError = false;
                                }
                            }

                            if (modelValue >= $scope.deliveryTime) {
                                $scope.showCollectionTimeError2 = true;
                            } else {
                                $scope.showCollectionTimeError2 = false;
                                $scope.showDeliveryTimeError2 = false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customTime2', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customTime2 = function (modelValue) {
                            //true or false based on custom dir validation
                            var hour = modelValue.getHours();
                            var minute = modelValue.getMinutes();

                            if ($scope.period && $scope.period.indexOf('AM') !== -1) {
                                if (hour < 9 || (hour >= 12 && minute > 0)) {
                                    $scope.showDeliveryTimeError = true;
                                } else {
                                    $scope.showDeliveryTimeError = false;
                                }
                            } else {
                                if (hour < 14 || (hour >= 17 && minute > 0)) {
                                    $scope.showDeliveryTimeError = true;
                                } else {
                                    $scope.showDeliveryTimeError = false;
                                }
                            }

                            if (modelValue <= $scope.collectionTime) {
                                $scope.showDeliveryTimeError2 = true;
                            } else {
                                $scope.showDeliveryTimeError2 = false;
                                $scope.showCollectionTimeError2 = false;
                            }

                            return true;
                        };
                    }
                };
            });
})();
