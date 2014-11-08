define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");

    var GestureAwareView = Backbone.View.extend({
        constructor: function(params) {
            Backbone.View.apply(this, params);
            this.listeners = [];
        },

        on: function(gesture, callback) {
            this.listeners[gesture] = callback;
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
                    self.listeners[payload.data.name]();
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

