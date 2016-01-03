package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.model.Counter;

public class AnyNode extends Node {

	public AnyNode() {
		super('@');
	}
	@Override
	public boolean match(char[] ch, Counter index) {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof AnyNode;
	}
}
