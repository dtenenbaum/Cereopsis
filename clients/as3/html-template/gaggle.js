var intervalId = setInterval("flashIsReady()",50);             
//console.log("started timer");

function flashIsReady() {   
    //console.log("interval has passed");
    if (FABridgeReady) {
        clearInterval(intervalId);
        console.log("We are ready to start now.");   
        connect();
    }
}  


function $() {
  var elements = new Array();

  for (var i = 0; i < arguments.length; i++) {
    var element = arguments[i];
    if (typeof element == 'string')
      element = document.getElementById(element);

    if (arguments.length == 1)
      return element;

    elements.push(element);
  }

  return elements;
}





function connect() {
    var flexApp = FABridge.example.root();
    
    //console.log(flexApp.getGoose().getGooseName());  
    
    var gotNamelistBroadcastCallback = function(event) {
        $('lastBroadcastReceived').innerHTML = "<font color='red'>Namelist</font>";
    }  
    
    var gotClusterBroadcastCallback = function(event) {
        $('lastBroadcastReceived').innerHTML = "<font color='red'>Cluster</font>";
    }

    var gotMatrixBroadcastCallback = function(event) {
        $('lastBroadcastReceived').innerHTML = "<font color='red'>Matrix</font>";
    }

    var gotNetworkBroadcastCallback = function(event) {
        $('lastBroadcastReceived').innerHTML = "<font color='red'>Network</font>";
    }

    var gotTupleBroadcastCallback = function(event) {
        $('lastBroadcastReceived').innerHTML = "<font color='red'>Tuple</font>";
    }
    
    var gotGooseNameCallback = function(event) {
    	console.log("in gotGooseNameCallback");
    	console.log("goose name is " + flexApp.getGoose().getGooseName());
    }

    
    var RECEIVED_NAMELIST_EVENT = "receivedNamelist"; 
    var RECEIVED_CLUSTER_EVENT = "receivedClusterEvent";
    var RECEIVED_MATRIX_EVENT = "receivedMatrixEvent";
    var RECEIVED_NETWORK_EVENT = "receivedNetworkEvent";
    var RECEIVED_TUPLE_EVENT = "receivedTupleEvent";
    var RECEIVED_GOOSE_NAME_EVENT = "receivedGooseNameEvent";
    
    flexApp.getGoose().addEventListener(RECEIVED_NAMELIST_EVENT, gotNamelistBroadcastCallback);
    flexApp.getGoose().addEventListener(RECEIVED_CLUSTER_EVENT, gotClusterBroadcastCallback);
    flexApp.getGoose().addEventListener(RECEIVED_MATRIX_EVENT, gotMatrixBroadcastCallback);
    flexApp.getGoose().addEventListener(RECEIVED_NETWORK_EVENT, gotNetworkBroadcastCallback);
    flexApp.getGoose().addEventListener(RECEIVED_TUPLE_EVENT, gotTupleBroadcastCallback);
    flexApp.getGoose().addEventListener(RECEIVED_GOOSE_NAME_EVENT, gotGooseNameCallback);
    
    

    var gotUpdatedGooseNamesCallback = function(event) { 
        //console.log("this is the event we got: " + event);  
        //var target = event.getTarget();                            
        //console.log("Goose is connected with the name " + flexApp.getGoose().getGooseName()); 
        //console.log("Event says the name is: " + event.getGooseName());
        $('gooseName').innerHTML =  "<font color='red'>" + flexApp.getGoose().getGooseName(); + "</font>";
        document.title = "WebPageGoose: " + flexApp.getGoose().getGooseName();
        var gooseNames = event.getGooseNames();         
        var str = "";
        for (var i = 0; i < gooseNames.length; i++) {
            //console.log("\tGoose name: " + gooseNames[i]);
            str += "<li>" + gooseNames[i] + "</li>\n";
        } 
        $('listOfGeese').innerHTML = str;
        return;
    }
    var UPDATED_GOOSE_LIST_EVENT = "updatedGooseListEvent";
    flexApp.getGoose().addEventListener(UPDATED_GOOSE_LIST_EVENT, gotUpdatedGooseNamesCallback);
    flexApp.getGoose().connect("WebPageGoose");

}   

