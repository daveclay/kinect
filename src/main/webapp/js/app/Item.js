define(function (require) {
    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var Numbers = require("app/util/Numbers");
    require("tweenmax");

    return Backbone.Model.extend({

        constructor: function (index, x, options) {
            this.x = x;
            this.index = index;
            this.options = options;

            var itemElement = $("<div/>");
            itemElement.append(options.template(this));
            itemElement.addClass("item");
            this.element = itemElement;
            this.enterComplete = false;

            options.initial(this);
        },

        enter: function () {
            if (this.enterTween && this.enterTween.isActive()) {
                return;
            }
            this.enterComplete = 0;
            this.enterTween = this.options.enter(this);
        },

        exit: function () {
            if (this.exitTween && this.exitTween.isActive()) {
                return;
            }
            this.enterTween.kill();
            this.enterTween.eventCallback("onComplete", null);
            this.exitTwean = this.options.exit(this);
        }
    })
});
