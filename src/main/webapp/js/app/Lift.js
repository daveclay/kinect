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

    var WIDTH = window.innerWidth;
    var HEIGHT = window.innerHeight;
    var NUMBER_OF_ITEMS = 25;
    var PIXELS_PER_ITEM = WIDTH / NUMBER_OF_ITEMS;
    var currentPosition = {
        x: 0,
        y: 0
    };

    var itemOptions = {
        initial: function(item) {
            item.element.css({
                left: item.x + "px",
                top: window.innerHeight + "px",
                transform: "scale(.5,.5) rotateX(-10deg) rotateY(-40deg) rotateZ(-154deg)"
            });
        },

        enter: function(item) {
            return TweenLite.to(item.element[0], 2, {
                autoAlpha: 1,
                top: window.innerHeight / 2,
                scale: 1,
                rotationZ: 0,
                rotationX: 0,
                rotationY: 0,
                onComplete: function () {
                    item.enterComplete = true;
                }
            });
        },

        exit: function(item) {
            return TweenLite.to(item.element[0], 1, {
                autoAlpha: 0,
                top: 0,
                rotationZ: 140,
                rotationX: 60,
                rotationY: 20,
                scale: .6,
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
