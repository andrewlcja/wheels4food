(function () {
    'use strict';
    angular
            .module('Wheels4Food.Marketplace')
            .controller('ViewMarketplaceCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$filter', '$stateParams',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $filter, $stateParams) {
                    var authData = localStorageService.get('authorizationData');
                    $scope.userID = authData.userID;

                    //default sort
                    $scope.sortType = 'itemName';

                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};
                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            proccessed['itemName'] = input;
                        }

                        return proccessed;
                    };

                    //retrieve details
                    $scope.getObj = function (component, column) {
                        if (column.indexOf('.') === -1) {
                            return component[column];
                        }

                        var columnPath = column.split(".");
                        var obj = component;
                        for (var y = 0; y < columnPath.length; y++) {
                            if (!obj[columnPath[y]]) {
                                return '';
                            }
                            obj = obj[columnPath[y]];
                        }

                        return obj;
                    };

                    $scope.sort = function (sortType) {
                        $scope.sortType = sortType;
                        $scope.sortReverse = !$scope.sortReverse;
                    };

                    $scope.sortBy = function (supply) {
                        if ($scope.sortType === 'organizationName') {
                            return supply['user']['organizationName'];
                        } else if ($scope.sortType === 'expiryDate') {
                            if (supply.expiryDate === 'NA') {
                                return new Date('1000', '01', '01')
                            }

                            var parts = supply.expiryDate.split('/');
                            var date = new Date(parseInt(parts[2]), parseInt(parts[1]), parseInt(parts[0]));
                            return date;
                        }
                        return supply[$scope.sortType];
                    };

                    $scope.requestSupplyList = [];
                    $scope.supplierName = "";

                    $scope.toggleSupply = function (supply) {
                        if (supply.checked) {
                            $scope.supplierName = supply.user.organizationName;
                            $scope.requestSupplyList.push(supply);
                        } else {
                            $scope.requestSupplyList.splice($scope.requestSupplyList.indexOf(supply), 1)

                            if ($scope.requestSupplyList.length === 0) {
                                $scope.supplierName = "";
                            }
                        }
                    };

                    $scope.clearAll = function () {
                        for (var i = 0; i < $scope.requestSupplyList.length; i++) {
                            $scope.requestSupplyList[i].checked = false;
                        }

                        $scope.requestSupplyList = [];
                        $scope.supplierName = "";
                    };

                    $scope.goToRequestPage = function () {
                        $scope.showRequestPage = true;
                    };

                    $scope.removeSupply = function (supply) {
                        $scope.requestSupplyList.splice($scope.requestSupplyList.indexOf(supply), 1);
                    };

                    $scope.back = function () {
                        $state.go($state.current, $stateParams, {reload: true, inherit: false});
                    };

                    //set up user table columns
                    $scope.tableColumns = ['itemName', 'category', 'user.organizationName', 'quantitySupplied', 'expiryDate'];

                    var indexPromise;

                    if ($state.current.url === '/All') {
                        indexPromise = $http({
                            url: api.endpoint + 'GetSupplyListRequest',
                            method: 'GET'
                        });
                    } else {
                        indexPromise = $http({
                            url: api.endpoint + 'GetSupplyListByCategoryRequest' + $state.current.url,
                            method: 'GET'
                        });
                    }

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.supplyList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;

                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['itemName']);
                            });
                        });
                    }, 1000);

                    $scope.scheduleAMList = [];
                    $scope.schedulePMList = [];

                    for (var i = 0; i < 10; i++) {
                        $scope.scheduleAMList.push({'value': false});
                        $scope.schedulePMList.push({'value': false});
                    }

                    var today = new Date();
                    today.setDate(today.getDate() + 3);

                    $scope.dates = [];

                    for (var i = 0; i < 10; i++) {
                        if (today.getDay() !== 0 && today.getDay() !== 6) {
                            $scope.dates.push({'value': new Date(today)});
                        } else {
                            i--;
                        }


                        today.setDate(today.getDate() + 1);
                    }

                    $scope.scheduleCount = 0;

                    $scope.selectSlot = function (value) {
                        if (value) {
                            $scope.scheduleCount++;
                        } else {
                            $scope.scheduleCount--;
                        }
                    };

                    $scope.showSelfCollect = function () {
                        $scope.selfCollectOptions = true;
                        $scope.volunteerOptions = false;
                    };

                    $scope.showVolunteer = function () {
                        $scope.selfCollectOptions = false;
                        $scope.volunteerOptions = true;
                    };

                    $scope.clear = function () {
                        $scope.deliveryDate = null;
                    };

                    var today = new Date();
                    $scope.minDate = today.setDate(today.getDate() + 3);

                    $scope.open = function ($event) {
                        $event.preventDefault();
                        $event.stopPropagation();

                        $scope.opened = true;
                    };

                    $scope.dateOptions = {
                        formatYear: 'yy',
                        startingDay: 1
                    };

                    // Disable weekend selection
                    $scope.disabled = function (date, mode) {
                        return (mode === 'day' && (date.getDay() === 0 || date.getDay() === 6));
                    };

                    $scope.format = 'dd/MM/yyyy';

                    $scope.$watch('deliveryDate', function (newValue, oldValue) {
                        if (newValue && newValue !== '') {
                            $scope.deliveryDateFinal = $filter('date')($scope.deliveryDate, 'dd/MM/yyyy');

                            $http({
                                url: api.endpoint + 'GetUnavailableTimeslotsByDeliveryDateRequest',
                                method: 'POST',
                                data: {
                                    'supplierID': $scope.requestSupplyList[0].user.id,
                                    'deliveryDate': $scope.deliveryDateFinal
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                $scope.unavailableTimeslots = response.data;
                                $scope.showTimeslots = true;
                            });
                        }
                    });

                    $scope.request = function () {
                        var supplyIDValues = '';
                        var quantityDemandedValues = ''

                        for (var i = 0; i < $scope.requestSupplyList.length; i++) {
                            if (i !== $scope.requestSupplyList.length - 1) {
                                supplyIDValues += $scope.requestSupplyList[i].id + ',';
                                quantityDemandedValues += $scope.requestSupplyList[i].quantityDemanded + ',';
                            } else {
                                supplyIDValues += $scope.requestSupplyList[i].id;
                                quantityDemandedValues += $scope.requestSupplyList[i].quantityDemanded;
                            }
                        }

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/requestSupplyPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function () {
                            var preferredDeliveryDate = "NA";
                            var preferredTimeslot = "NA";
                            var preferredSchedule = "NA";

                            if ($scope.deliveryType === 'selfCollect') {
                                preferredDeliveryDate = $scope.deliveryDateFinal;
                                preferredTimeslot = $scope.timeslot;
                            } else {
                                var combinedSchedule = '';

                                for (var i = 0; i < 10; i++) {
                                    if ($scope.scheduleAMList[i].value) {
                                        combinedSchedule += '1';
                                    } else {
                                        combinedSchedule += '0';
                                    }

                                    if ($scope.schedulePMList[i].value) {
                                        combinedSchedule += '1';
                                    } else {
                                        combinedSchedule += '0';
                                    }
                                }

                                preferredSchedule = combinedSchedule;
                            }

                            indexPromise = $http({
                                url: api.endpoint + 'CreateDemandRequest',
                                method: 'POST',
                                data: {
                                    'userID': $scope.userID,
                                    'supplyIDValues': supplyIDValues,
                                    'quantityDemandedValues': quantityDemandedValues,
                                    'preferredDeliveryDate': preferredDeliveryDate,
                                    'preferredTimeslot': preferredTimeslot,
                                    'preferredSchedule': preferredSchedule
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            });

                            $scope.promise = [indexPromise];

                            $timeout(function () {
                                indexPromise.then(function (response) {
                                    if (response.data.isCreated) {
                                        ngDialog.openConfirm({
                                            template: '/Wheels4Food/resources/ngTemplates/requestSupplySuccess.html',
                                            className: 'ngdialog-theme-default dialog-generic',
                                            scope: $scope
                                        }).then(function (response) {
                                            $state.go('Inventory.Demand');
                                        });
                                    } else {
                                        $scope.errorList = response.data.errorList;

                                        ngDialog.openConfirm({
                                            template: '/Wheels4Food/resources/ngTemplates/requestSupplyError.html',
                                            className: 'ngdialog-theme-default dialog-generic',
                                            scope: $scope
                                        });
                                    }
                                });
                            }, 1000);
                        });
                    };

                    //cgBusy configuration
                    $scope.delay = 1;
                    $scope.minDuration = 820;
                    $scope.message = 'Please Wait...';
                    $scope.backdrop = true;
                    $scope.promise = [indexPromise];
                    $scope.templateUrl = "/Wheels4Food/resources/ngTemplates/cgBusy.html";
                }
            ])
            .directive('customNumber', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customNumber = function (modelValue) {
                            //true or false based on custom dir validation
                            if (modelValue % 1 !== 0) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            })
            .directive('customDate', function () {
                return {
                    restrict: 'A',
                    require: 'ngModel',
                    link: function ($scope, $element, $attrs, ngModel) {
                        ngModel.$validators.customDate = function (modelValue) {
                            //true or false based on custom dir validation
                            if (!modelValue) {
                                return false;
                            }

                            return true;
                        };
                    }
                };
            });
})();
