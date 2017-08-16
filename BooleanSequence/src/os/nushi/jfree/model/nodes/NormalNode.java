package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.MatchingCharSequence;

public class NormalNode extends Node {

	public NormalNode() {
	}

	public NormalNode(char ch, boolean shouldBeCaptured,MatchingCharSequence ref) {
		super(ch,ref);
		super.shouldBeCaptured = shouldBeCaptured;
	}

	@Override
	public Result match(char[] ch, Counter index) {
		if(super.value == ch[index.counter]) {
			capture(ch[index.counter]);
			return Result.PASSED;
		}
		else
			return Result.FAILED;
	}
	
}
