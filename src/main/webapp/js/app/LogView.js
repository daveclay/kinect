define(function (require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");

    var LogView = Backbone.View.extend({

        el: $('#log'),

        render: function(params) {
        },

        log: function(message) {
            var existing = this.textarea.innerHTML;
            this.textarea.innerHTML = new Date() + ": " + message + "\n" + existing;
        },

        initialize: function() {
            this.textarea = document.getElementById("log-data");
        }
    });

    return LogView;

});

