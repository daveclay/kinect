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

        showText: function(item) {
            item.element.append("There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc.");
            //item.element.append(item.index);
            //item.expandAnimation.eventCallback("onComplete", null);
        },

        scheduleExpand: function(item) {
            item.enterTween.eventCallback("onComplete", null);
            item.expandAnimation = TweenLite.to(item.element[0], .75, {
                width: 400,
                onComplete: function() {
                    item.expandAnimation.eventCallback("onComplete", null);
                    this.showText(item);
                }.bind(this)
            }).delay(1);
        },

        enter: function(item) {
            var color = Colors.HSVtoRGB(item.index / 25, 1, 1);
            var cssColor = "rgba(" + color.r + ", " + color.g + ", " + color.b + ")";

            return TweenLite.to(item.element[0], 2, {
                alpha: 1,
                top: 0,
                backgroundColor: cssColor,
                onComplete: function() {
                    this.scheduleExpand(item);
                    item.enterComplete = true;
                }.bind(this)
            });
        },

        exit: function(item) {
            item.expandAnimation.kill();
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

