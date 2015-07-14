define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var Swiper = require("swiper");

    var SwiperView = Backbone.View.extend({

        el: $('#swiper'),

        render: function(params) {
            var self = this;
            var uri = "ws://localhost:12345";
            var ws = new WebSocket(uri);
            ws.onopen = function() {
                self.log("[WebSocket#onopen]\n");
            };
            ws.onmessage = function(event) {
                var payload = JSON.parse(event.data);
                self.log("[WebSocket#onmessage] Message: '" + payload + "'\n");
                // { type: 'userGestureRecognized', data: { name: '" + gesture.name + "', score: " + gesture.score + " }}
                // { type: 'userDidEnterZone', data: { zone: '" + stageZone.getID() + "'}}
                if (payload.type === 'userGestureRecognized') {
                    self.listeners[payload.data.name]();
                }
            };
            ws.onclose = function() {
                self.log("[WebSocket#onclose]\n");
                ws = null;
            };
        },

        _registerGestureListeners: function() {
            var self = this;

            self.listeners = [];

            self.listeners["LeftToRightLine"] = function() {
                self.horizontalSwiper.swipePrev();
            };

            self.listeners["RightToLeftLine"] = function() {
                self.horizontalSwiper.swipeNext();
            };

            self.listeners["TopToBottomLine"] = function() {
                var slideIndex = self.horizontalSwiper.activeIndex;
                self.verticalsSwipers[slideIndex].swipeNext();
            };

            self.listeners["BottomToTopLine"] = function() {
                var slideIndex = self.horizontalSwiper.activeIndex;
                self.verticalsSwipers[slideIndex].swipePrev();
            };
        },

        buildSlider: function(element, mode) {
            var swiper = element.swiper({
                watchActiveIndex: true,
                mode: mode,
                resistance: false,
                centeredSlides: true,
                resizeReInit: true,
                onImagesReady: function() {
                },
                noSwiping: true, // So swiping can be disabled with a class
                onSlideChangeStart: function(swiper) {
                },
                onSlideChangeEnd: function(swiper) {
                },
                onFirstInit: function(swiper) {
                }
            });

            //Smart resize
            $(window).resize(function() {
                swiper.resizeFix(true);
            });

            return swiper;
        },

        log: function(message) {
            var existing = this.textarea.innerHTML;
            this.textarea.innerHTML = new Date() + ": " + message + existing;
        },

        initialize: function() {
            var self = this;
            var body = $('body');

            self.textarea = $('#log').find('textarea')[0];

            var swiperElement = body.find(".horizontal-swiper");
            self.horizontalSwiper = self.buildSlider($(swiperElement), 'horizontal');

            self.verticalsSwipers = [];

            var verticalSwiperElements = body.find(".vertical-swiper");
            _.each(verticalSwiperElements, function(element, index) {
                var verticalSwiper = self.buildSlider($(element), 'vertical');
                self.verticalsSwipers.push(verticalSwiper);
            });

            self._registerGestureListeners();
        }
    });

    mvc.addRouteConfig({
        path: "swiper",
        view: new SwiperView(),
        defaultRoute: true
    });

});