function clearLastBroadcastReceived() {
        $('lastBroadcastReceived').innerHTML = "(none)";
	
}

function sendNamelistBroadcast() {
    //alert("Sending namelist");
    var flexApp = FABridge.example.root();
    var namelist = {"names":["one","two","three"],"name":"a broadcast from web page","species":"moose"};
    flexApp.getGoose().sendNamelist(namelist, "Boss");
}

function sendClusterBroadcast() {
    var flexApp = FABridge.example.root();
    var cluster = {"columnNames":["T000","T120","T240"],"metadata":null,"name":"Sample Cluster from web page","rowNames":["YFL036W","YLR212C","YML085C","YML123C"],"species":"Saccharomyces cerevisiae"}; 
    flexApp.getGoose().sendCluster(cluster, "Boss");
}   
      
function sendMatrixBroadcast() {
    var flexApp = FABridge.example.root();
    var matrix = {"columnCount":4,"columnTitles":["T000","T060","T120","T240"],"data":[[0,0.09,0.18,0.27],[0.38,0.47,0.56,0.65],[0.76,0.85,0.94,1.03],[1.14,1.23,1.32,1.41],[1.52,1.61,1.7,1.79],[1.9,1.99,2.08,2.17],[2.28,2.37,2.46,2.55],[2.66,2.75,2.84,2.93]],"dataTypeBriefName":"","fileExtension":"","fullName":"A random meaningless matrix from a web page","metadata":null,"name":"a sample matrix from a web page","rowCount":8,"rowTitles":["YFL036W","YFL037W","YLR212C","YLR213C","YML085C","YML086C","YML123C","YML124C"],"rowTitlesTitle":"GENE","shortName":"","species":"Saccharomyces cerevisiae"};
    flexApp.getGoose().sendMatrix(matrix, "Boss");
} 

function sendNetworkBroadcast() {
    var flexApp = FABridge.example.root();
    var network = {"edgeAttrNames":["score"],"edgeAttrValues":[0.1,0.35,0.8,0.45,0.4,0.2,0.3,0.55,0.5,0.75],"edgeAttributes":{"0":{"2":6,"4":0,"8":3,"9":1,"6":9,"1":4,"3":5,"7":7,"5":2,"0":8}},"edgeTypes":["GeneCluster","GeneFusion"],"edges":["4,7,0,u","7,0,1,u","7,6,1,u","7,2,1,u","0,3,0,u","0,6,1,u","0,2,1,u","5,2,0,u","6,1,0,u","6,2,1,u"],"name":"a sample network from a web page","nodeAttrNames":["species","moleculeType"],"nodeAttrValues":["Saccharomyces cerevisiae","DNA"],"nodeAttributes":{"1":{"2":1,"4":1,"6":1,"1":1,"3":1,"7":1,"5":1,"0":1},"0":{"2":0,"4":0,"6":0,"1":0,"3":0,"7":0,"5":0,"0":0}},"nodes":["YLR212C","YML086C","YML124C","YLR213C","YFL036W","YML123C","YML085C","YFL037W"],"species":"moose moosculus"};
    flexApp.getGoose().sendNetwork(network, "Boss");
}  

function sendTupleBroadcast() {
    alert("Tuple broadasting is disabled temporarily");
}

function trace(msg) {
	$("output").value = msg.toString() + "\n" + $("output").value;	
}

function getEventTarget(e) {
	if (/Explorer/.test(navigator.appName))
		return e.srcElement;
	else
		return e.target;
}

function dumpit(e) {
	var out = "";
	for (var aProp in e)
		out += ("obj[" + aProp + "] = " + e[aProp]) + "\n";
	trace(out);
}
