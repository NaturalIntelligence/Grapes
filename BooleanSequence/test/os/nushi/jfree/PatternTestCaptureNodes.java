package os.nushi.jfree;

import org.junit.Assert;
import org.junit.Test;
import os.nushi.jfree.matcher.CoreMatcher;
import os.nushi.jfree.matcher.LazyMatcher;
import os.nushi.jfree.model.SequenceLength;

import static os.nushi.jfree.Result.*;

public class PatternTestCaptureNodes {
	
	private void assertTrue(ResultIdentifier ei){
		Assert.assertEquals(Result.PASSED ,ei);
	}
	
	private void assertFalse(ResultIdentifier ei){
		Assert.assertEquals(FAILED,ei);
	}
	
	@Test
	public void withNoSubSequenceToCapture() {
		Sequence seq = new Pattern("abcd").compile();
		
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,4);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abcd".toCharArray()));
		Assert.assertEquals(matcher.getGroups().size(),0);
		assertFalse(matcher.match("acd".toCharArray()));
		Assert.assertEquals(matcher.getGroups().size(),0);
	}
	
	@Test
	public void withSubSequence(){
		Sequence seq = new Pattern("a(bc)d").compile();
		
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,4);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abcd".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(matcher.getGroups().get(1).value().length(),2);
		
		assertFalse(matcher.match("acd".toCharArray()));
	}
	
	@Test
	public void withSubSequenceAndCapturedMatch(){
		Sequence seq = new Pattern("a([bc])d\\1").compile();
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,4);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abdb".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
		
		assertTrue(matcher.match("acdc".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
		
		assertFalse(matcher.match("acdb".toCharArray()));
		assertFalse(matcher.match("abdc".toCharArray()));
		assertFalse(matcher.match("abcdbc".toCharArray()));
	}


	@Test
	public void withBracket(){
		Sequence seq = new Pattern("a([bc])d").compile();
		Assert.assertEquals(seq.minPathLength,3);
		Assert.assertEquals(seq.maxPathLength,3);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abd".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
		assertTrue(matcher.match("acd".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
	}
	
	@Test
	public void withRange(){
		Sequence seq = new Pattern("a([m-z])d").compile();
		Assert.assertEquals(seq.minPathLength,3);
		Assert.assertEquals(seq.maxPathLength,3);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("amd".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
		assertTrue(matcher.match("axd".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
	}
	
	@Test
	public void withAny(){
		Sequence seq = new Pattern("a([m-z]b.)d").compile();
		Assert.assertEquals(seq.minPathLength,5);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("ambad".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(3,matcher.getGroups().get(1).value().length());
		assertFalse(matcher.match("ambd".toCharArray()));
	}
	
	@Test
	public void withOptional(){
		Sequence seq = new Pattern("a([m-z]b.?)d").compile();
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("ambad".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(3,matcher.getGroups().get(1).value().length());
		assertTrue(matcher.match("ambd".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(2,matcher.getGroups().get(1).value().length());
		
		
	}
	
	@Test
	public void testMultipleCapture() {
		Sequence seq = new Pattern("a([bc])d(mn)?").compile();
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(seq.minPathLength,3);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abdmn".toCharArray()));
		Assert.assertEquals(2,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
		Assert.assertEquals(2,matcher.getGroups().get(2).value().length());
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		Assert.assertEquals(1,matcher.getGroups().size());
		Assert.assertEquals(1,matcher.getGroups().get(1).value().length());
		assertTrue(matcher.match("acdmn".toCharArray()));
		
	}
	
	@Test
	public void testCaptureAndLazyNodes() {
		Sequence seq = new Pattern("a([bc])d(mn|o)\\1a\\2").compile();
		Assert.assertEquals(seq.minPathLength,7);
		Assert.assertEquals(seq.maxPathLength,9);

		LazyMatcher matcher = new LazyMatcher(seq);
		Assert.assertEquals(FAILED,matcher.match());
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','o','b'));
		Assert.assertEquals(PASSED,matcher.match('a','o'));
		System.out.println(matcher.getGroups());
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','o','b'));
		Assert.assertEquals(FAILED,matcher.match('a','m','n'));
		System.out.println(matcher.getGroups());
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','m','n','b'));
		Assert.assertEquals(PASSED,matcher.match('a','m','n'));
		System.out.println(matcher.getGroups());
		
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','m','n','b'));
		Assert.assertEquals(MATCHED,matcher.match('a','m'));
		Assert.assertEquals(PASSED,matcher.match('n'));
		System.out.println(matcher.getGroups());
		
	}
	

	@Test
	public void testToJson() {
		Sequence seq = new Pattern("a([bc])d(mn)?").compile();
		System.out.println(Util.toJson(seq));
	}
	
}
