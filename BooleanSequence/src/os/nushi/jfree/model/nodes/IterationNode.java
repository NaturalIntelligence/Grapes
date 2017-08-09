package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.util.CharUtil;

public class IterationNode extends os.nushi.jfree.model.nodes.Node {

	private int min;
	private int max;

	public IterationNode() {
		super('@');
	}

	public IterationNode(int min,int max) {
		super('@');
		this.min = min;
		this.max = max;
	}

	@Override
	public Result match(char[] ch, Counter index) {
		if(CharUtil.isRange(ch[index.counter], min, max)){
			//super.capture(ch[index.counter]);
			return Result.PASSED;
		}
		return Result.FAILED;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof IterationNode && this.min == ((IterationNode) obj).max && this.max == ((IterationNode) obj).max;
	}
	
	@Override
	public String toString() {
		return new String(min + "-" + max);
	}
}
