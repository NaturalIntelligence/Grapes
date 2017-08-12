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
		Assert.assertEquals(seq.matchingGroups.size(),0);
		assertFalse(matcher.match("acd".toCharArray()));
		Assert.assertEquals(seq.matchingGroups.size(),0);
	}
	
	@Test
	public void withSubSequence(){
		Sequence seq = new Pattern("a(bc)d").compile();
		
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,4);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abcd".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(seq.matchingGroups.get(0).size(),2);
		
		assertFalse(matcher.match("acd".toCharArray()));
	}
	
	@Test
	public void withSubSequenceAndCapturedMatch(){
		Sequence seq = new Pattern("a([bc])d\\1").compile();
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,4);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abdb".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
		
		assertTrue(matcher.match("acdc".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
		
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
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
		assertTrue(matcher.match("acd".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
	}
	
	@Test
	public void withRange(){
		Sequence seq = new Pattern("a([m-z])d").compile();
		Assert.assertEquals(seq.minPathLength,3);
		Assert.assertEquals(seq.maxPathLength,3);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("amd".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
		assertTrue(matcher.match("axd".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
	}
	
	@Test
	public void withAny(){
		Sequence seq = new Pattern("a([m-z]b.)d").compile();
		Assert.assertEquals(seq.minPathLength,5);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("ambad".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(3,seq.matchingGroups.get(0).size());
		assertFalse(matcher.match("ambd".toCharArray()));
	}
	
	@Test
	public void withOptional(){
		Sequence seq = new Pattern("a([m-z]b.?)d").compile();
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("ambad".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(3,seq.matchingGroups.get(0).size());
		assertTrue(matcher.match("ambd".toCharArray()));
		Assert.assertEquals(1,seq.matchingGroups.size());
		Assert.assertEquals(2,seq.matchingGroups.get(0).size());
		
		
	}
	
	@Test
	public void testMultipleCapture() {
		Sequence seq = new Pattern("a([bc])d(mn)?").compile();
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(seq.minPathLength,3);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abdmn".toCharArray()));
		Assert.assertEquals(2,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
		Assert.assertEquals(2,seq.matchingGroups.get(1).size());
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		Assert.assertEquals(2,seq.matchingGroups.size());
		Assert.assertEquals(1,seq.matchingGroups.get(0).size());
		Assert.assertEquals(0,seq.matchingGroups.get(1).size());
		assertTrue(matcher.match("acdmn".toCharArray()));
		
	}
	
	@Test
	public void testCaptureAndLazyNodes() {
		LazyMatcher matcher = new LazyMatcher();
		Sequence seq = new Pattern("a([bc])d(mn|o)\\1a\\2").compile();
		Assert.assertEquals(seq.minPathLength,7);
		Assert.assertEquals(seq.maxPathLength,9);
		
		matcher.setSequence(seq);
		Assert.assertEquals(FAILED,matcher.match());
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','o','b'));
		Assert.assertEquals(PASSED,matcher.match('a','o'));
		System.out.println(seq.matchingGroups);
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','o','b'));
		Assert.assertEquals(FAILED,matcher.match('a','m','n'));
		System.out.println(seq.matchingGroups);
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','m','n','b'));
		Assert.assertEquals(PASSED,matcher.match('a','m','n'));
		System.out.println(seq.matchingGroups);
		
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','m','n','b'));
		Assert.assertEquals(MATCHED,matcher.match('a','m'));
		Assert.assertEquals(PASSED,matcher.match('n'));
		System.out.println(seq.matchingGroups);
		
	}
	

	@Test
	public void testToJson() {
		Sequence seq = new Pattern("a([bc])d(mn)?").compile();
		System.out.println(Util.toJson(seq));
	}
	
}
