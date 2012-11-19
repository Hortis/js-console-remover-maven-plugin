define(['backbone', //
    'jQuery'], // ],

    function (Backbone, $) {
        return App.module('test', function (test) {
            test.router = Backbone.Router.extend({

                routes: {
                    'list': 'userList',
                    'list/:id': 'oneUser'
                },


                userList: function () {
                    $.ajax({
                        type: 'GET',
                        url: '/api/users',

                        success: function (response) {
                            console.log("Getting list from users : " + response);
                        },
                        error: function (error) {
                            console.log('router > error : ' + error);
                        }
                    });
                },

                oneUser: function (e) {
                    console.log('test : ');

                    $.ajax({
                        type: 'GET',
                        url: '/users/' + e,

                        success: function (response) {
                           console.log("response : " + response);
                        },
                        error: function(error) {
                            console.log("router > error : " + error);
                        }

                    });
                }

            });
        });
    });