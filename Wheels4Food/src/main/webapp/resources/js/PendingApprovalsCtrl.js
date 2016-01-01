(function () {
    'use strict';
    angular
            .module('Wheels4Food.PendingApprovals')
            .controller('PendingApprovalsCtrl', ['$scope', '$state', '$http', 'api', '$timeout', 'ngDialog', 'localStorageService',
                function ($scope, $state, $http, api, $timeout, ngDialog, localStorageService) {
                    var authData = localStorageService.get('authorizationData');
                    var userID = authData.userID;

                    //set default schedule
                    $scope.schedule = {
                        'monday': false,
                        'tuesday': false,
                        'wednesday': false,
                        'thursday': false,
                        'friday': false
                    };

                    //counts the days selected
                    $scope.dayCount = 0;
                    $scope.selectDay = function (day) {
                        if (day) {
                            $scope.dayCount++;
                        } else {
                            $scope.dayCount--;
                        }
                    };
                    
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
                        } else if ($scope.sortType === 'quantityRemaining') {
                            return pendingApproval['supply']['quantityRemaining'];
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
                            proccessed['itemName'] = input;
                        }

                        return proccessed;
                    };

                    var validApproval = function (pendingApproval, index, comment) {
                        ngDialog.openConfirm({
                            template: '/Wheels4Food/resources/ngTemplates/approveRequestPrompt.html',
                            className: 'ngdialog-theme-default dialog-generic',
                            scope: $scope
                        }).then(function (schedule) {
                            $http({
                                url: api.endpoint + 'CreateJobRequest',
                                method: 'POST',
                                data: {
                                    'demandID': pendingApproval.id,
                                    'monday': schedule.monday,
                                    'tuesday': schedule.tuesday,
                                    'wednesday': schedule.wednesday,
                                    'thursday': schedule.thursday,
                                    'friday': schedule.friday,
                                    'comments': comment
                                },
                                headers: {
                                    'Content-Type': 'application/json',
                                }
                            }).then(function (response) {
                                if (response.data.isCreated) {
                                    $scope.pendingApprovalList.splice(($scope.currentPage - 1) * 10 + index, 1);
                                }
                            });
                        });
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

                    $scope.approve = function (pendingApproval, index) {
                        $scope.currentPendingApproval = pendingApproval;

                        if (pendingApproval.quantityDemanded <= pendingApproval.supply.quantitySupplied) {
                            validApproval(pendingApproval, index, '');
                        } else if (pendingApproval.supply.quantitySupplied > 0) {
                            ngDialog.openConfirm({
                                template: '/Wheels4Food/resources/ngTemplates/approveRequestPartialPrompt.html',
                                className: 'ngdialog-theme-default dialog-approve-request',
                                scope: $scope
                            }).then(function (reason) {
                                validApproval(pendingApproval, index, reason);
                            });
                        }
                    };

                    $scope.reject = function (pendingApproval, index) {
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
                                    $scope.pendingApprovalList.splice(($scope.currentPage - 1) * 10 + index, 1);
                                }
                            });
                        });
                    };

                    //set up user table columns
                    $scope.tableColumns = ['supply.itemName', 'supply.category', 'user.organizationName', 'supply.quantitySupplied', 'quantityDemanded', 'dateRequested'];


                    var indexPromise = $http({
                        url: api.endpoint + 'GetPendingDemandListBySupplierIdRequest/' + userID,
                        method: 'GET'
                    });

                    $timeout(function () {
                        indexPromise.then(function (response) {
                            $scope.pendingApprovalList = response.data;
                            $scope.currentPage = 1;
                            $scope.pageSize = 10;

                            $scope.$watch('searchFilter', function () {
                                $scope.proccessedSearchFilter = parseSplitArray($scope.searchFilter, ['itemName']);
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
