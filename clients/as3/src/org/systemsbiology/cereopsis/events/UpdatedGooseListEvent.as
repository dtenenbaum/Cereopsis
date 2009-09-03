package org.systemsbiology.cereopsis.events
{
	import flash.events.Event;
	
	public class UpdatedGooseListEvent extends Event
	{
		public static const UPDATED_GOOSE_LIST_EVENT:String = "updatedGooseListEvent";
		
		public var gooseNames:Object;
		public var gooseName:String;
		
		public function UpdatedGooseListEvent(type:String)
		{
			super(type);
		}

	}
}