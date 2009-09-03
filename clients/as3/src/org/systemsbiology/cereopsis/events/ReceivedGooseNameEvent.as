package org.systemsbiology.cereopsis.events
{
	import flash.events.Event;
	
	public class ReceivedGooseNameEvent extends Event
	{
		public var gooseName:String;
		
		public static const RECEIVED_GOOSE_NAME_EVENT : String = "receivedGooseNameEvent";
		
		public function ReceivedGooseNameEvent(type:String)
		{
			super(type);
		}

	}
}