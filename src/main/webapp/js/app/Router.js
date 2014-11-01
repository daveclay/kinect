define(function(require) {

    var $ = require("jquery");
    var Backbone = require("backbone");
    var _ = require("underscore");
    var mvc = require("app/mvc");

    var allViews = [];

    // Extends Backbone.Router
    var Router = Backbone.Router.extend( {

        constructor: function(routeConfigs) {
            console.log("Router.constructor");
            if (routeConfigs){
                this._configureRoutes(routeConfigs);
            }

            Backbone.Router.prototype.constructor.call(this, arguments);
        },

        _configureRoutes: function(routeConfigs){
            var self = this;
            _.forEach(routeConfigs, function(routeConfig) {
                self._addRouteConfig(routeConfig);
            });
        },

        _hideOtherViews: function(view) {
            _.each(allViews, function(otherView) {
                if (view !== otherView) {
                    if (_.isFunction(otherView.hide)) {
                        otherView.hide();
                    }
                    otherView.$el.hide();
                }
            });
            view.$el.show();
        },

        _isRequestParamString: function(param) {
            return param.indexOf("=") > 0;
        },

        _isRequestParam: function(param) {
            return _.isString(param) && this._isRequestParamString(param);
        },

        _parseNameValuePairs: function(params) {
            var pairs = {};
            var vars = params.split('&');
            for (var i = 0; i < vars.length; i++) {
                var pair = vars[i].split('=');
                var name = decodeURIComponent(pair[0]);
                var value = decodeURIComponent(pair[1]);

                pairs[name] = value;
            }

            return pairs;
        },

        _parseNameValueRequestParams: function(args) {
            var self = this;
            var parsedArgs = {};
            _.each(args, function(arg) {
                if (self._isRequestParam(arg)) {
                    var parsedParams = self._parseNameValuePairs(arg);
                    for (var name in parsedParams) {
                        parsedArgs[name] = parsedParams[name];
                    }
                }
            });

            return parsedArgs;
        },

        _addRouteConfig: function(routeConfig) {
            console.log("adding route:");
            console.log(routeConfig);
            var self = this;

            var path = routeConfig.path;
            var name = routeConfig.name || routeConfig.path;
            var controller = routeConfig.controller;
            var view = routeConfig.view;
            var authRequired = routeConfig.authRequired || false;
            var redirectTo = routeConfig.redirectTo;

            var callback = function() {
                // add the view as the first argument to the controller
                var args = Array.prototype.slice.call(arguments);

                var requestParams = self._parseNameValueRequestParams(args);

                // Call the controller run() method with the view and any arguments
                var data = routeConfig.link.data;
                var param = {
                    data: data,
                    view: view,
                    requestParameters: requestParams
                };

                console.log("Calling controller for " + path + " with ", param);
                controller.run.call(controller, param);

                if (redirectTo) {
                    self.navigate(redirectTo, { trigger: true });
                } else {
                    self._hideOtherViews(view);
                    // notify the view that is to be rendered.
                    view.render();
                }
            };

            // invoke the Backbone route() method to register the function that calls
            // the controller and page change.
            this.route(path, name, callback);

            if (view) {
                // some controllers simply redirect (like Logout)
                allViews.push(view);
            }

            if (routeConfig.defaultRoute) {
                console.log("added default route to " + name);
                this.route("", name, callback);
            }
        },

        // The Router constructor
        initialize: function() {
            console.log( "Router.initialize" );
            Backbone.history.start();
        }
    } );

    return {
        router: null,

        createRouter: function() {
            this.router = new Router(mvc.getRouterConfigs());
            return this.router;
        }
    };
} );