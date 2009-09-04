package {
	import flash.display.MovieClip;
	import bridge.FABridge;
	
	import org.systemsbiology.cereopsis.Connector;
	
	public class EmptySwf extends MovieClip {
		
		public var goose:Connector = new Connector();
		
		private var externalBridge:FABridge;
		
		public function EmptySwf() {
			super();
			externalBridge = new FABridge();
			externalBridge.rootObject = this;
		}
	}
}    