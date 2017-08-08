package os.nushi.jfree.model.nodes;

public class AnyNode extends Node {

	public AnyNode() {
		super('@');
	}
	@Override
	public boolean match(char[] ch, os.nushi.jfree.model.Counter index) {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof AnyNode;
	}
}
