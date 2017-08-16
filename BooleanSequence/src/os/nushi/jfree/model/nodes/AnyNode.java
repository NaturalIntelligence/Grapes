package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.MatchingCharSequence;

public class AnyNode extends Node {

	public AnyNode() {
		super('@');
	}

	public AnyNode(boolean shouldBeCaptured, MatchingCharSequence ref) {

		super('@',ref);
		super.shouldBeCaptured = shouldBeCaptured;
}

	@Override
	public Result match(char[] ch, os.nushi.jfree.model.Counter index) {
		capture(ch[index.counter]);
		return Result.PASSED;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof AnyNode;
	}
}
