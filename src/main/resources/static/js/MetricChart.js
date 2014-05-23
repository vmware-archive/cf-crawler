function MetricChart(container) {
    var self = this;
    self.randomData;

    self.chart = new Highcharts.Chart({
        chart: {
            type: 'area',
            renderTo: container,
            backgroundColor:'rgba(255, 255, 255, 0.1)',
            events: {
                load: function () {
                    self.randomData = this.series[0];
                }
            }
        },

        title: {
            text: false
        },
        xAxis: {
            type: 'datetime',
            minRange: 60 * 1000,
            lineWidth: 0,
            minorGridLineWidth: 0,
            lineColor: 'transparent',

            labels: {
                enabled: false
            },
            minorTickLength: 0,
            tickLength: 0
        },
        yAxis: {
            title: {
                text: false
            },
            lineWidth: 0,
            minorGridLineWidth: 0,
            lineColor: 'transparent',

            labels: {
                enabled: false
            },
            minorTickLength: 0,
            tickLength: 0
        },
        legend: {
            enabled: false
        },
        plotOptions: {
            series: {
                threshold: 0,
                marker: {
                    enabled: false
                }
            }
        },
        series: [{
            name: 'Data',
            data: [],
            fillColor: {
                linearGradient: {
                    x1: 0,
                    y1: 0,
                    x2: 0,
                    y2: 1
                },
                stops: [
                    [0, Highcharts.getOptions().colors[0]],
                    [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                ]
            },
            threshold: null
        }]
    });




self.addData = function (point) {
    var shift = self.randomData.data.length > 60;
    self.randomData.addPoint(point, true, shift);
};

}