(function () {
    'use strict';
    angular
            .module('Wheels4Food.Config', [])
            .constant('config', {
                'errors': {
                    'required': 'This field is required.',
            
                    //registration & change password
                    'passwordMatch': 'Password and confirmation password do not match.',
                    'email': 'Please enter a valid email.',
                    'postalCode': 'Please enter a 6-digit number.',
                    'contact': 'Please enter a 8-digit number.',
                    'license': 'Please enter a whole number.',
                    
                    //create & edit supply
                    'wholeNumber': 'Please enter a whole number only.',
                    'minimum': 'Please enter a number greater than 0.',
                    'monetaryValue': 'Please enter a number only.',
                    'dateFormat': 'Please enter a valid date in the correct format.',
                    'dateToday': 'Please choose a date after today.',
                    
                    //request supplies
                    'requiredFields': 'The fields above are required.',
                    'wholeNumbers': 'Please enter whole numbers only.',
                    'requestQuantity': 'Please enter numbers between Minimum and Maximum Request Quantity.',
                    'timeslot': 'Please select 1 timeslot only.',
                    'volunteerTimeslots': 'Please select at least 3 timeslots.'
                },
                'cancelSelfCollectionReasons': {
                    'requester': {
                        'reason1': {
                            'text': 'I am unable to collect the items at the specified timing',
                            'value': 'Requesting VWO is unable to collect the items at the specified timing'
                        },
                        'reason2': {
                            'text': 'Supplier did not show up at the specified timing',
                            'value': 'Supplying VWO did not show up at the specified timing'
                        }
                    },
                    'supplier': {
                        'reason1': {
                            'text': 'I am unable to provide the items at the specified timing',
                            'value': 'Suppying VWO is unable to provide the items at the specified timing'
                        },
                        'reason2': {
                            'text': 'Requester did not show up at the specified timing',
                            'value': 'Requesting VWO did not show up at the specified timing'
                        }
                    }
                },
                'cancelCreatedJobReasons': {
                    'requester': {
                        'reason1': {
                            'text': 'I am unable to receive the items at the specified timing',
                            'value': 'Requesting VWO is unable to receive the items at the specified timing'
                        }
                    },
                    'supplier': {
                        'reason1': {
                            'text': 'I am unable to provide the items at the specified timing',
                            'value': 'Suppying VWO is unable to provide the items at the specified timing'
                        }
                    }
                },
                'cancelAcceptedJobReasons': {
                    'requester': {
                        'reason1': {
                            'text': 'I am unable to receive the items at the specified timing',
                            'value': 'Requesting VWO is unable to receive the items at the specified timing'
                        },
                        'reason2': {
                            'text': 'Volunteer did not deliver the items at the specified timing',
                            'value': 'Volunteer did not deliver the items at the specified timing'
                        }
                    },
                    'supplier': {
                        'reason1': {
                            'text': 'I am unable to provide the items at the specified timing',
                            'value': 'Suppying VWO is unable to provide the items at the specified timing'
                        },
                        'reason2': {
                            'text': 'Volunteer did not arrive at the specified timing',
                            'value': 'Volunteer did not arrive at the specified timing'
                        }
                    },
                    'volunteer': {
                        'reason1': {
                            'text': 'I am unable to collect the items at the specified timing',
                            'value': 'Volunteer is unable to collect the items at the specified timin'
                        },
                        'reason2': {
                            'text': 'I am unable to deliver the items at the specified timing',
                            'value': 'Volunteer is unable to deliver the items at the specified timin'
                        },
                        'reason3': {
                            'text': 'Supplier did not show up at the specified timing',
                            'value': 'Supplying VWO did not show up at the specified timing'
                        },
                        'reason4': {
                            'text': 'Requester did not show up at the specified timing',
                            'value': 'Requesting VWO did not show up at the specified timing'
                        }
                    }
                }
            });
})();