package os.nushi.jfree;

import static os.nushi.jfree.BooleanIdentifier.FAILED;
import static os.nushi.jfree.BooleanIdentifier.MATCHED;
import static os.nushi.jfree.BooleanIdentifier.PASSED;

import os.nushi.jfree.matcher.Matcher;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.jfree.matcher.CoreMatcher;
import os.nushi.jfree.matcher.ProgressiveMatcher;

public class PatternTest {
	
	private void assertTrue(ExpressionIdentifier ei){
		Assert.assertEquals(os.nushi.jfree.BooleanIdentifier.PASSED ,ei);
	}
	
	private void assertFalse(ExpressionIdentifier ei){
		Assert.assertEquals(os.nushi.jfree.BooleanIdentifier.FAILED,ei);
	}
	
	@Test
	public void testMinimizationAndOptimization() {
		Pattern seq = new Pattern("ab(cd|ef)gh");
		seq.compile();
		Assert.assertEquals(9, Util.count(seq));
		seq.minimize();
		Assert.assertEquals(Util.count(seq),8);
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,6);
		Assert.assertEquals(depth.max,6);
		
		seq = new Pattern("ab(cde|c)?mn");
		seq.compile();
		Assert.assertEquals(10, Util.count(seq));
		seq.minimize();
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,7);
		Assert.assertEquals(Util.count(seq),7);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abmn".toCharArray()));
		assertTrue(matcher.match("abcdemn".toCharArray()));
		assertTrue(matcher.match("abcmn".toCharArray()));
		
	}
	
	@Test
	public void testMerge() {
		Pattern seq = new Pattern("ab(cd|ef)?");
		seq.compile();
		System.out.println(Util.toJson(seq));
		seq.minimize();
		System.out.println(Util.toJson(seq));
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		
		Pattern seq2 = new Pattern("ab(cd|gh)i");
		seq2.compile().minimize();
		os.nushi.jfree.model.SequenceLength depth2 = Util.calculateDepth(seq2);
		Assert.assertEquals(depth2.min,5);
		Assert.assertEquals(depth2.max,5);
		Util.mergeSequences(seq,seq2);
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abcd".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("abcdi".toCharArray()));
		assertTrue(matcher.match("abghi".toCharArray()));
		assertFalse(matcher.match("abi".toCharArray()));
	}
	
	
	
	@Test
	public void testOptional() {
		Pattern seq = new Pattern("ab?c");
		seq.compile();seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abc".toCharArray()));
		assertTrue(matcher.match("ac".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		assertFalse(matcher.match("dbc".toCharArray()));
		assertFalse(matcher.match("dabc".toCharArray()));
		assertFalse(matcher.match("acb".toCharArray()));
		assertFalse(matcher.match("ab".toCharArray()));
		assertFalse(matcher.match("".toCharArray()));
	}

	@Test
	public void testOr() {
		Pattern seq = new Pattern("ab|cd");
		seq.compile();seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,2);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("cd".toCharArray()));
		assertFalse(matcher.match("ac".toCharArray()));
		assertFalse(matcher.match("abfaltu".toCharArray()));
		
	}
	
	@Test
	public void testMultipleOr() {
		Pattern seq = new Pattern("b|bc|bcd");
		seq.compile();seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,1);
		Assert.assertEquals(depth.max,3);
		System.out.println(Util.toJson(seq));
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("b".toCharArray()));
		assertTrue(matcher.match("bc".toCharArray()));
		assertTrue(matcher.match("bcd".toCharArray()));
	}
	
	@Test
	public void testIteration() {
		Pattern seq = new Pattern("ab{53}cd");
		seq.compile();
		seq.minimize();
		/*SequenceLength depth = RESequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,2);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("cd".toCharArray()));
		assertFalse(matcher.match("ac".toCharArray()));
		assertFalse(matcher.match("abfaltu".toCharArray()));*/
	}

	@Test
	public void testBackSlash() {
		Pattern seq = new Pattern("ab\\?c");
		seq.compile();seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,4);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ab?c".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("ac".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		assertFalse(matcher.match("dbc".toCharArray()));
		assertFalse(matcher.match("dabc".toCharArray()));
		assertFalse(matcher.match("acb".toCharArray()));
		assertFalse(matcher.match("ab".toCharArray()));
	}
	
	@Test
	public void testRange() {
		Pattern seq = new Pattern("ab[a-z0-9A-Z]\\[c");
		seq.compile();
		System.out.println(Util.toJson(seq));
		seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(depth.min,5);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abm[c".toCharArray()));
		assertTrue(matcher.match("abM[c".toCharArray()));
		assertTrue(matcher.match("ab5[c".toCharArray()));
		assertFalse(matcher.match("ab[c".toCharArray()));
		assertFalse(matcher.match("abm8M[c".toCharArray()));
	}
	
	@Test
	public void testAny() {
		Pattern seq = new Pattern("ab.\\.c");
		seq.compile();
		seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,5);
		Assert.assertEquals(depth.max,5);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ab?.c".toCharArray()));
		assertTrue(matcher.match("abd.c".toCharArray()));
		assertTrue(matcher.match("ab3.c".toCharArray()));
		assertTrue(matcher.match("ab..c".toCharArray()));
		
		assertFalse(matcher.match("ab.c".toCharArray()));
		assertFalse(matcher.match("ab.bc".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
	}
	
	@Test
	public void testBracket() {
		Pattern seq = new Pattern("a[bc]d");
		seq.compile();seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,3);
		Assert.assertEquals(depth.max,3);
		System.out.println(Util.toJson(seq));
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		
		seq = new Pattern("a[bc]?d");
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		assertTrue(matcher.match("ad".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
	}
	
	
	@Test
	public void testToJson() {
		Pattern seq = new Pattern("a([bc])d(mn)?");
		seq.compile();seq.minimize();
		System.out.println(Util.toJson(seq));
	}
	
	
	@Test
	public void testLazyMatch() {
		os.nushi.jfree.matcher.LazyMatcher matcher = new os.nushi.jfree.matcher.LazyMatcher();
		Pattern seq = new Pattern("a([bc])d(mn|o)\\1a\\2");
		seq.compile().minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
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
		
		seq = new Pattern("a(bc|d?e)?");
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min,1);
		Assert.assertEquals(depth.max,3);
		Assert.assertEquals(PASSED,matcher.match('a'));//PASSED
		Assert.assertEquals(PASSED,matcher.match('b','c'));//PASSED
		Assert.assertEquals(FAILED,matcher.match('e'));//FAILED
	}
	
	@Test
	public void testProgressiveMatch() {
		Pattern seq = new Pattern("a([bc])d(mn|o)\\1a\\2");
		seq.compile();
		seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min, 7);
		Assert.assertEquals(depth.max, 8);
		ProgressiveMatcher matcher = new ProgressiveMatcher(seq);
		
		Assert.assertEquals(FAILED,matcher.match());
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));
		Assert.assertEquals(MATCHED,matcher.match("abdob".toCharArray()));
		Assert.assertEquals(PASSED,matcher.match("abdobao".toCharArray()));
		System.out.println(seq.matchedSequenceList);
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));
		Assert.assertEquals(MATCHED,matcher.match("abdob".toCharArray()));
		Assert.assertEquals(FAILED,matcher.match("abdobamn".toCharArray()));
		System.out.println(seq.matchedSequenceList);
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));
		Assert.assertEquals(MATCHED,matcher.match("abdmnb".toCharArray()));
		Assert.assertEquals(PASSED,matcher.match("abdmnbamn".toCharArray()));
		System.out.println(seq.matchedSequenceList);
		
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));//MATCHED
		Assert.assertEquals(MATCHED,matcher.match("abdmnb".toCharArray()));//MATCHED
		Assert.assertEquals(FAILED,matcher.match("abdmnbam".toCharArray()));//FAILED
		Assert.assertEquals(FAILED,matcher.match("abdmnbamn".toCharArray()));//FAILED Value of lazy node should not be broken
		System.out.println(seq.matchedSequenceList);
		
		seq = new Pattern("a(bc|d?e)?");
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(PASSED,matcher.match('a'));//PASSED
		Assert.assertEquals(PASSED,matcher.match("abc".toCharArray()));//PASSED
		Assert.assertEquals(FAILED,matcher.match("abce".toCharArray()));//FAILED
	}
	
	@Test
	public void testPathLength() {
		Pattern seq = new Pattern("[abc](ab|c?d)?ef");
		//seq.capture = true;
		seq.compile();
		seq.minimize();
		os.nushi.jfree.model.SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(depth.min, 3);
		Assert.assertEquals(depth.max, 5);
	}
	
	@Test
	public void test() {
		Pattern rootSeq = new Pattern("");rootSeq.compile().minimize();
		Pattern angleSeq = new Pattern("-?0(\\.0)?°",TokenType.ANGLE); angleSeq.compile().minimize();
		Pattern cordinateSeq = new Pattern("-?0(\\.0)?° -?0(\\.0)?['′] -?0(\\.0)?[\"″] a",TokenType.G_Cordinate);cordinateSeq.compile().minimize();
		Pattern tempratureSeq = new Pattern("-?0(\\.0)?° ?a",TokenType.TEMPRATURE);tempratureSeq.compile().minimize();
		
		//System.out.println(RESequenceUtil.toJson(angleSeq));
		//System.out.println(RESequenceUtil.toJson(cordinateSeq));
		//System.out.println(RESequenceUtil.toJson(tempratureSeq));
		
		//angleSeq.merge(cordinateSeq).merge(tempratureSeq);
		
		rootSeq.merge(angleSeq).merge(cordinateSeq).merge(tempratureSeq);
		System.out.println(Util.count(rootSeq));
		System.out.println(Util.toJson(rootSeq));
		
		ProgressiveMatcher matcher = rootSeq.getProgressiveMatcher();
		Assert.assertEquals(TokenType.ANGLE,matcher.match("0°".toCharArray()));
		Assert.assertEquals(TokenType.G_Cordinate,matcher.match("0° 0′ 0″ a".toCharArray()));
		//Assert.assertEquals(TokenType.G_Cordinate,matcher.match("0° 0′ 0\" a".toCharArray()));
		//Assert.assertEquals(TokenType.G_Cordinate,matcher.match("0° 0' 0″ a".toCharArray()));
		//Assert.assertEquals(TokenType.TEMPRATURE,matcher.match("0° a".toCharArray()));
		//Assert.assertEquals(FAILED,matcher.match("0° a 0".toCharArray()));
		//System.out.println(Patterns.G_Cordinate.lazyMatch('0','°'));
		//System.out.println(Patterns.G_Cordinate.lazyMatch('0','°','0','′', '0','″','a'));
		
	}


	@Test
	public void tempTest() {
		Pattern seq = new Pattern("\\.d(ab?|c)e?[x-z].\\1");
		seq.compile().minimize();

		System.out.println(Util.toJson(seq));
		System.out.println(Util.count(seq));

		Matcher matcher = seq.getCoreMatcher();


		Assert.assertEquals(PASSED,matcher.match(".daey;a".toCharArray()));

	}
}
