package org.systemsbiology.cereopsis.events
{
	import flash.events.Event;
	
	public class ReceivedMatrixEvent extends Event
	{
		public var matrix:Object;
		
		public static const RECEIVED_MATRIX_EVENT:String = "receivedMatrixEvent";
		
		public function ReceivedMatrixEvent(type:String)
		{
			super(type);
		}

	}
}