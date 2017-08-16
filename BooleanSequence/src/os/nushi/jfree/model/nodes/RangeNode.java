package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.MatchingCharSequence;
import os.nushi.jfree.util.CharUtil;

public class RangeNode extends Node {

	private char start;
	private char end;

	public RangeNode(char from, char to) {
		super('@');
	}

	public RangeNode(char left, char right, boolean shouldBeCaptured, MatchingCharSequence ref) {
		super('@',ref);
		this.start = left;
		this.end = right;
		super.shouldBeCaptured = shouldBeCaptured;
	}

	@Override
	public Result match(char[] ch, Counter index) {
		if(CharUtil.isRange(ch[index.counter], start, end)){
			capture(ch[index.counter]);
			return Result.PASSED;
		}else{
			return Result.FAILED;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof RangeNode && this.start == ((RangeNode) obj).end && this.end == ((RangeNode) obj).end;
	}
	
	@Override
	public String toString() {
		return new String(start + "-" + end);
	}
}
