define(function(require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var BaseClass = require("app/BaseClass");
    var _ = require("underscore");

    var routerConfigs = [];

    return {

        Param: BaseClass.extend({
            constructor: function(name, value) {
                this.name = name;
                this.value = value;
            }
        }),

        Controller: Backbone.Model.extend({
            run: function(params) {
                console.log(this + ".run(" + params + ")");
            }
        }),

        getRouterConfigs: function() {
            return routerConfigs;
        },

        addRouteConfig: function(routeConfig) {
            routeConfig.link = {};
            routerConfigs.push(routeConfig);
        },

        linkTo: function(page, data) {
            var routeConfig = _.find(routerConfigs, function(routerConfig) {
                return routerConfig.path === page;
            });

            if ( ! routeConfig) {
                console.log("Could not find route for " + page);
            }

            routeConfig.link.data = data;
        }
    };
});