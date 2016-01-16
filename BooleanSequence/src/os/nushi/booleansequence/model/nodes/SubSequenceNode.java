package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.BooleanSequence;
import os.nushi.booleansequence.matcher.Matcher;
import os.nushi.booleansequence.model.Counter;

public class SubSequenceNode extends Node {

	public BooleanSequence sequence;
	private Matcher matcher;

	public SubSequenceNode(BooleanSequence sequence) {
		super('@');
		this.sequence = sequence;
	}
	
	public void setMatcher(Matcher matcher){
		this.matcher = matcher;
		this.matcher.setSequence(sequence);
	}
	
	@Override
	public boolean match(char[] ch, Counter index) {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof SubSequenceNode;
	}
}
