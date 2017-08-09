package os.nushi.jfree;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.jfree.model.nodes.Node;

public class PatternUtilTest {

	@Test
	public void testGetEndNodes() {
		Sequence seq = new Pattern("ab|cd").compile();
		Set<Node> endNodes = Util.getEndNodes(seq);
		Assert.assertEquals(2,endNodes.size());
		
		seq = new Pattern("(ab|c?d)?ef").compile();
		endNodes = Util.getEndNodes(seq);
		Assert.assertEquals(1,endNodes.size());
		
		seq = new Pattern("(ab|c?d)?").compile();
		endNodes = Util.getEndNodes(seq);
		Assert.assertEquals(3,endNodes.size());
	}
}
