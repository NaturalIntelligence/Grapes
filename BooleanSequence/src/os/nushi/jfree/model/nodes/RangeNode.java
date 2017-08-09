package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.util.CharUtil;

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
	public Result match(char[] ch, os.nushi.jfree.model.Counter index) {
		if(CharUtil.isRange(ch[index.counter], start, end)){
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
