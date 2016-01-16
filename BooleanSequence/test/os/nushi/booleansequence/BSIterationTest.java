package os.nushi.booleansequence;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.booleansequence.matcher.CoreMatcher;
import os.nushi.booleansequence.model.SequenceLength;

public class BSIterationTest {
	
	private void assertTrue(ExpressionIdentifier ei){
		Assert.assertEquals(BooleanIdentifier.PASSED ,ei);
	}
	
	private void assertFalse(ExpressionIdentifier ei){
		Assert.assertEquals(BooleanIdentifier.FAILED,ei);
	}
	
	@Test
	public void testToJson() {
		
		
		/*BooleanSequence seq = new BooleanSequence("a([bc])+");
		//BooleanSequence seq = new BooleanSequence("a([bc])d(mn|o)\\1a\\2");
		seq.capture = true;
		seq.compile().minimize();
		System.out.println(BooleanSequenceUtil.toJson(seq));
		System.out.println(BooleanSequenceUtil.count(seq));
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,2);
		CoreMatcher matcher = seq.getCoreMatcher();
		//assertTrue(matcher.match("ab".toCharArray()));
		//assertTrue(matcher.match("abb".toCharArray()));
		assertTrue(matcher.match("abbb".toCharArray()));
		assertTrue(matcher.match("abcb".toCharArray()));
		assertFalse(matcher.match("a".toCharArray()));*/
		
		
		/*seq = new BooleanSequence("[mn](ax[bc])+e");
		seq.capture = true;
		seq.compile().minimize();
		System.out.println(BooleanSequenceUtil.toJson(seq));
		System.out.println(BooleanSequenceUtil.count(seq));
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,5);
		Assert.assertEquals(depth.max,5);
		matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("maxbe".toCharArray()));
		assertTrue(matcher.match("maxbaxce".toCharArray()));
		assertTrue(matcher.match("naxbaxbaxbe".toCharArray()));
		assertFalse(matcher.match("mnnaxbaxbe".toCharArray()));
		assertFalse(matcher.match("maxbce".toCharArray()));
		assertFalse(matcher.match("mabe".toCharArray()));
		assertFalse(matcher.match("me".toCharArray()));*/
	}

}
