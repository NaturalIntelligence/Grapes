package os.nushi.jfree.model.nodes;

import os.nushi.jfree.ds.primitive.CharArrList;

public class RangeNode extends Node {

	private char start;
	private char end;

	public RangeNode() {
		super('@');
	}

	public RangeNode(char left,char right,CharArrList ref) {
		super('@',ref);
		this.start = left;
		this.end = right;
	}

	@Override
	public boolean match(char[] ch, os.nushi.jfree.model.Counter index) {
		return os.nushi.jfree.util.CharUtil.isRange(ch[index.counter], start, end);
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
