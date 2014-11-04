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
            ws.onmessage = function(e) {
                self.log("[WebSocket#onmessage] Message: '" + e.data + "'\n");
            };
            ws.onclose = function() {
                self.log("[WebSocket#onclose]\n");
                ws = null;
            };
        },

        buildSlider: function(element, mode) {
            var self = this;
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
        },

        log: function(message) {
            var existing = $('#log').find('textarea')[0].innerHTML;
            $('#log').find('textarea')[0].innerHTML = new Date() + ": " + message + existing;
        },

        initialize: function() {
            var self = this;

            var swiper = $('body').find(".horizontal-swiper");
            self.buildSlider($(swiper), 'horizontal');

            var swipers = $('body').find(".vertical-swiper");
            _.each(swipers, function(element) {
                self.buildSlider($(element), 'vertical');
            });

            this.log("Hi!");
        }
    });

    mvc.addRouteConfig({
        path: "swiper",
        view: new SwiperView(),
        defaultRoute: true
    });

});

