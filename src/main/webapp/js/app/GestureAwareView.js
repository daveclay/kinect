define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var LogView = require("app/LogView");

    var GestureAwareView = Backbone.View.extend({

        constructor: function(params) {
            Backbone.View.apply(this, params);
            this.listeners = [];
            this.logView = new LogView();
        },

        on: function(gesture, callback) {
            this.listeners[gesture] = callback;
        },

        log: function(msg) {
            this.logView.log(msg);
        },

        connect: function(uri) {
            var self = this;
            uri = uri || "ws://localhost:12345";
            var ws = new WebSocket(uri);
            ws.onopen = function() {
                self.log("[WebSocket#onopen]\n");
            };
            ws.onmessage = function(event) {
                var payload = JSON.parse(event.data);
                self.log("[WebSocket#onmessage] Message: '" + payload + "'\n");
                // { type: 'userGestureRecognized', data: { name: '" + gesture.name + "', score: " + gesture.score + " }}
                // { type: 'userDidEnterZone', data: { zone: '" + stageZone.getID() + "'}}
                if (payload.type === 'userGestureRecognized') {
                    var listener = self.listeners[payload.data.name];
                    if (listener) {
                        listener(payload.data);
                    }
                }
            };
            ws.onclose = function() {
                self.log("[WebSocket#onclose]\n");
                ws = null;
            };
        }

    });

    return GestureAwareView;

});

