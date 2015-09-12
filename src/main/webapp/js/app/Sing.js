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
        NUMBER_OF_ITEMS: 355,
        template: function(item) {
            return ""
        },

        initial: function(item) {
            item.element.css({
                left: item.x + "px",
                top: window.innerHeight + "px",
                transform: "scale(.5,.5) rotateX(-23deg) rotateY(-241deg) rotateZ(-94deg)"
            });
        },

        enter: function(item) {
            var color = Colors.HSVtoRGB(item.index / 255, 1, 1);

            var startColor = "rgba(" + color.r + ", " + color.g + ", " + color.b + ", .1)";
            var stopColor = "rgba(" + color.r + ", " + color.g + ", " + color.b + ", 0)";
            return TweenLite.to(item.element[0], 2, {
                autoAlpha:.5,
                top: 0,
                scale: 20,
                rotationZ: 0,
                rotationX: 4,
                rotationY: 5,
                backgroundImage:"radial-gradient(circle," + startColor + " 0%, " + stopColor + " 80%)",

                onComplete: function () {
                    item.enterComplete = true;
                }
            });
        },

        exit: function(item) {
            var index = item.index;
            // 0 <= h, s, v <= 1
            var color = Colors.HSVtoRGB(index / 255, 1, 1);

            var startColor = "rgba(250, 247, 255, .75)";
            var stopColor = "rgba(" + color.r + ", " + color.g + ", " + color.b + ", 0)";
            return TweenLite.to(item.element[0], 1, {
                autoAlpha: 0,
                top: window.innerHeight * .9,
                backgroundImage:"radial-gradient(circle," + startColor + " 0%, " + stopColor + " 80%)",
                rotationZ: 0,
                rotationX: 3,
                rotationY: 9,
                scale: 2,
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

