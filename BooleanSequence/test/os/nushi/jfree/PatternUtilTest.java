package os.nushi.jfree;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.jfree.model.nodes.Node;

public class PatternUtilTest {

	@Test
	public void testGetEndNodes() {
		Pattern seq = new Pattern("ab|cd");
		seq.compile().minimize();
		Set<Node> endNodes = Util.getEndNodes(seq);
		Assert.assertEquals(2,endNodes.size());
		
		seq = new Pattern("(ab|c?d)?ef");
		seq.compile().minimize();
		endNodes = Util.getEndNodes(seq);
		Assert.assertEquals(1,endNodes.size());
		
		seq = new Pattern("(ab|c?d)?");
		seq.compile().minimize();
		endNodes = Util.getEndNodes(seq);
		Assert.assertEquals(3,endNodes.size());
	}
}
