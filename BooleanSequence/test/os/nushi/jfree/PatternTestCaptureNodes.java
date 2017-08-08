package os.nushi.jfree;

import static os.nushi.jfree.BooleanIdentifier.FAILED;
import static os.nushi.jfree.BooleanIdentifier.MATCHED;
import static os.nushi.jfree.BooleanIdentifier.PASSED;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.jfree.matcher.CoreMatcher;
import os.nushi.jfree.model.SequenceLength;

public class PatternTestCaptureNodes {
	
	private void assertTrue(ExpressionIdentifier ei){
		Assert.assertEquals(os.nushi.jfree.BooleanIdentifier.PASSED ,ei);
	}
	
	private void assertFalse(ExpressionIdentifier ei){
		Assert.assertEquals(os.nushi.jfree.BooleanIdentifier.FAILED,ei);
	}
	
	
	@Test
	public void withNoSubSequenceToCapture() {
		Pattern seq = new Pattern("abcd");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abcd".toCharArray()));
		Assert.assertEquals(seq.matchedSequenceList.size(),0);
		assertFalse(matcher.match("acd".toCharArray()));
		Assert.assertEquals(seq.matchedSequenceList.size(),0);
	}
	
	@Test
	public void withSubSequence(){
		Pattern seq = new Pattern("a(bc)d");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abcd".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(seq.matchedSequenceList.get(0).size(),2);
		
		assertFalse(matcher.match("acd".toCharArray()));
	}
	
	@Test
	public void withSubSequenceAndCapturedMatch(){
		Pattern seq = new Pattern("a([bc])d\\1");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abdb".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
		
		assertTrue(matcher.match("acdc".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
		
		assertFalse(matcher.match("acdb".toCharArray()));
		assertFalse(matcher.match("abdc".toCharArray()));
		assertFalse(matcher.match("abcdbc".toCharArray()));
	}


	@Test
	public void withBracket(){
		Pattern seq = new Pattern("a([bc])d");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,3);
		Assert.assertEquals(depth.max,3);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abd".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
		assertTrue(matcher.match("acd".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
	}
	
	@Test
	public void withRange(){
		Pattern seq = new Pattern("a([m-z])d");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,3);
		Assert.assertEquals(depth.max,3);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("amd".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
		assertTrue(matcher.match("axd".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
	}
	
	@Test
	public void withAny(){
		Pattern seq = new Pattern("a([m-z]b.)d");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,5);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ambad".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(3,seq.matchedSequenceList.get(0).size());
		assertFalse(matcher.match("ambd".toCharArray()));
	}
	
	@Test
	public void withOptional(){
		Pattern seq = new Pattern("a([m-z]b.?)d");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ambad".toCharArray()));
		Assert.assertEquals(1,seq.matchedSequenceList.size());
		Assert.assertEquals(3,seq.matchedSequenceList.get(0).size());
		//TODO : Bug
		//assertTrue(matcher.match("ambd".toCharArray()));
		//Assert.assertEquals(1,seq.matchedSequenceList.size());
		//Assert.assertEquals(2,seq.matchedSequenceList.get(0).size());
		
		
	}
	
	@Test
	public void testMultipleCapture() {
		Pattern seq = new Pattern("a([bc])d(mn)?");
		seq.capture = true;
		seq.compile().minimize();
		System.out.println(Util.toJson(seq));
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,3);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abdmn".toCharArray()));
		Assert.assertEquals(2,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
		Assert.assertEquals(2,seq.matchedSequenceList.get(1).size());
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		Assert.assertEquals(2,seq.matchedSequenceList.size());
		Assert.assertEquals(1,seq.matchedSequenceList.get(0).size());
		Assert.assertEquals(0,seq.matchedSequenceList.get(1).size());
		assertTrue(matcher.match("acdmn".toCharArray()));
		
	}
	
	@Test
	public void testCaptureAndLazyNodes() {
		os.nushi.jfree.matcher.LazyMatcher matcher = new os.nushi.jfree.matcher.LazyMatcher();
		Pattern seq = new Pattern("a([bc])d(mn|o)\\1a\\2");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,7);
		Assert.assertEquals(depth.max,8);
		
		matcher.setSequence(seq);
		Assert.assertEquals(FAILED,matcher.match());
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','o','b'));
		Assert.assertEquals(PASSED,matcher.match('a','o'));
		System.out.println(seq.matchedSequenceList);
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','o','b'));
		Assert.assertEquals(FAILED,matcher.match('a','m','n'));
		System.out.println(seq.matchedSequenceList);
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','m','n','b'));
		Assert.assertEquals(PASSED,matcher.match('a','m','n'));
		System.out.println(seq.matchedSequenceList);
		
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match('a','b'));
		Assert.assertEquals(MATCHED,matcher.match('d','m','n','b'));
		Assert.assertEquals(FAILED,matcher.match('a','m'));//Value of lazy node should not be broken
		Assert.assertEquals(FAILED,matcher.match('n'));
		System.out.println(seq.matchedSequenceList);
		
	}
	

	@Test
	public void testToJson() {
		Pattern seq = new Pattern("a([bc])d(mn)?");
		seq.capture = true;
		seq.compile();seq.minimize();
		System.out.println(Util.toJson(seq));
	}
	
}
