package os.nushi.jfree.model.nodes;

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;

public class NormalNode extends Node {

	public NormalNode() {
	}

	public NormalNode(char ch, CharArrList ref) {
		super(ch,ref);
	}

	@Override
	public boolean match(char[] ch, Counter index) {
		return super.value == ch[index.counter];
	}
	
}
