try {
    console.log("App.Initializing");

    var initializeAppComponents = function($) {
        require([
            "app/Router",
            "app/Grid"
        ], function(Router, Grid) {
            var router = Router.createRouter();
            console.log("app initialized");
        });
    };

    require.config({
        baseUrl: 'js',
        paths: {
            jquery: 'vendor/jquery-2.1.1.min',
            backbone: "vendor/backbone",
            iscroll: "vendor/iscroll-zoom",
            underscore: "vendor/underscore",
            swiper: "vendor/idangerous.swiper"
        },

        shim: {
            'underscore': {
                exports: '_'
            },
            "backbone": {
                "deps": [ "underscore", "jquery" ],
                "exports": "Backbone"  //attaches "Backbone" to the window object
            },
           "swiper": {
                exports: "Swiper"
            },
            "iscroll": {
                exports: "IScroll"
            }
        }
    });

    require([
        "jquery"
        ], function($) {

        console.log("jquery initialized");
        initializeAppComponents($);
    } );


} catch (exception) {
    console.log(exception);
}
