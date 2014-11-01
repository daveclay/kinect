define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var Swiper = require("swiper");

    var SwiperController = mvc.Controller.extend({
        run: function(params) {
            params.view.populate();
        }
    });

    var SwiperView = Backbone.View.extend({

        el: $('#swiper'),

        populate: function() {

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

        initialize: function() {
            var self = this;

            var swiper = $('body').find(".horizontal-swiper");
            self.buildSlider($(swiper), 'horizontal');

            var swipers = $('body').find(".vertical-swiper");
            _.each(swipers, function(element) {
                self.buildSlider($(element), 'vertical');
            })
        }
    });

    mvc.addRouteConfig({
        path: "swiper",
        controller: new SwiperController(),
        view: new SwiperView(),
        defaultRoute: true
    });

});

