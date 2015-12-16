package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.model.Counter;
import os.nushi.booleansequence.util.CharUtil;

public class IterationNode extends Node {

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
	public boolean match(char[] ch, Counter index) {
		if(CharUtil.isRange(ch[index.counter], min, max)){
			super.capture(ch[index.counter]);
			return true;
		}
		return false;
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
