package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.ds.primitive.CharArrList;

public class AnyNode extends Node {

	public AnyNode() {
		super('@');
	}

	public AnyNode(CharArrList ref) {
		super('@',ref);
}

	@Override
	public Result match(char[] ch, os.nushi.jfree.model.Counter index) {
		return Result.PASSED;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof AnyNode;
	}
}
