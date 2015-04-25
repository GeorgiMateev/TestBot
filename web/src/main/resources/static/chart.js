$(function () {
    $('#chart-container').highcharts({
        chart: {
            zoomType: 'x'
        },
        title: {
            text: 'Amount of bugs in the interval from 2013 through 2014'
        },
        subtitle: {
        //     text: document.ontouchstart === undefined ?
        //             'Click and drag in the plot area to zoom in' :
        //             'Pinch the chart to zoom in'
        },
        xAxis: {
            type: 'datetime',
            minRange: 14 * 24 * 3600000 // fourteen days
        },
        yAxis: {
            title: {
                text: 'Amount of bugs'
            }
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            area: {
                fillColor: {
                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                    stops: [
                        [0, Highcharts.getOptions().colors[0]],
                        [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                    ]
                },
                marker: {
                    radius: 2
                },
                lineWidth: 1,
                states: {
                    hover: {
                        lineWidth: 1
                    }
                },
                threshold: null
            }
        },

        series: [{
            type: 'area',
            name: 'Bugs that day',
            pointInterval: 24 * 3600 * 1000,
            pointStart: Date.UTC(2006, 0, 1),
            data: [
                 38, 42, 44, 39, 37, 37, 36, 30, 31, 29, 29, 26
            ]
        }]
    });
});