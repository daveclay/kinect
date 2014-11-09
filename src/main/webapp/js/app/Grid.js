define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");

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

        _getSlideAtLocation: function() {
            return this.slides[this.slideLocation.row][this.slideLocation.column];
        },

        _isCurrentLocation: function(newLocation) {
            return this.slideLocation.row === newLocation.row
                && this.slideLocation.column === newLocation.column;
        },

        _moveToNewLocation: function(newLocation) {
            if ( ! this._isCurrentLocation(newLocation)) {
                var slideLeaving = this._getSlideAtLocation();
                slideLeaving.style.opacity = 0;
                this.slideLocation = newLocation;
            }

            var x = -100 * this.slideLocation.column;
            var y = -100 * this.slideLocation.row;

            var colorIndex = ((this.slides[this.slideLocation.row].length * this.slideLocation.row) + this.slideLocation.column);
            var color = this.slideData[colorIndex];

            this.stage.style.backgroundColor = color.backgroundColor;
            this.header.style.backgroundColor = color.headerColor;

            var slideEntering = this._getSlideAtLocation();
            slideEntering.style.opacity = 1;
            slideEntering.style.color = color.color;

            this.slideContainer.style.transform = "translate3d(" + x + "%, " + y + "%, 0)";
        },

        _registerGestureListeners: function() {
            var self = this;

            this.navigateUp = function() {
                if (self.slideLocation.row > 0) {
                    self._moveToNewLocation({
                        row: self.slideLocation.row - 1,
                        column: self.slideLocation.column
                    });
                }
            };

            this.navigateDown = function() {
                if (self.slideLocation.row < self.rows - 1) {
                    self._moveToNewLocation({
                        row: self.slideLocation.row + 1,
                        column: self.slideLocation.column
                    });
                }
            };

            this.navigateLeft = function() {
                if (self.slideLocation.column > 0) {
                    self._moveToNewLocation({
                        row: self.slideLocation.row,
                        column: self.slideLocation.column - 1
                    });
                }
            };

            this.navigateRight = function() {
                if (self.slideLocation.column < self.columns - 1) {
                    self._moveToNewLocation({
                        row: self.slideLocation.row,
                        column: self.slideLocation.column + 1
                    });
                }
            };

            this.navigateHome = function() {
                self._moveToNewLocation({
                    row: 0,
                    column: 0
                });
            };

            this.on("LeftToRightLine", this.navigateLeft);
            this.on("RightToLeftLine", this.navigateRight);
            this.on("TopToBottomLine", this.navigateUp);
            this.on("BottomToTopLine", this.navigateDown);
            this.on("Circle", this.navigateHome);
        },

        constructor: function(params) {
            GestureAwareView.prototype.constructor.apply(this, params);
            this.stage = document.getElementById("stage");
            this.header = document.getElementById("header");
            this.slideContainer = document.getElementById("slide-container");

            this.slideData = [
                {
                    backgroundColor: "#CEDAD0",
                    headerColor: "#93c900",
                    color: "#000000"
                },
                {
                    backgroundColor: "#40403A",
                    headerColor: "#EFFE00",
                    color: "#E9CFC5"
                },
                {
                    backgroundColor: "#39353C",
                    headerColor: "#B7EF00",
                    color: "#FFFFFF"
                },
                {
                    backgroundColor: "#B1AD9C",
                    headerColor: "#2F058B",
                    color: "#120E1C"
                },
                {
                    backgroundColor: "#9B998F",
                    headerColor: "#0F078E",
                    color: "#120E1C"
                },
                {
                    backgroundColor: "#858173",
                    headerColor: "#3F048A",
                    color: "#120E1C"
                },
                {
                    backgroundColor: "#C1C696",
                    headerColor: "#9EB007",
                    color: "#64674A"
                },
                {
                    backgroundColor: "#9CB496",
                    headerColor: "#2C6974",
                    color: "#333333"
                },
                {
                    backgroundColor: "#69524C",
                    color: "#ffffff",
                    headerColor: "#B42E07"
                }
            ];

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
            this._registerGestureListeners();
            this._registerKeyListeners();

            this.stage.style.opacity = 1;

            this.connect();

            this.navigateHome();
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

