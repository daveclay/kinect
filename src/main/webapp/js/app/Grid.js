define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");
    var GestureAwareView = require("app/GestureAwareView");

    HTMLElement.prototype.findChildWithClass = function(className) {
        return _.find(this.childNodes, function(childElement) {
            if (childElement.nodeType == 1) {
                var classes = childElement.getAttribute("class").split(" ");
                return _.contains(classes, className);
            } else {
                return false;
            }
        });
    };

    var keys = {
        z: 90,
        h: 72,
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
                self.log(event.keyCode + "\n");
                switch (event.keyCode) {
                    case keys.z:
                        event.preventDefault();
                        self.zoom();
                        break;
                    case keys.h:
                        event.preventDefault();
                        self.navigateHome();
                        break;
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
                slideLeaving.slideElement.style.opacity = 0;
                // slideLeaving.titleElement.style.transform = "translate3d(50%, 0, 0)";
                slideLeaving.titleElement.style.opacity = 0;
                this.slideLocation = newLocation;
            }

            var slideX = -100 * this.slideLocation.column;
            var slideY = -100 * this.slideLocation.row;
            var backgroundX = -35 * this.slideLocation.column;
            var backgroundY = -30 * this.slideLocation.row;

            this.backgroundElement.style.transform = "translate3d(" + backgroundX + "%, " + backgroundY + "%, 0)";

            var slideEntering = this._getSlideAtLocation();
            var slideElement = slideEntering.slideElement;
            var titleElement = slideEntering.titleElement;

            this.stage.style.backgroundColor = slideEntering.slideStyle.backgroundColor;
            this.header.style.backgroundColor = slideEntering.headerStyle.backgroundColor;
            this.header.style.color = slideEntering.headerStyle.color;
            slideElement.style.opacity = 1;
            slideElement.style.color = slideEntering.slideStyle.color;
            titleElement.style.color = slideEntering.headerStyle.color;
            titleElement.style.opacity = 1;

            this.slideContainer.style.transform = "translate3d(" + slideX + "%, " + slideY + "%, 0)";
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

            this.zoom = function() {
            };

            this.onGesture("LeftToRightLine", this.navigateLeft);
            this.onGesture("RightToLeftLine", this.navigateRight);
            this.onGesture("TopToBottomLine", this.navigateUp);
            this.onGesture("BottomToTopLine", this.navigateDown);
            this.onGesture("Circle", this.navigateHome);
        },

        constructor: function(params) {
            GestureAwareView.prototype.constructor.apply(this, params);
            this.body = document.getElementsByTagName("body")[0];
            this.stage = document.getElementById("stage");
            this.header = document.getElementById("header");
            this.backgroundElement = document.getElementById("background");
            this.titleContainer = document.getElementById("title-container");
            this.slideContainer = document.getElementById("slide-container");

            this.rows = 3;
            this.columns = 3;
            this.slides = [];
            var count = 1;
            for (var row = 0; row < this.rows; row++) {
                this.slides[row] = [];
                for (var column = 0; column < this.columns; column++) {
                    var slideElement = document.getElementById("slide" + count);
                    var slideHeaderElement = slideElement.findChildWithClass("header-title");

                    var slideStyle = window.getComputedStyle(slideElement);
                    var headerStyle = window.getComputedStyle(slideHeaderElement);

                    this.slides[row][column] = {
                        slideStyle: {
                            backgroundColor: slideStyle.backgroundColor
                        },
                        headerStyle: {
                            backgroundColor: headerStyle.backgroundColor,
                            color: headerStyle.color
                        },
                        slideElement: slideElement,
                        titleElement: slideHeaderElement
                    };

                    slideElement.style.background = "none";
                    slideElement.style.width = window.innerWidth + "px";
                    slideElement.style.height = window.innerHeight + "px";
                    slideHeaderElement.style.backgroundColor = "none";
                    slideHeaderElement.style.opacity = 0;

                    this.titleContainer.appendChild(slideHeaderElement);
                    count++;
                }
            }

            this.slideLocation = {
                row: 0,
                column: 0
            };
            this._registerGestureListeners();
            this._registerKeyListeners();

            var self = this;
            this.onConnect(function() {
                self.stage.style.opacity = 1;
                self.navigateHome();
            });

            this.connect();
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

