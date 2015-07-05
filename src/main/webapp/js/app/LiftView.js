define(function (require) {
    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");
    var LogView = require("app/LogView");
    var Numbers = require("app/util/Numbers");
    require("tweenmax");
    var Item = require("app/Item");

    var HEIGHT = window.innerHeight;
    var currentPosition = {
        x: 0,
        y: 0
    };

    return GestureAwareView.extend({
        el: '#stage',

        updateItemsAtCurrentPosition: function() {
            var indexData = this.items[this.currentIndex];
            if (!indexData.item) {
                var newItem = new Item(this.currentIndex, this.pixelsPerItem * this.currentIndex, this.options);
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
            var index = Math.round(x / this.pixelsPerItem);
            if (index > this.options.NUMBER_OF_ITEMS - 1) {
                index = this.options.NUMBER_OF_ITEMS - 1;
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

            this.thread = window.requestAnimationFrame(function() {
                this.updatePosition();
            }.bind(this))
        },

        isViewerInBounds: function() {
            return currentPosition.y  < HEIGHT / 2;
        },

        constructor: function(params) {
            GestureAwareView.prototype.constructor.apply(this, params);
            this.options = params.itemOptions;
            this.pixelsPerItem = window.innerWidth / this.options.NUMBER_OF_ITEMS;

            this.title = $("#title");

            this.items = [];
            for (var i = 0; i < this.options.NUMBER_OF_ITEMS; i++) {
                this.items[i] = {
                    index: i
                }
            }

            this.onConnect(function() {
                this.thread = window.requestAnimationFrame(function() {
                    this.updatePosition();
                }.bind(this));
            }.bind(this));

            this.onUserDidMove(function(position) {
                currentPosition = {
                    x: Numbers.map(position.fromLeft, 0, 1, 0, window.innerWidth),
                    y: Numbers.map(position.fromFront, 0, 1, 0, window.innerHeight)
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

            this.connect();
        }
    });
});
