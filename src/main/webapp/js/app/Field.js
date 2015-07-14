define(function (require) {
    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");
    var Numbers = require("app/util/Numbers");
    require("tweenmax");
    var LiftView = require("app/LiftView");
    var Item = require("app/Item");
    var Colors = require("app/util/Colors");

    var itemOptions = {
        NUMBER_OF_ITEMS: 25,
        template: function(item) {
            return "<h1>Statistics #" + item.index + "</h1>"
        },

        initial: function(item) {
            item.element.css({
                left: item.x + "px",
                top: window.innerHeight + "px",
                height: window.innerHeight + "px"
            });
        },

        enter: function(item) {
            var color = Colors.HSVtoRGB(item.index / 25, 1, 1);
            var cssColor = "rgba(" + color.r + ", " + color.g + ", " + color.b + ")";

            return TweenLite.to(item.element[0], 2, {
                alpha: 1,
                top: 0,
                backgroundColor: cssColor,
                onComplete: function () {
                    item.enterComplete = true;
                }
            });
        },

        exit: function(item) {
            var index = item.index;
            // 0 <= h, s, v <= 1
            var color = Colors.HSVtoRGB(index / 255, 1, 1);
            return TweenLite.to(item.element[0], 1, {
                autoAlpha: 0,
                top: window.innerHeight * .9,
                onComplete: function () {
                    item.element.remove();
                }
            });
        }
    };

    mvc.addRouteConfig({
        path: "presentation",
        view: new LiftView({
            itemOptions: itemOptions
        }),
        defaultRoute: true
    });
});

