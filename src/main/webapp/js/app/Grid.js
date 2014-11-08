define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");
    var LogView = require("app/LogView");

    var keys = {
        left: 37,
        right: 39,
        up: 38,
        down: 40
    };

    var GridView = GestureAwareView.extend({

        render: function(params) {
        },

        _registerKeyListeners: function() {
            var self = this;
            document.onkeydown = function(event) {
                switch (event.keyCode) {
                    case keys.left:
                        event.preventDefault();
                        self.navigateLeft();
                        break;
                    case keys.right:
                        event.preventDefault();
                        self.navigateRight();
                        break;
                    case keys.up:
                        event.preventDefault();
                        self.navigateUp();
                        break;
                    case keys.down:
                        event.preventDefault();
                        self.navigateDown();
                        break;

                }
            }
        },

        _registerGestureListeners: function() {
            var self = this;

            this.moveToNewLocation = function() {
                var x = -100 * this.slideLocation.row;
                var y = -100 * this.slideLocation.column;
                self.slideContainer.style.transform = "translate3d(" + x + "%, " + y + "%, 0)";
            };

            this.navigateUp = function() {
                if (self.slideLocation.column > 0) {
                    self.slideLocation.column--;
                    self.moveToNewLocation();
                }
            };

            this.navigateDown = function() {
                if (self.slideLocation.column < self.columns - 1) {
                    self.slideLocation.column++;
                    self.moveToNewLocation();
                }
            };

            this.navigateLeft = function() {
                if (self.slideLocation.row > 0) {
                    self.slideLocation.row--;
                    self.moveToNewLocation();
                }
            };

            this.navigateRight = function() {
                if (self.slideLocation.row < self.rows - 1) {
                    self.slideLocation.row++;
                    self.moveToNewLocation();
                }
            };

            this.on("LeftToRightLine", this.navigateRight);
            this.on("RightToLeftLine", this.navigateLeft);
            this.on("TopToBottomLine", this.navigateDown);
            this.on("BottomToTopLine", this.navigateUp);
        },

        constructor: function(params) {
            GestureAwareView.prototype.constructor.apply(this, params);
            this.slideContainer = document.getElementById("slide-container");
            this.rows = 3;
            this.columns = 3;
            this.slides = [];
            var count = 1;
            for (var row = 0; row < this.rows; row++) {
                this.slides[row] = [];
                for (var column = 0; column < this.columns; column++) {
                    var slideElement = document.getElementById("slide" + count);
                    slideElement.style.width = window.innerWidth + "px";
                    slideElement.style.height = window.innerHeight + "px";
                    this.slides[row][column] = slideElement;
                    count++;
                }
            }

            this.slideLocation = {
                row: 0,
                column: 0
            };
            this.logView = new LogView();
            this._registerGestureListeners();
            this._registerKeyListeners();
        }
    });

    mvc.addRouteConfig({
        path: "presentation",
        view: new GridView({
            el: $('#stage')
        }),
        defaultRoute: true
    });

});

