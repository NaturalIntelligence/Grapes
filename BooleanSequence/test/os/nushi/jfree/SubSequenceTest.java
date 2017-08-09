package os.nushi.jfree;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.jfree.matcher.CoreMatcher;
import os.nushi.jfree.model.SequenceLength;

public class SubSequenceTest {

	private void assertTrue(ResultIdentifier ei){
		Assert.assertEquals(Result.PASSED ,ei);
	}
	
	private void assertFalse(ResultIdentifier ei){
		Assert.assertEquals(Result.FAILED,ei);
	}
	
	@Test
	public void testGroupingWithOr() {
		Sequence seq = new Pattern("ab(cd|ef)").compile();

		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abcd".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertFalse(matcher.match("abcdef".toCharArray()));
		assertFalse(matcher.match("".toCharArray()));

		seq = new Pattern("(ab|cd)ef").compile();
		matcher.setSequence(seq);
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		assertTrue(matcher.match("cdef".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));

		seq = new Pattern("ab(cd|ef)gh").compile();
		matcher.setSequence(seq);
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,6);
		Assert.assertEquals(depth.max,6);
		assertTrue(matcher.match("abcdgh".toCharArray()));
		assertTrue(matcher.match("abefgh".toCharArray()));
		assertFalse(matcher.match("abcdef".toCharArray()));
		assertFalse(matcher.match("cdgh".toCharArray()));
	}
	
	@Test
	public void testOptional() {		
		Sequence seq = new Pattern("(ab|cd)?ef").compile();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = new CoreMatcher(seq);
		matcher.setSequence(seq);
		assertTrue(matcher.match("cdef".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertTrue(matcher.match("ef".toCharArray()));
		
		seq = new Pattern("(ab|c?d)?ef").compile();
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		matcher.setSequence(seq);
		assertTrue(matcher.match("cdef".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertTrue(matcher.match("ef".toCharArray()));
		assertTrue(matcher.match("def".toCharArray()));
		assertTrue(matcher.match("cdef".toCharArray()));
		
		seq = new Pattern("(ab|c?d)?").compile();
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,0);
		Assert.assertEquals(depth.max,2);
		matcher.setSequence(seq);
		assertTrue(matcher.match("cd".toCharArray()));
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("d".toCharArray()));
		assertTrue(matcher.match("".toCharArray()));
		
		seq = new Pattern("ab(ab|c?d)?").compile();
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		matcher.setSequence(seq);
		assertTrue(matcher.match("ab".toCharArray()));
		assertFalse(matcher.match("".toCharArray()));
	}
	
	@Test
	public void testBracket() {
		Sequence seq = new Pattern("a([bc]|d)?e").compile();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		CoreMatcher matcher = new CoreMatcher(seq);
		System.out.println(Util.toJson(seq));
		System.out.println(Util.count(seq));
		assertTrue(matcher.match("abe".toCharArray()));
		assertTrue(matcher.match("ace".toCharArray()));
		assertTrue(matcher.match("ade".toCharArray()));
		assertTrue(matcher.match("ae".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
	}
	
	@Test
	public void testNestedGrouping() {
		Sequence seq = new Pattern("a(b(cd)?)?e").compile();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abe".toCharArray()));
		assertTrue(matcher.match("abcde".toCharArray()));
		assertTrue(matcher.match("ae".toCharArray()));
		assertFalse(matcher.match("acde".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		
		seq = new Pattern("a(b|(bc|cd))").compile();
		System.out.println(Util.toJson(seq));
		System.out.println(Util.count(seq));
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("abc".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
	}
}
