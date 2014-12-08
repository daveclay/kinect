define(function (require) {
    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");
    var Numbers = require("app/util/Numbers");

    var WIDTH = window.innerWidth;
    var HEIGHT = window.innerHeight;
    var NUMBER_OF_ITEMS = 25;
    var TRANSITION_TIME = 2000;
    var PIXELS_PER_ITEM = WIDTH / NUMBER_OF_ITEMS;
    var OPACITY_DISTANCE = 200;

    function Item(index) {
        var itemElement = $("<div/>");
        itemElement.append("n=" + index);
        itemElement.addClass("item");
        this.element = itemElement;
        this.x = PIXELS_PER_ITEM * index;
        this.index = index;
        this.move(HEIGHT, 0);
    }

    Item.prototype.move = function(y, opacity) {
        this.element.css({
            "transform": "translate3d(" + this.x + "px, " + y + "px, 0)",
            "opacity": opacity
        });
    };

    Item.prototype.setOpacity = function(opacity) {
        this.opacity = opacity;
        // todo: don't use the transition time to adjust the opacity when setting it here?
        this.element.css({
            "opacity": opacity
        });
    };

    Item.prototype.liftIn = function(opacity, callback) {
        this.move(HEIGHT / 2 + 50, opacity);
        if (callback) {
            setTimeout(callback, TRANSITION_TIME);
        }
    };

    Item.prototype.liftOut = function(callback) {
        this.move(0, 0);
        var index = this.index;
        var element = this.element;
        setTimeout(function() {
            element.remove();
            if (callback) {
                callback(index);
            }
        }, TRANSITION_TIME);
    };

    var LiftView = GestureAwareView.extend({

        createItemAt: function(index) {
            if (!this.itemsLiftingIn[index]) {
                this.itemsLiftingIn[index] = true;

                var item = new Item(index);

                this.items.push(item);
                this.stage.append(item.element);

                var self = this;

                setTimeout(function() {
                    item.liftIn(self.opacity);
                }, Math.round(Math.random() * 200) + 1000);
            }
        },

        scheduleItemLiftOut: function(item) {
            setTimeout(function() {
                item.liftOut();
            }, Math.round(Math.random() * 1500) + 1500);
        },

        setOpacity: function(opacity) {
            this.opacity = opacity;
            for (var i = 0; i < this.items.length; i++) {
                this.items[i].setOpacity(opacity);
            }
        },

        triggerItemsForPosition: function(x) {
            var index = Math.round(x / PIXELS_PER_ITEM);
            this.createItemAt(index - 1);
            this.createItemAt(index);
            this.createItemAt(index + 1);
            for (var i = 0; i < this.items.length; i++) {
                var item = this.items[i];
                if (item.index < index - 1 || item.index > index + 1) {
                    this.items.splice(this.items.indexOf(item), 1);
                    this.itemsLiftingIn[item.index] = false;
                    this.scheduleItemLiftOut(item);
                }
            }
        },

        updatePosition: function(position) {
            var x = position.x;
            var y = position.y;

            var verticalSpace = (y - OPACITY_DISTANCE / 2) / (HEIGHT - OPACITY_DISTANCE);
            if (verticalSpace < 0) {
                verticalSpace = 0;
            }

            this.background.css({
                opacity: verticalSpace < .5 / 2 ? 1 : 0
            });

            this.setOpacity(verticalSpace);
            this.triggerItemsForPosition(x);
        },

        constructor: function(params) {
            GestureAwareView.prototype.constructor.apply(this, params);

            this.stage = $('#stage');
            this.background = $('#background');
            this.itemsLiftingIn = [];
            this.items = [];

            var self = this;
            this.onConnect(function() {
                console.log("Connected.");
            });

            this.onUserDidMove(function(position) {
                var hi = {
                    x: Numbers.map(position.x, -300, 300, window.innerWidth, 0),
                    y: Numbers.map(position.z, 900, 2200, window.innerHeight, 0)
                };

                self.updatePosition(hi);
            });

            $(document).mousemove(function(event) {
                self.updatePosition({
                    x: event.pageX,
                    y: event.pageY
                });
            });

            this.connect();
        }
    });

    mvc.addRouteConfig({
        path: "presentation",
        view: new LiftView({
        }),
        defaultRoute: true
    });

});
