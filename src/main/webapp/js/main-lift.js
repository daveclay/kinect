try {
    console.log("App.Initializing");

    var initializeAppComponents = function($) {
        require([
            "app/Router",
            "app/Lift"
        ], function(Router) {
            var router = Router.createRouter();
            console.log("app initialized");
        });
    };

    require.config({
        baseUrl: 'js',
        paths: {
            jquery: 'vendor/jquery-2.1.1.min',
            backbone: "vendor/backbone",
            underscore: "vendor/underscore"
        },

        shim: {
            'underscore': {
                exports: '_'
            },
            "backbone": {
                "deps": [ "underscore", "jquery" ],
                "exports": "Backbone"  //attaches "Backbone" to the window object
            }
        }
    });

    require([
        "jquery",
        "vendor/RAF"
        ], function($) {
        console.log("jquery initialized");
        initializeAppComponents($);
    } );


} catch (exception) {
    console.log(exception);
}
