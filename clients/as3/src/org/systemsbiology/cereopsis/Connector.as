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
	import org.systemsbiology.cereopsis.events.LoggingEvent;
		
	public class Connector extends EventDispatcher
	{
		
		
		
		
		[Event(name="loggingEvent", type="org.systemsbiology.cereopsis.events.LoggingEvent")]
		
		
		private var stomp:Stomp;
		private var sessionId:String;
		
		private var uid:String;
		
		public function Connector()
		{
			uid = UIDUtil.createUID();
			
		}
		
		public function connect(desiredName:String):void {
			
		//	try {

				stomp = new Stomp();
				stomp.autoReconnect = false;
				
				stomp.addEventListener(IOErrorEvent.IO_ERROR, errorHandler);
				stomp.addEventListener(IOErrorEvent.NETWORK_ERROR, errorHandler);
				stomp.addEventListener(ErrorEvent.ERROR, errorHandler);
				stomp.addEventListener(SecurityErrorEvent.SECURITY_ERROR, errorHandler);



				stomp.addEventListener(ConnectedEvent.CONNECTED, function(event:ConnectedEvent):void {
					sessionId = stomp.sessionID;
					log("Connected: this goose has the unique ID: " + sessionId);  
				});
					
			    var ch: ConnectHeaders = new ConnectHeaders();
				ch.login = uid;
				ch.passcode = "guest";
				
				addEventListener(IOErrorEvent.NETWORK_ERROR, errorHandler);
				addEventListener(IOErrorEvent.IO_ERROR, errorHandler);
				addEventListener(ErrorEvent.ERROR, errorHandler);
				addEventListener(SecurityErrorEvent.SECURITY_ERROR, errorHandler);


				var sock:Socket = new Socket();
				try {
					sock.connect("127.0.0.1", 61613);
				} catch (e) {
					Alert.show("your sock is dirty", "title");
				}

				if (true) return;


				try {
					Security.loadPolicyFile("xmlsocket://127.0.0.1:9876");
				} catch (e) {
					Alert.show("loadPolicyFile failed","title");
				}



				
				trace("so far so good");
				try{
					stomp.connect("127.0.0.1", 61613, ch);//you have to say 127.0.0.1 instead of localhost--and/or match what you say in Security.loadPolicyFile()
				} catch (e) {
					trace("an error in connect");
					Alert.show("a real error","title");
				} finally {
					trace("finally....");
					//Alert.show("bla","alb");
					//return;
				}
				
				
				
				
				stomp.subscribe("/topic/Broadcast");
				stomp.subscribe("/topic/Admin");
				stomp.subscribe("/topic/Boss");
				stomp.subscribe("/topic/Legacy");
				
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
				});
/*
				
			} catch (e:Error) {
				trace("OMFG!!!!");
				log("OMG! An error!!!!!!!!!");
			} finally {
				trace("was there an error? i am done in any case...");
			}
			
*/			

		}
		
		private function errorHandler(event:ErrorEvent):void {
			Alert.show("in error handler", "title");
		}
		
    	private function log(msg:String):void {
			trace(msg);
			var le:LoggingEvent = new LoggingEvent(LoggingEvent.LOGGING_EVENT);
			le.logEntry = msg;
			dispatchEvent(le);
		}

		public function sendData(topicName:String, headers:SendHeaders, payload:String):void {
			try {
				stomp.sendTextMessage("/topic/" + topicName, payload, headers);
			} catch (e:Error) {
				Alert.show("sendData error", "title");
			}
		}
		
		public function sendNamelist(namelist:Object):void {
			var sendHeaders:SendHeaders = new SendHeaders();
			var nameJson:String = JSON.encode(namelist);
			sendHeaders.addHeader("MessageType", "Namelist");
			try {
				stomp.sendTextMessage("/topic/Broadcast", nameJson, sendHeaders);
			} catch (e) {
				Alert.show("Goose and/or boss have gone away. Restart them and (possibly) refresh this page. Working on more graceful approach.", "title");
			}
		}

	}
}