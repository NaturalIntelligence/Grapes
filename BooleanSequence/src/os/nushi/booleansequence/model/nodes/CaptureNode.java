package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.model.Counter;

public class CaptureNode extends Node {

	private Node node;

	public CaptureNode(Node node) {
		super.value = node.value;
		this.node = node;
	}

	@Override
	public boolean match(char[] ch, Counter index) {
		if(node.match(ch, index)){
			super.capture(ch[index.counter]);
			return true;
		}
		return false;
	}
	
}
