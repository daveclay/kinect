define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var LogView = require("app/LogView");

    var min = 10000;
    var max = -10000;

    var GestureAwareView = Backbone.View.extend({

        constructor: function(params) {
            Backbone.View.apply(this, params);
            this.listeners = [];
            this.logView = new LogView();
            this.connectCallback = function() {};
            var self = this;
            this.messageWasReceivedHandler = function(event) {
                self.messageWasReceived(event)
            };
            this.connectionWasOpenedHandler = function(event) {
                self.log("[WebSocket connection opened]");
                self.connectCallback();
            };
            this.connectionWasClosedHandler = function(event) {
                self.log("[WebSocket connection closed]");
                self.cleanup();
            };
            this.errorWasReceivedHandler = function(event) {
                console.log(event);
                self.log("[Websocket error: " + event + "]");
                setTimeout(function() {
                    self.connect(self.uri);
                }, 5000);
            }
        },

        cleanup: function() {
            this.ws.onopen = null;
            this.ws.onmessage = null;
            this.ws.onclose = null;
            this.ws.onerror = null;
            this.ws = null;
        },

        onConnect: function(callback) {
            this.connectCallback = callback;
        },

        onGesture: function(gesture, callback) {
            this.listeners[gesture] = callback;
        },

        onUserDidMove: function(callback) {
            this.userDidMove = callback;
        },

        log: function(msg) {
            console.log(msg);
            this.logView.log(msg);
        },

        messageWasReceived: function(event) {
            var payload;
            try {
                payload = JSON.parse(event.data);
            } catch (error) {
                console.log("BAD MESSAGE: ", error);
            }
            if (!payload) {
                return;
            }

            // { type: 'userGestureRecognized', data: { name: '" + gesture.name + "', score: " + gesture.score + " }}
            // { type: 'userDidEnterZone', data: { zone: '" + stageZone.getID() + "'}}
            if (payload.type === 'userGestureRecognized') {
                var listener = this.listeners[payload.data.name];
                if (listener) {
                    listener(payload.data);
                }
            } else if (payload.type === 'userDidMove') {
                var position = payload.data;
                if (position.x == 0 && position.y == 0 && position.z == 0) {
                    return;
                }

                if (this.userDidMove) {
                    this.userDidMove(position);
                }
            }
        },

        connect: function(uri) {
            this.uri = uri || "ws://localhost:12345";
            var ws = new WebSocket(this.uri);
            this.registerHandlers(ws);
        },

        registerHandlers: function(ws) {
            ws.onopen = this.connectionWasOpenedHandler;
            ws.onmessage = this.messageWasReceivedHandler;
            ws.onclose = this.connectionWasClosedHandler;
            ws.onerror = this.errorWasReceivedHandler;
            this.ws = ws;
        }

    });

    return GestureAwareView;

});

