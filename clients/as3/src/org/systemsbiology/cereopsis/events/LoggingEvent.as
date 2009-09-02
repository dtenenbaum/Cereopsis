package org.systemsbiology.cereopsis.events
{
	import flash.events.Event;
	
	public class LoggingEvent extends Event
	{
		public static const LOGGING_EVENT : String = "loggingEvent";
		
		public var logEntry:String;
		
		public function LoggingEvent(type:String)
		{
			super(type);
		}

	}
}