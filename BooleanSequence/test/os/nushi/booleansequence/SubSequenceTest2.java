package os.nushi.booleansequence;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.booleansequence.matcher.CoreMatcher;
import os.nushi.booleansequence.model.SequenceLength;

public class SubSequenceTest2 {

	private void assertTrue(ExpressionIdentifier ei){
		Assert.assertEquals(BooleanIdentifier.PASSED ,ei);
	}
	
	private void assertFalse(ExpressionIdentifier ei){
		Assert.assertEquals(BooleanIdentifier.FAILED,ei);
	}
	
	/*@Test
	public void testGroupingWithOr() {
		BooleanSequence seq = new BooleanSequence("ab(cd|ef)");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abcd".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertFalse(matcher.match("abcdef".toCharArray()));
		assertFalse(matcher.match("".toCharArray()));
		
		seq = new BooleanSequence("(ab|cd)ef");
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		assertTrue(matcher.match("cdef".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		
		seq = new BooleanSequence("ab(cd|ef)gh");
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,6);
		Assert.assertEquals(depth.max,6);
		assertTrue(matcher.match("abcdgh".toCharArray()));
		assertTrue(matcher.match("abefgh".toCharArray()));
		assertFalse(matcher.match("abcdef".toCharArray()));
		assertFalse(matcher.match("cdgh".toCharArray()));
	}
	
	@Test
	public void testOptional() {		
		BooleanSequence seq = new BooleanSequence("(ab|cd)?ef");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = seq.getCoreMatcher();
		matcher.setSequence(seq);
		assertTrue(matcher.match("cdef".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertTrue(matcher.match("ef".toCharArray()));
		
		seq = new BooleanSequence("(ab|c?d)?ef");
		seq.compile();seq.minimize();
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		matcher.setSequence(seq);
		assertTrue(matcher.match("cdef".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertTrue(matcher.match("ef".toCharArray()));
		assertTrue(matcher.match("def".toCharArray()));
		assertTrue(matcher.match("cdef".toCharArray()));
		
		seq = new BooleanSequence("(ab|c?d)?");
		seq.compile();
		seq.minimize();
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,0);
		Assert.assertEquals(depth.max,2);
		matcher.setSequence(seq);
		assertTrue(matcher.match("cd".toCharArray()));
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("d".toCharArray()));
		assertTrue(matcher.match("".toCharArray()));
		
		seq = new BooleanSequence("ab(ab|c?d)?");
		seq.compile();seq.minimize();
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		matcher.setSequence(seq);
		assertTrue(matcher.match("ab".toCharArray()));
		assertFalse(matcher.match("".toCharArray()));
	}
	
	@Test
	public void testBracket() {
		BooleanSequence seq = new BooleanSequence("a([bc]|d)?e");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		CoreMatcher matcher = seq.getCoreMatcher();
		System.out.println(BooleanSequenceUtil.toJson(seq));
		System.out.println(BooleanSequenceUtil.count(seq));
		assertTrue(matcher.match("abe".toCharArray()));
		assertTrue(matcher.match("ace".toCharArray()));
		assertTrue(matcher.match("ade".toCharArray()));
		assertTrue(matcher.match("ae".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
	}
	
	@Test
	public void testNestedGrouping() {
		BooleanSequence seq = new BooleanSequence("a(b(cd)?)?e");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abe".toCharArray()));
		assertTrue(matcher.match("abcde".toCharArray()));
		assertTrue(matcher.match("ae".toCharArray()));
		assertFalse(matcher.match("acde".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		
		//TODO : Bug
		seq = new BooleanSequence("a(b|(bc|cd))");
		seq.compile();
		System.out.println("afte compile");
		System.out.println(BooleanSequenceUtil.toJson(seq));
		seq.minimize();
		System.out.println("afte minimize");
		System.out.println(BooleanSequenceUtil.toJson(seq));
		System.out.println(BooleanSequenceUtil.count(seq));
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("abc".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
	}*/
}
