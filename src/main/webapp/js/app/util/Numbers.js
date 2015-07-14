define(function (require) {

    return {
        limitNegatives: function(value) {
            if (value < 0) {
                value = 0;
            }
            return value;
        },
        map: function(value, start1, stop1, start2, stop2) {
            var value = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));

            return value;
        }
    };

});
