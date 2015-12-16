package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.model.Counter;

public class NormalNode extends Node {

	public NormalNode() {
	}

	public NormalNode(char ch) {
		super(ch);
	}

	@Override
	public boolean match(char[] ch, Counter index) {
		if(super.value == ch[index.counter]){
			super.capture(ch[index.counter]);
			return true;
		}
		return false;
	}
	
}
