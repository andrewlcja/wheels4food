(function () {
    'use strict';
    angular
            .module('Wheels4Food.PendingApprovals')
            .controller('PendingApprovalsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService', '$stateParams', '$filter',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService, $stateParams, $filter) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    $scope.selectSlot = function (date, time) {
                        $scope.selectedDate = $filter('date')(date, 'dd/MM/yyyy');
                        $scope.selectedTimeslot = time;
                    };

                    $scope.timeslot = {
                        'choice': ''
                    }

                    //default sort
                    $scope.sortType = 'itemName';

                    $scope.sort = function (sortType) {
                        $scope.sortType = sortType;
                        $scope.sortReverse = !$scope.sortReverse;
                    };

                    $scope.sortBy = function (pendingApproval) {
                        if ($scope.sortType === 'organizationName') {
                            return pendingApproval['user']['organizationName'];
                        } else if ($scope.sortType === 'itemName') {
                            return pendingApproval['supply']['itemName'];
                        } else if ($scope.sortType === 'quantitySupplied') {
                            return pendingApproval['supply']['quantitySupplied'];
                        } else if ($scope.sortType === 'category') {
                            return pendingApproval['supply']['category'];
                        } else if ($scope.sortType === 'expiryDate') {
                            if (pendingApproval.expiryDate === 'NA') {
                                return new Date('1000', '01', '01')
                            }

                            var parts = pendingApproval.supply.expiryDate.split('/');
                            var date = new Date(parseInt(parts[2]), parseInt(parts[1]), parseInt(parts[0]));
                            return date;
                        }
                        return pendingApproval[$scope.sortType];
                    };

                    //setup searchFilter options
                    var parseSplitArray = function (input, sequenceArray) {
                        var proccessed = {};

                        if (input === null || input === undefined) {
                            proccessed = {};
                        } else {
                            proccessed = {
                                'user': {
                                    organizationName: input
                                }
                            };
                        }

                        return proccessed;
                    };

                    var validApproval = function (pendingApproval, index, comment) {
                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + pendingApproval.id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.currentDemandItemList = response.data;
                        });

                        var finalIndex = $scope.pendingApprovalList.indexOf(pendingApproval);

                        if (pendingApproval.preferredSchedule === 'NA') {
                            $scope.currentPendingApproval = pendingApproval;

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/approveSelfCollectionPrompt.html',
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            }).then(function () {
                                indexPromise = $http({
                                    url: api.endpoint + 'CreateSelfCollectionRequest',
                                    method: 'POST',
                                    data: {
                                        'demandID': pendingApproval.id,
                                        'deliveryDate': pendingApproval.preferredDeliveryDate,
                                        'timeslot': pendingApproval.preferredTimeslot
                                    },
                                    headers: {
                                        'Content-Type': 'application/json',
                                    }
                                });

                                $scope.promise = [indexPromise];

                                indexPromise.then(function (response) {
                                    if (response.data.isCreated) {
                                        $scope.pendingApprovalList.splice(finalIndex, 1);
                                        $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                    }
                                });
                            });
                        } else {
                            $scope.scheduleAMList = [];
                            $scope.schedulePMList = [];
                            $scope.disabledAMList = [];
                            $scope.disabledPMList = [];
                            $scope.scheduleCount = 0;

                            for (var i = 0; i < $scope.currentPendingApproval.preferredSchedule.length; i++) {
                                var value = $scope.currentPendingApproval.preferredSchedule.charAt(i);

                                if (i % 2 === 0) {
                                    if (value === '0') {
                                        $scope.scheduleAMList.push({'value': false});
                                        $scope.disabledAMList.push(i / 2);
                                    } else {
                                        $scope.scheduleAMList.push({'value': true});
                                        $scope.scheduleCount++;
                                    }
                                } else {
                                    if (value === '0') {
                                        $scope.schedulePMList.push({'value': false});
                                        $scope.disabledPMList.push(Math.floor(i / 2));
                                    } else {
                                        $scope.schedulePMList.push({'value': true});
                                        $scope.scheduleCount++;
                                    }
                                }
                            }

                            var parts = $scope.currentPendingApproval.dateRequested.split("/");
                            var requestDate = new Date(parseInt(parts[2], 10),
                                    parseInt(parts[1], 10) - 1,
                                    parseInt(parts[0], 10));

                            requestDate.setDate(requestDate.getDate() + 3);
                            $scope.dates = [];

                            for (var i = 0; i < 10; i++) {
                                if (requestDate.getDay() !== 0 && requestDate.getDay() !== 6) {
                                    $scope.dates.push({'value': new Date(requestDate)});
                                } else {
                                    i--;
                                }


                                requestDate.setDate(requestDate.getDate() + 1);
                            }

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/approveJobRequestPrompt.html',
                                className: 'ngdialog-theme-default dialog-approve-request-2',
                                scope: $scope
                            }).then(function () {
                                indexPromise = $http({
                                    url: api.endpoint + 'CreateJobRequest',
                                    method: 'POST',
                                    data: {
                                        'demandID': pendingApproval.id,
                                        'userID': pendingApproval.user.id,
                                        'deliveryDate': $scope.selectedDate,
                                        'timeslot': $scope.selectedTimeslot
                                    },
                                    headers: {
                                        'Content-Type': 'application/json',
                                    }
                                });

                                $scope.promise = [indexPromise];

                                indexPromise.then(function (response) {
                                    if (response.data.isCreated) {
                                        $scope.pendingApprovalList.splice(finalIndex, 1);
                                        $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                    }
                                });
                            });
                        }
                    };

                    //retrieve details
                    $scope.getObj = function (component, column) {
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

                    $scope.view = function (pendingApproval) {
                        $scope.currentPendingApproval = pendingApproval;

                        $http({
                            url: api.endpoint + 'GetDemandItemListByDemandIdRequest/' + pendingApproval.id,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.currentDemandItemList = response.data;
                        });

                        if (pendingApproval.preferredSchedule === 'NA') {
                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/viewSelfCollectionRequest.html',
                                className: 'ngdialog-theme-default dialog-generic-2',
                                scope: $scope
                            });
                        } else {
                            $scope.scheduleAMList = [];
                            $scope.schedulePMList = [];
                            $scope.disabledAMList = [];
                            $scope.disabledPMList = [];
                            $scope.scheduleCount = 0;

                            for (var i = 0; i < $scope.currentPendingApproval.preferredSchedule.length; i++) {
                                var value = $scope.currentPendingApproval.preferredSchedule.charAt(i);

                                if (i % 2 === 0) {
                                    if (value === '0') {
                                        $scope.scheduleAMList.push({'value': false});
                                        $scope.disabledAMList.push(i / 2);
                                    } else {
                                        $scope.scheduleAMList.push({'value': true});
                                        $scope.scheduleCount++;
                                    }
                                } else {
                                    if (value === '0') {
                                        $scope.schedulePMList.push({'value': false});
                                        $scope.disabledPMList.push(Math.floor(i / 2));
                                    } else {
                                        $scope.schedulePMList.push({'value': true});
                                        $scope.scheduleCount++;
                                    }
                                }
                            }

                            var parts = $scope.currentPendingApproval.dateRequested.split("/");
                            var requestDate = new Date(parseInt(parts[2], 10),
                                    parseInt(parts[1], 10) - 1,
                                    parseInt(parts[0], 10));

                            requestDate.setDate(requestDate.getDate() + 3);
                            $scope.dates = [];

                            for (var i = 0; i < 10; i++) {
                                if (requestDate.getDay() !== 0 && requestDate.getDay() !== 6) {
                                    $scope.dates.push({'value': new Date(requestDate)});
                                } else {
                                    i--;
                                }


                                requestDate.setDate(requestDate.getDate() + 1);
                            }

                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/viewVolunteerRequest.html',
                                className: 'ngdialog-theme-default dialog-approve-request-3',
                                scope: $scope
                            });
                        }
                    };

                    $scope.edit = function (id) {
                        $state.go('PendingApprovalsEdit', {Id: id});
                    };

                    $scope.approve = function (pendingApproval, index) {
                        $scope.currentPendingApproval = pendingApproval;
                        validApproval(pendingApproval, index, '');
                    };

                    $scope.reject = function (pendingApproval, index) {
                        var finalIndex = $scope.pendingApprovalList.indexOf(pendingApproval);

                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/rejectRequestPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function (reason) {
                            $http({
                                url: api.endpoint + 'RejectDemandRequest/' + pendingApproval.id,
                                method: 'PUT',
                                data: {
                                    'comments': reason
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isRejected) {
                                    $scope.pendingApprovalList.splice(finalIndex, 1);
                                    $state.go($state.current, $stateParams, {reload: true, inherit: false});
                                }
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['user.organizationName'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetPendingDemandListBySupplierIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        $http({
                            url: api.endpoint + 'GetDemandItemListBySupplierIdRequest/' + userID,
                            method: 'GET'
                        }).then(function (response) {
                            $scope.demandItemList = response.data;

                            indexPromise.then(function (response) {
                                $scope.pendingApprovalList = response.data;
                                $scope.currentPage = 1;
                                $scope.pageSize = 10;

                                $scope.$watch('searchFilter', function () {
                                    $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['itemName']);
                                });
                            });
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
