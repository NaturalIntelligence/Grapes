package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.model.Counter;
import os.nushi.booleansequence.util.CharUtil;

public class RangeNode extends Node {

	private char start;
	private char end;

	public RangeNode() {
		super('@');
	}

	public RangeNode(char left,char right) {
		super('@');
		this.start = left;
		this.end = right;
	}

	@Override
	public boolean match(char[] ch, Counter index) {
		if(CharUtil.isRange(ch[index.counter], start, end)){
			super.capture(ch[index.counter]);
			return true;
		}
		return false;
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
