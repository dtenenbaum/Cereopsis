package org.systemsbiology.cereopsis.events
{
	import flash.events.Event;
	

	public class ReceivedNamelistEvent extends Event
	{
		public static const RECEIVED_NAMELIST_EVENT : String = "receivedNamelist";
		
		public var namelist:Object;
		
		public function ReceivedNamelistEvent(type:String)
		{
			super(type);
		}
		
	}
}