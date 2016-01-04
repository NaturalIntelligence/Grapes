package os.nushi.booleansequence;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.booleansequence.model.nodes.Node;

public class BooleanSequenceUtilTest {

	@Test
	public void testGetEndNodes() {
		BooleanSequence seq = new BooleanSequence("ab|cd");
		seq.compile().minimize();
		Set<Node> endNodes = BooleanSequenceUtil.getEndNodes(seq);
		Assert.assertEquals(2,endNodes.size());
		
		seq = new BooleanSequence("(ab|c?d)?ef");
		seq.compile().minimize();
		endNodes = BooleanSequenceUtil.getEndNodes(seq);
		Assert.assertEquals(1,endNodes.size());
		
		seq = new BooleanSequence("(ab|c?d)?");
		seq.compile().minimize();
		endNodes = BooleanSequenceUtil.getEndNodes(seq);
		Assert.assertEquals(3,endNodes.size());
	}
}
