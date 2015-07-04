define(function (require) {
    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var Numbers = require("app/util/Numbers");
    require("tweenmax");

    return Backbone.Model.extend({

        constructor: function (index, x) {
            this.x = x;
            this.index = index;

            var itemElement = $("<div/>");
            itemElement.append("This is a test of the session " + index);
            itemElement.addClass("item");
            itemElement.css({
                left: this.x + "px",
                top: window.innerHeight + "px",
                transform: "scale(.5,.5) rotateX(-10deg) rotateY(-40deg) rotateZ(-154deg)"
            });
            this.element = itemElement;
            this.enterComplete = false;
        },

        enter: function () {
            if (this.enterTween && this.enterTween.isActive()) {
                return;
            }
            this.enterComplete = 0;
            this.enterTween = TweenLite.to(this.element[0], 2, {
                autoAlpha: 1,
                top: window.innerHeight / 2,
                scale: 1,
                rotationZ: 0,
                rotationX: 0,
                rotationY: 0,
                onComplete: function () {
                    this.enterComplete = true;
                }.bind(this)
            });
        },

        exit: function () {
            if (this.exitTween && this.exitTween.isActive()) {
                return;
            }
            this.enterTween.kill();
            this.enterTween.eventCallback("onComplete", null);
            this.exitTween = TweenLite.to(this.element[0], 1, {
                autoAlpha: 0,
                top: 0,
                rotationZ: 140,
                rotationX: 60,
                rotationY: 20,
                scale: .6,
                onComplete: function () {
                    this.element.remove();
                }.bind(this)
            });
        }
    })
});
