package org.systemsbiology.cereopsis.events
{
	import flash.events.Event;
	
	
	
	public class ReceivedClusterEvent extends Event
	{

	public var cluster:Object;
	
	public static const RECEIVED_CLUSTER_EVENT :String = "receivedClusterEvent";


		public function ReceivedClusterEvent(type:String)
		{
			super(type);
		}

	}
}