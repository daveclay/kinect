define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");

    var MultitouchTestController = mvc.Controller.extend({
        run: function(params) {
            params.view.populate();
        }
    });

    var MultitouchTestView = Backbone.View.extend({

        el: $('body'),

        populate: function() {
        },

        events: {
            "touchstart": "onTouchstart"
        },

        onTouchstart: function(event) {
            _.each(event.touches, function(touch) {
                var left = touch.clientX;
                var top = touch.clientY;

                var div = $('<div/>');
                div.addClass("touch");
                div.css({
                    top: top,
                    left: left
                });
                $('body').append(div)

            });
        },

        initialize: function() {
            var self = this;
        }
    });

    mvc.addRouteConfig({
        path: "multitouchtest",
        controller: new MultitouchTestController(),
        view: new MultitouchTestView(),
        defaultRoute: true
    });

});

