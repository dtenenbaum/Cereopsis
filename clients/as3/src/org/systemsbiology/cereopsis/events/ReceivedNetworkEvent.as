package org.systemsbiology.cereopsis.events
{
	import flash.events.Event;
	
	public class ReceivedNetworkEvent extends Event
	{
		public var compactNetwork:Object;
		
		public static const RECEIVED_NETWORK_EVENT : String = "receivedNetworkEvent";
		
		public function ReceivedNetworkEvent(type:String)
		{
			super(type);
		}

	}
}