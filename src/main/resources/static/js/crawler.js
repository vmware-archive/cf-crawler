var session, runningTime, tickTimer, spinner, pagesPerSec, bytesPerSec, start;
var chart1, chart2;
var graph = new DomainGraph();
var ws = new SockJS(window.location+'stomp');
var client = Stomp.over(ws);
client.heartbeat.outgoing = 0;
client.heartbeat.incoming = 0;
var pageSubscription, linkSubscription;
var pageStats = {count:0, bytes:0, links:0, time:0};
var lastTick = {count:0, bytes:0, links:0, time:0};
var tickInterval = 1000;
client.debug = function() {
	
};



Object.defineProperty(Number.prototype,'fileSize',{value:function(a,b,c,d){
	 return (a=a?[1e3,'k','B']:[1024,'K','iB'],b=Math,c=b.log,
	 d=c(this)/c(a[0])|0,this/b.pow(a[0],d)).toFixed(2)
	 +' '+(d?(a[1]+'MGTPEZY')[--d]+a[2]:'Bytes');
	},writable:false,enumerable:false});

function reset() {
	 $("#sessionStart").text("");
	 pageStats = {count:0, bytes:0, links:0, time:0};
	 lastTick = {count:0, bytes:0, links:0, time:0};
	 chart1.reset();
	 chart2.reset();
}


function tick(){
	pagesPerSec = (pageStats.count - lastTick.count)/(tickInterval/1000);
	
	bytesPerSec = (pageStats.bytes - lastTick.bytes)/(tickInterval/1000);
	
	console.log("pagestats : " + JSON.stringify(pageStats));
	console.log("tickStats : " + JSON.stringify(lastTick));
	console.log("bytes/sec: " + bytesPerSec);
	console.log("pages/sec: " + pagesPerSec);
	console.log("------------------------------------------------------------------------------------------------------------------------------------------------------------")
	lastTick.count = pageStats.count;
	lastTick.bytes = pageStats.bytes;
	chart1.addData([Date.now(),pagesPerSec]);
	chart2.addData([Date.now(),bytesPerSec]);
	if((Date.now() - lastTick.time) > 5000){
	    $("#sessionStatus").text("Finished");
		$("#sessionStatus").css("color: blue");
		$("#url").removeAttr("disabled");
		$("#draw").show();
		spinner.stop();
		clearInterval(tickTimer);
		unsubscribe();
	}
}

var onPageMessage = function(frame){
	lastTick.time = Date.now();
	var page = JSON.parse(frame.body);
	pageStats.count++;
	pageStats.links += page.numLinks;
	pageStats.bytes += page.size;
	pageStats.time += page.crawlTime;
	runningTime = Date.now() - start;
	updateStatus();
}

var onLinkMessage = function(frame){
	lastTick.time = Date.now();
	var link = JSON.parse(frame.body);
	var fromNode = chart.createNode(link.source,0);
	var toNode = chart.createNode(link.destination,link.weight);
	chart.createEdge({
		id : fromNode.id+"-"+toNode.id,
		from : fromNode.id,
		to : toNode.id
	});
	
}




var error_callback = function(error) {
    alert(error.headers.message);
};

var connect_callback = function() {
	console.log("Connected");
  };

function unsubscribe(){
	pageSubscription.unsubscribe();
	linkSubscription.unsubscribe();
}

function subscribe(crawlerSession){
	session = crawlerSession;
	start = Date.now();
	lastTick.time = Date.now();
	$("#sessionStart").text(start);
	$("#sessionStatus").text("Running");
	$("#sessionStatus").css("color: green");
	pageSubscription = client.subscribe("/queue/page_"+session.id, onPageMessage,{id:"page_"+session.id});
	linkSubscription = client.subscribe("/queue/link_"+session.id, onLinkMessage,{id:"links_"+session.id});
	tickTimer = setInterval(tick,tickInterval);
}

function updateStatus(){

	$("#sessionPages").text(pageStats.count);
	$("#sessionLinks").text(pageStats.links);
	$("#sessionBytes").text(pageStats.bytes.fileSize());
	$("#sessionTime").text(pageStats.time +" ms");
	$("#sessionRunning").text(runningTime +" ms");
	$("#sessionPageSec").text(pagesPerSec.toFixed(2));
	$("#sessionByteSec").text(bytesPerSec.fileSize());
	
	
}







function start(){
	reset();
	var sessionUrl = $("#url").val();
	if(sessionUrl.lastIndexOf("http://",0) != 0){
		sessionUrl = "http://"+sessionUrl;
	}
	$("#url").val(sessionUrl);
	var session = {url:sessionUrl, maxDepth: $("#depth").val()};
	
	$.ajax({
			type: "POST",
			data: JSON.stringify(session),
			url: "session/start",
			contentType: "application/json",
			dataType: 'json',
			success : function (data){
				subscribe(data);
			}
	});
}

$(document).ready(function() {
	chart1 = new MetricChart("pageSecChart");
	chart2 = new MetricChart("byteSecChart");
	reset();
	client.connect({},connect_callback,error_callback);
	$("#fetch").click(function(e) {
		e.preventDefault();
		$("#draw").hide();
		$("#url").attr("disabled","disabled");
		spinner = Ladda.create(this);
		spinner.start();
		start();
		$("#disconnect").show();
	});
	$("#draw").click(function(){
		chart.start();
	});
	$("#draw").hide();
	$("#dump").click(function(){
		nodes.forEach(function(data){console.log(data)});
		edges.forEach(function(data){console.log(data)});
	});
});











function CrawlerGraph(){
	var self = this;
	
	self.graph;
	
	self.nodes = new vis.DataSet();
	self.edges = new vis.DataSet();
	
	var container = $('#graph').get(0);
	var options = {
			nodes : {
				radius : 5,
				shape : "dot"
			},
			edges : {
				style: "arrow"
			},
			physics: {barnesHut: {gravitationalConstant: -16350, centralGravity: 0.75, springLength: 120, springConstant: 0.159, damping: 0.16}},
			stabilize: false,
			tooltip : {
				delay : 300,
				fontColor : "black",
				fontSize : 11, // px
				fontFace : "verdana",
				color : {
					border : "#666",
					background : "#FFFFC6"
				}
			}
		};
	
	self.start = function(){
		self.graph = new vis.Graph(container, {nodes:self.nodes, edges:self.edges}, options);
		self.graph.on('select', function(properties) {
		   console.log(properties);
		  });
	}
	
	self.createNode = function(page, weight){
		if(!weight){
			weight = 0;
		}
		var node = {id:page.id, color: self.color(page.responseCode), value: 5+weight, mass:1, title : page.url, label : "" }
		var oldNode = nodes.get(node.id);
		if(oldNode){
			oldNode.color = node.color;
			oldNode.value += weight;
			oldNode.mass += node.mass;
			self.nodes.update(oldNode);
			node = oldNode;
		}else{
			self.nodes.add(node);
		}
		return node;
	}
	
	self.addEdge = function (edge){
		self.edges.update(edge);
	}
	 self.color = function(responseCode){
		var color;
		if(responseCode == 0){
			color = "blue";
		}
		else if(responseCode>=200&&responseCode<300){
			color = "#00FF00";
		}else if(responseCode > 300){
			color = "#FF0000"
		}
		return color;
	}
	
}









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
            dateTimeLabelFormats: '%H:%M:%S',
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

self.reset = function() {
	self.randomData.setData([],true);
}

}
