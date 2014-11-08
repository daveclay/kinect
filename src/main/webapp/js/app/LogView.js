define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");

    var LogView = Backbone.View.extend({

        el: $('#log'),

        render: function(params) {
            this.textarea = document.getElementById("log-data");
        },

        log: function(message) {
            var existing = this.textarea.innerHTML;
            this.textarea.innerHTML = new Date() + ": " + message + existing;
        },

        initialize: function() {
        }
    });

    return LogView;

});

