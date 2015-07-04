define(function (require) {
    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");
    var Numbers = require("app/util/Numbers");
    require("tweenmax");
    var Item = require("app/Item");

    var WIDTH = window.innerWidth;
    var HEIGHT = window.innerHeight;
    var NUMBER_OF_ITEMS = 25;
    var PIXELS_PER_ITEM = WIDTH / NUMBER_OF_ITEMS;
    var currentPosition = {
        x: 0,
        y: 0
    };

    return GestureAwareView.extend({
        el: '#stage',

        updateItemsAtCurrentPosition: function() {
            var indexData = this.items[this.currentIndex];
            if (!indexData.item) {
                var newItem = new Item(this.currentIndex, PIXELS_PER_ITEM * this.currentIndex, this.itemOptions);
                this.$el.append(newItem.element);
                indexData.item = newItem;
            }
            if (this.isViewerInBounds()) {
                var item = indexData.item;
                item.enter();
            }
            this.updateOtherItems();
        },

        updateOtherItems: function() {
            for (var i = 0; i < this.items.length; i++) {
                if (i != this.currentIndex) {
                    var indexData = this.items[i];
                    if (indexData.item) {
                        var item = indexData.item;
                        if (item.enterComplete) {
                            item.exit();
                            indexData.item = null;
                        }
                    }
                }
            }
        },

        nextIndex: function() {
            var x = currentPosition.x;
            var index = Math.round(x / PIXELS_PER_ITEM);
            if (index > NUMBER_OF_ITEMS - 1) {
                index = NUMBER_OF_ITEMS - 1;
            }
            return index;
        },

        updatePosition: function() {
            if (!this.titleTween || !this.titleTween.isActive()) {
                if (this.isViewerInBounds()) {
                    this.titleTween = TweenLite.to(this.title[0], 1, {
                        autoAlpha: 0,
                        top: 0
                    });
                } else {
                    this.titleTween = TweenLite.to(this.title[0], 1, {
                        autoAlpha: 1
                    });
                }
            }
            var nextIndex = this.nextIndex();
            this.currentIndex = nextIndex;
            this.updateItemsAtCurrentPosition();

            var self = this;
            this.thread = window.requestAnimationFrame(function() {
                self.updatePosition();
            })
        },

        isViewerInBounds: function() {
            return currentPosition.y  < HEIGHT / 2;
        },

        constructor: function(params) {
            GestureAwareView.prototype.constructor.apply(this, params);
            var self = this;
            this.itemOptions = params.itemOptions;

            this.title = $("#title");
            this.titlePosition = this.title[0].style.height;
            console.log(this.titlePosition);

            this.items = [];
            for (var i = 0; i < NUMBER_OF_ITEMS; i++) {
                this.items[i] = {
                    index: i
                }
            }

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

            // this.connect();
        }
    });
});
