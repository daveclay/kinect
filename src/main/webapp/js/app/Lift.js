define(function (require) {
    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");
    var Numbers = require("app/util/Numbers");

    var WIDTH = window.innerWidth;
    var HEIGHT = window.innerHeight;
    var NUMBER_OF_ITEMS = 11;
    var TRANSITION_TIME = 2000;
    var PIXELS_PER_ITEM = WIDTH / NUMBER_OF_ITEMS;
    var OPACITY_DISTANCE = 200;

    var currentPosition = {
        x: 0,
        y: 0
    };

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

        // Todo: setTimeout replacement
        console.log(new Date() + " removing element at " + index);
        element.remove();
        if (callback) {
            callback(index);
        }
    };

    var LiftView = GestureAwareView.extend({

        _findItemLiftingInForIndex: function(index) {
            for (var i = 0; i < this.itemsLiftingIn.length; i++) {
                var item = this.itemsLiftingIn[i];
                if (item.index == index) {
                    return item;
                }
            }
        },

        createItemAt: function(index) {
            if (index < 0) {
                return;
            }
            var existingItem = this._findItemLiftingInForIndex(index);
            if (! existingItem) {
                this.itemsLiftingIn[index] = true;

                var item = new Item(index);

                this.items.push(item);
                this.stage.append(item.element);

                var self = this;

                item.liftIn(self.opacity);
            }
        },

        _removeAndLiftItemOut: function(item) {
            if (this.items[item.index]) {
                this.items.splice(this.items.indexOf(item), 1);
            }
            item.liftOut();
        },

        scheduleItemLiftOut: function(item) {
            var self = this;
            if ( ! self.itemsToLiftOut[item.index]) {
                self.itemsToLiftOut.push({
                    timestamp: new Date().getTime(), // TODO: make random
                    item: item
                });
            }
        },

        setOpacity: function(opacity) {
            this.opacity = opacity;
            for (var i = 0; i < this.items.length; i++) {
                this.items[i].setOpacity(opacity);
            }
        },

        createItemsForPosition: function(x) {
            var index = Math.round(x / PIXELS_PER_ITEM);
            this.currentIndex = index;
            this.createItemAt(index - 1);
            this.createItemAt(index);
            this.createItemAt(index + 1);
        },

        _isItemWithinIndex: function(item) {
            var index = this.currentIndex;
            return item.index >= index - 1 && item.index <= index + 1;
        },

        _checkItemsForCurrentIndex: function() {
            for (var i = 0; i < this.items.length; i++) {
                var item = this.items[i];
                if ( ! this._isItemWithinIndex(item)) {
                    this.scheduleItemLiftOut(item);
                }
            }
        },

        updatePosition: function() {
            var x = currentPosition.x;
            var y = currentPosition.y;

            var verticalSpace = (y - OPACITY_DISTANCE / 2) / (HEIGHT - OPACITY_DISTANCE);
            if (verticalSpace < 0) {
                verticalSpace = 0;
            }

            this.background.css({
                opacity: verticalSpace < .5 / 2 ? 1 : 0
            });

            this.setOpacity(verticalSpace);
            this.createItemsForPosition(x);
            this._checkItemsForCurrentIndex();

            var temp = [];
            for (var i = 0; i < this.itemsToLiftOut.length; i++) {
                var liftOutData = this.itemsToLiftOut[i];
                if (liftOutData) {
                    // items are non-sequential: sometimes we get mouse positions at 1, 5, and 6
                    var item = liftOutData.item;
                    if (new Date().getTime() - liftOutData.timestamp > 2000) {
                        if ( ! this._isItemWithinIndex(item)) {
                            this._removeAndLiftItemOut(item);
                        } else {
                            temp.push(liftOutData);
                        }
                    }
                }
            }

            this.itemsToLiftOut = temp;

            var self = this;
            this.thread = window.requestAnimationFrame(function() {
                self.updatePosition();
            })
        },

        constructor: function(params) {
            GestureAwareView.prototype.constructor.apply(this, params);

            this.stage = $('#stage');
            this.background = $('#background');
            this.itemsLiftingIn = [];
            this.itemsToLiftOut = [];
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
            });

            $(document).mousemove(function(event) {
                // Todo: since this queues, you're running against all these queued up events.
                // Todo: set the current position, then let the animation thread pick up the
                // Todo: current location and work with that.
                currentPosition = {
                    x: event.pageX,
                    y: event.pageY
                };
            });

            this.thread = window.requestAnimationFrame(function() {
                self.updatePosition();
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
