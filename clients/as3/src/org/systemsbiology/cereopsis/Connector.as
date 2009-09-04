package org.systemsbiology.cereopsis
{
	import com.adobe.serialization.json.JSON;
	
	import flash.events.ErrorEvent;
	import flash.events.EventDispatcher;
	import flash.events.IOErrorEvent;
	import flash.events.SecurityErrorEvent;
	import flash.net.Socket;
	import flash.system.Security;
	
	import mx.controls.Alert;
	import mx.utils.UIDUtil;
	
	import org.codehaus.stomp.Stomp;
	import org.codehaus.stomp.event.ConnectedEvent;
	import org.codehaus.stomp.event.MessageEvent;
	import org.codehaus.stomp.headers.ConnectHeaders;
	import org.codehaus.stomp.headers.SendHeaders;
	import org.codehaus.stomp.headers.SubscribeHeaders;
	import org.systemsbiology.cereopsis.events.LoggingEvent;
	import org.systemsbiology.cereopsis.events.ReceivedClusterEvent;
	import org.systemsbiology.cereopsis.events.ReceivedGooseNameEvent;
	import org.systemsbiology.cereopsis.events.ReceivedMatrixEvent;
	import org.systemsbiology.cereopsis.events.ReceivedNamelistEvent;
	import org.systemsbiology.cereopsis.events.ReceivedNetworkEvent;
	import org.systemsbiology.cereopsis.events.UpdatedGooseListEvent;
		
	public class Connector extends EventDispatcher
	{
		
		
		
		
		[Event(name="loggingEvent", type="org.systemsbiology.cereopsis.events.LoggingEvent")]
		[Event(name="receivedNamelistEvent", type="org.systemsbiology.cereopsis.events.ReceivedNamelistEvent")]
		[Event(name="receivedNetworkEvent", type="org.systemsbiology.cereopsis.events.ReceivedNetworkEvent")]
		[Event(name="receivedClusterEvent", type="org.systemsbiology.cereopsis.events.ReceivedClusterEvent")]
		[Event(name="receivedMatrixEvent", type="org.systemsbiology.cereopsis.events.ReceivedMatrixEvent")]
		[Event(name="updatedGooseListEvent", type="org.systemsbiology.cereopsis.events.UpdatedGooseListEvent")]
		[Event(name="receivedGooseNameEvent", type="org.systemsbiology.cereopsis.events.ReceivedGooseNameEvent")]
		
		
		private var stomp:Stomp;
		private var sessionId:String;
		
		public var gooseName:String;
		public var gooseNames:Array;
		
		private var uid:String;
		
		public function Connector()
		{
			uid = UIDUtil.createUID();
			
		}
		
		public function connect(desiredName:String):void {
			stomp = new Stomp();
			stomp.autoReconnect = false;
			
			stomp.addEventListener(IOErrorEvent.IO_ERROR, errorHandler);
			stomp.addEventListener(IOErrorEvent.NETWORK_ERROR, errorHandler);
			stomp.addEventListener(ErrorEvent.ERROR, errorHandler);
			stomp.addEventListener(SecurityErrorEvent.SECURITY_ERROR, errorHandler);

			stomp.addEventListener(ConnectedEvent.CONNECTED, function(event:ConnectedEvent):void {
				sessionId = stomp.sessionID;
				log("Connected: this goose has the unique ID: " + sessionId);  
				getGooseNames();
			});
				
		    var ch: ConnectHeaders = new ConnectHeaders();
			ch.login = uid;
			ch.passcode = "guest";
			
			addEventListener(IOErrorEvent.NETWORK_ERROR, errorHandler);
			addEventListener(IOErrorEvent.IO_ERROR, errorHandler);
			addEventListener(ErrorEvent.ERROR, errorHandler);
			addEventListener(SecurityErrorEvent.SECURITY_ERROR, errorHandler);


			Security.loadPolicyFile("xmlsocket://127.0.0.1:9876");


			var sock:Socket = new Socket();
			sock.addEventListener(IOErrorEvent.NETWORK_ERROR, errorHandler);
			sock.addEventListener(IOErrorEvent.IO_ERROR, errorHandler);
			sock.addEventListener(ErrorEvent.ERROR, errorHandler);
			sock.addEventListener(SecurityErrorEvent.SECURITY_ERROR, errorHandler);

			sock.connect("127.0.0.1", 61613);
			
			
			stomp.connect("127.0.0.1", 61613, ch);//you have to say 127.0.0.1 instead of localhost--and/or match what you say in Security.loadPolicyFile()
			
			var subscribeHeaders:SubscribeHeaders = new SubscribeHeaders();
			subscribeHeaders.addHeader(SubscribeHeaders.AMQ_NO_LOCAL, true);
			stomp.subscribe("/topic/Broadcast", subscribeHeaders);
			
			//getGooseNames();
			
			
			stomp.addEventListener(MessageEvent.MESSAGE, function(event:MessageEvent):void {
				var l:String = "";
				l += "Got event:\nHeaders:\n";
				for each (var header:String in event.message.headers) {
					l += header;
					l += ": ";
					l += event.message.headers[header];
					l += "\n";
				}
				l += "\nBody:\n";
				l += event.message.body;
				log(l);
				if (event.message.headers["MessageType"] && event.message.headers["MessageType"] == "Namelist") {
					var namelist:Object = JSON.decode(event.message.body.toString());
					var namelistEvent:ReceivedNamelistEvent = new ReceivedNamelistEvent(ReceivedNamelistEvent.RECEIVED_NAMELIST_EVENT);
					namelistEvent.namelist = namelist;
					dispatchEvent(namelistEvent);	 
				} else if (event.message.headers["MessageType"] && event.message.headers["MessageType"] == "Network") {
					var network:Object = JSON.decode(event.message.body.toString());
					var networkEvent:ReceivedNetworkEvent = new ReceivedNetworkEvent(ReceivedNetworkEvent.RECEIVED_NETWORK_EVENT);
					networkEvent.compactNetwork = network;
					dispatchEvent(networkEvent);
				} else if (event.message.headers["MessageType"] && event.message.headers["MessageType"] == "Cluster") {
					var cluster:Object = JSON.decode(event.message.body.toString());
					var clusterEvent:ReceivedClusterEvent = new ReceivedClusterEvent(ReceivedClusterEvent.RECEIVED_CLUSTER_EVENT);
					clusterEvent.cluster = cluster;
					dispatchEvent(clusterEvent);
				} else if (event.message.headers["MessageType"] && event.message.headers["MessageType"] == "DataMatrix") {
					var matrix:Object = JSON.decode(event.message.body.toString());
					var matrixEvent:ReceivedMatrixEvent = new ReceivedMatrixEvent(ReceivedMatrixEvent.RECEIVED_MATRIX_EVENT);
					matrixEvent.matrix = matrix;
					dispatchEvent(matrixEvent);
				} else if (event.message.headers["MessageType"] && event.message.headers["MessageType"] == "GooseList") {
					var tmpObj:Object = JSON.decode(event.message.body.toString());
					gooseNames = tmpObj['GooseList'];
					gooseNames.push("Boss");
					filterGooseNames();
					gooseNames.sort();
					var gooseListEvent:UpdatedGooseListEvent = new UpdatedGooseListEvent(UpdatedGooseListEvent.UPDATED_GOOSE_LIST_EVENT);
					gooseListEvent.gooseNames = gooseNames;
					dispatchEvent(gooseListEvent);
				} else if (event.message.headers["MessageType"] && event.message.headers["MessageType"] == "GooseName") {
					var tmp:Object = JSON.decode(event.message.body.toString());
					gooseName =  tmp['GooseName'];
					filterGooseNames();
					if (gooseNames != null) {
						var glEvent:UpdatedGooseListEvent = new UpdatedGooseListEvent(UpdatedGooseListEvent.UPDATED_GOOSE_LIST_EVENT);
						glEvent.gooseNames = gooseNames;
						dispatchEvent(glEvent);
						
					}
					var gotGooseNameEvent:ReceivedGooseNameEvent = new ReceivedGooseNameEvent(ReceivedGooseNameEvent.RECEIVED_GOOSE_NAME_EVENT);
					gotGooseNameEvent.gooseName = gooseName;
					dispatchEvent(gotGooseNameEvent);
				}
			});

		}
		
		private function filterGooseNames():void {
			if (gooseNames != null && gooseName != null) {
				var gooseNamesCopy:Array = new Array();
					for each (var item:String in gooseNames) {
						if (item != gooseName) {
							gooseNamesCopy.push(item);
						}
					}
					gooseNames = gooseNamesCopy;
			}
		}
		
		private function getGooseNames():void {
			var sendHeaders:SendHeaders = new SendHeaders();
			sendHeaders.addHeader("MessageType", "RequestGooseName");
			var dummyObject:Object = {"dummy": "value"};
			sendData(sendHeaders, JSON.encode(dummyObject));
		}
		
		private function errorHandler(event:ErrorEvent):void {
			trace("in error handler: " + event.text)
			Alert.show("Connection failure. Are the Boss and Cereopsis goose running? If not, start them and refresh this page.", "Error");
		}
		
    	private function log(msg:String):void {
			trace(msg);
			var le:LoggingEvent = new LoggingEvent(LoggingEvent.LOGGING_EVENT);
			le.logEntry = msg;
			dispatchEvent(le);
		}

		public function sendData(headers:SendHeaders, payload:String):void {
			try {
				stomp.sendTextMessage("/topic/Broadcast", payload, headers);
			} catch (e:Error) {
				Alert.show("sendData error", "Error");
			}
		}
		
		public function sendNamelist(namelist:Object, target:String):void {
			var sendHeaders:SendHeaders = new SendHeaders();
			var nameJson:String = JSON.encode(namelist);
			sendHeaders.addHeader("MessageType", "Namelist");
			sendHeaders.addHeader("Target", target);
			try {
				stomp.sendTextMessage("/topic/Broadcast", nameJson, sendHeaders);
			} catch (e:Error) {
				Alert.show("Goose and/or boss have gone away. Restart them and (possibly) refresh this page. Working on more graceful approach.", "Error");
			}
		}
		
		public function sendNetwork(network:Object, target:String):void {
			var sendHeaders:SendHeaders = new SendHeaders();
			var networkJson:String = JSON.encode(network);
			sendHeaders.addHeader("MessageType", "Network");
			sendHeaders.addHeader("Target", target);
			
			try {
				stomp.sendTextMessage("/topic/Broadcast", networkJson, sendHeaders);
			} catch (e:Error) {
				Alert.show("Goose and/or boss have gone away. Restart them and (possibly) refresh this page. Working on more graceful approach.", "Error");
			}
		}
		
		public function sendMatrix(matrix:Object, target:String):void {
			var sendHeaders:SendHeaders = new SendHeaders();
			var matrixJson:String = JSON.encode(matrix);
			sendHeaders.addHeader("MessageType", "DataMatrix");
			sendHeaders.addHeader("Target", target);
			
			try {
				stomp.sendTextMessage("/topic/Broadcast", matrixJson, sendHeaders);
			} catch (e:Error) {
				Alert.show("Goose and/or boss have gone away. Restart them and (possibly) refresh this page. Working on more graceful approach.", "Error");
			}
		}
		
		public function sendCluster(cluster:Object, target:String):void {
			var sendHeaders:SendHeaders = new SendHeaders();
			var clusterJson:String = JSON.encode(cluster);
			sendHeaders.addHeader("MessageType", "Cluster");
			sendHeaders.addHeader("Target", target);
			
			try {
				stomp.sendTextMessage("/topic/Broadcast", clusterJson, sendHeaders);
			} catch (e:Error) {
				Alert.show("Goose and/or boss have gone away. Restart them and (possibly) refresh this page. Working on more graceful approach.", "Error");
			}
		}
		
		public function sendHideMessage(target:String):void {
			var sendHeaders:SendHeaders = new SendHeaders();
			var json:String = JSON.encode({"dummy object": "dummy value"});
			sendHeaders.addHeader("MessageType", "Hide");
			sendHeaders.addHeader("Target", target);
			
			try {
				stomp.sendTextMessage("/topic/Broadcast", json, sendHeaders);
			} catch (e:Error) {
				Alert.show("Goose and/or boss have gone away. Restart them and (possibly) refresh this page. Working on more graceful approach.", "Error");
			}
		}
		
		public function sendShowMessage(target:String):void {
			var sendHeaders:SendHeaders = new SendHeaders();
			var json:String = JSON.encode({"dummy object": "dummy value"});
			sendHeaders.addHeader("MessageType", "Show");
			sendHeaders.addHeader("Target", target);
			
			try {
				stomp.sendTextMessage("/topic/Broadcast", json, sendHeaders);
			} catch (e:Error) {
				Alert.show("Goose and/or boss have gone away. Restart them and (possibly) refresh this page. Working on more graceful approach.", "Error");
			}
		}

	}
}