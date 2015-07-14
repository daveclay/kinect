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
            return "<h3>Statistics #" + item.index + "</h3><div class=\"info-text\">" +
                "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc." +
                "</div>";
        },

        initial: function(item) {
            item.element.css({
                left: item.x + "px",
                top: window.innerHeight + "px",
                height: window.innerHeight + "px"
            });
        },

        showText: function(item) {
            var textElem = item.element.find(".info-text");
            item.textTween = TweenLite.to(textElem[0],.5, {
                opacity: 1
            });
        },

        scheduleExpand: function(item) {
            item.enterTween.eventCallback("onComplete", null);


            var clipPath1 = [0, 100, 0, 0, 100, 0, 100, 100, 100];
            var clipPath2 = [0, 100, 0, 0, 100, 0, 80, 100, 400];

            clipPath2.onUpdate = function() {
                TweenMax.set(item.element[0], {
                    webkitClipPath: 'polygon(' +
                        clipPath1[0] + '% ' + clipPath1[1] + '%,' + clipPath1[2] + '% ' + clipPath1[3] + '%,' +
                        clipPath1[4] + '% ' + clipPath1[5] + '%,' + clipPath1[6] + '% ' + clipPath1[7] + '%)',
                    width: clipPath1[8],
                    ease: Power4.easeInOut
                });
            };

            item.expandTween = TweenLite.to(clipPath1, .75, clipPath2).delay(1);
            /*
            item.expandTween = TweenLite.to(item.element[0], .75, {
                width: 400,
                webkitClipPath: "polygon(25% 0%, 100% 0, 75% 100%, 0% 100%)",
                onComplete: function() {
                    item.expandTween.eventCallback("onComplete", null);
                    this.showText(item);
                }.bind(this)
            }).delay(1);
            */
        },

        enter: function(item) {
            var backgroundColor = Colors.cssHSV(item.index / 25, 1, 1, 1);
            var color = Colors.cssHSV((this.NUMBER_OF_ITEMS - item.index) / 15, 1, .25);

            return TweenLite.to(item.element[0], 2, {
                alpha: 1,
                top: 0,
                backgroundColor: backgroundColor,
                color: color,
                ease: Power4.easeInOut,
                onComplete: function() {
                    this.scheduleExpand(item);
                    item.enterComplete = true;
                }.bind(this)
            });
        },

        exit: function(item) {
            item.expandTween.kill();

            var reverseExpand = function() {
                item.expandTween.eventCallback("onReverseComplete", function () {
                    this.goAway(item);
                }.bind(this));
                item.expandTween.reverse();
            }.bind(this);

            if (item.textTween && item.textTween.progress() > 0) {
                item.textTween.eventCallback("onReverseComplete", function () {
                    reverseExpand();
                }.bind(this));
                item.textTween.reverse();
            } else if (item.expandTween && item.expandTween.progress() > 0) {
                reverseExpand();
            } else {
                this.goAway(item);
            }
        },

        goAway: function(item) {
            var index = item.index;
            // 0 <= h, s, v <= 1
            var color = Colors.HSVtoRGB(index / 255, 1, 1);
            return TweenLite.to(item.element[0], 1, {
                autoAlpha: 0,
                ease: Power4.easeInOut,
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

