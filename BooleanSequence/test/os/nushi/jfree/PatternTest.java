package os.nushi.jfree;

import static os.nushi.jfree.Result.FAILED;
import static os.nushi.jfree.Result.MATCHED;
import static os.nushi.jfree.Result.PASSED;

import os.nushi.jfree.data.TokenType;
import os.nushi.jfree.model.SequenceLength;

import os.nushi.jfree.matcher.Matcher;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.jfree.matcher.CoreMatcher;
import os.nushi.jfree.matcher.ProgressiveMatcher;
import os.nushi.jfree.util.Pair;
import os.nushi.jfree.util.PathLengthCalculator;

public class PatternTest {
	
	private void assertTrue(ResultIdentifier ei){
		Assert.assertEquals(Result.PASSED ,ei);
	}
	
	private void assertFalse(ResultIdentifier ei){
		Assert.assertEquals(Result.FAILED,ei);
	}
	
	@Test
	public void testMinimizationAndOptimization() {
		Sequence seq = new Pattern("ab(cd|ef)gh").compile();
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(8, Util.count(seq));
		Assert.assertEquals(Util.count(seq),8);

		seq = new Pattern("ab(cde|c)?mn").compile();
		Assert.assertEquals(7, Util.count(seq));
		Assert.assertEquals(Util.count(seq),7);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abmn".toCharArray()));
		assertTrue(matcher.match("abcdemn".toCharArray()));
		assertTrue(matcher.match("abcmn".toCharArray()));
		
	}
	
	@Test
	public void testMerge() {
		Sequence seq = new Pattern("ab(cd|ef)?").compile();
		Assert.assertEquals(seq.minPathLength,2);
		Assert.assertEquals(seq.maxPathLength,4);

		Sequence seq2 = new Pattern("ab(cd|gh)i").compile();
		Assert.assertEquals(seq2.minPathLength,5);
		Assert.assertEquals(seq2.maxPathLength,5);
		Util.mergeSequences(seq,seq2);
		Assert.assertEquals(seq.minPathLength,2);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abcd".toCharArray()));
		assertTrue(matcher.match("abef".toCharArray()));
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("abcdi".toCharArray()));
		assertTrue(matcher.match("abghi".toCharArray()));
		assertFalse(matcher.match("abi".toCharArray()));
	}
	
	
	
	@Test
	public void testOptional() {
		Sequence seq = new Pattern("ab?c").compile();
		Assert.assertEquals(seq.minPathLength,2);
		Assert.assertEquals(seq.maxPathLength,3);
		CoreMatcher matcher = new CoreMatcher(seq);
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
		Sequence seq = new Pattern("ab|cd").compile();
		Assert.assertEquals(seq.minPathLength,2);
		Assert.assertEquals(seq.maxPathLength,2);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("cd".toCharArray()));
		assertFalse(matcher.match("ac".toCharArray()));
		assertFalse(matcher.match("abfaltu".toCharArray()));
		
	}
	
	@Test
	public void testMultipleOr() {
		Sequence seq = new Pattern("b|bc|bcd").compile();
		Assert.assertEquals(seq.minPathLength,1);
		Assert.assertEquals(seq.maxPathLength,3);
		System.out.println(Util.toJson(seq));
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("b".toCharArray()));
		assertTrue(matcher.match("bc".toCharArray()));
		assertTrue(matcher.match("bcd".toCharArray()));
	}
	
	/*@Test
	public void testIteration() {
		Sequence seq = new Pattern("ab{53}cd").compile();
		SequenceLength depth = Util.calculateDepth(seq);
		Assert.assertEquals(seq.minPathLength,2);
		Assert.assertEquals(seq.maxPathLength,2);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("cd".toCharArray()));
		assertFalse(matcher.match("ac".toCharArray()));
		assertFalse(matcher.match("abfaltu".toCharArray()));
	}*/

	@Test
	public void testBackSlash() {
		Sequence seq = new Pattern("ab\\?c").compile();
		Assert.assertEquals(seq.minPathLength,4);
		Assert.assertEquals(seq.maxPathLength,4);
		CoreMatcher matcher = new CoreMatcher(seq);
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
		Sequence seq = new Pattern("ab[a-z0-9A-Z]\\[c").compile();
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(seq.minPathLength,5);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abm[c".toCharArray()));
		assertTrue(matcher.match("abM[c".toCharArray()));
		assertTrue(matcher.match("ab5[c".toCharArray()));
		assertFalse(matcher.match("ab[c".toCharArray()));
		assertFalse(matcher.match("abm8M[c".toCharArray()));
	}
	
	@Test
	public void testAny() {
		Sequence seq = new Pattern("ab.\\.c").compile();
		Assert.assertEquals(seq.minPathLength,5);
		Assert.assertEquals(seq.maxPathLength,5);
		CoreMatcher matcher = new CoreMatcher(seq);
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
		Sequence seq = new Pattern("a[bc]d").compile();
		Assert.assertEquals(seq.minPathLength,3);
		Assert.assertEquals(seq.maxPathLength,3);
		System.out.println(Util.toJson(seq));
		CoreMatcher matcher = new CoreMatcher(seq);
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		
		seq = new Pattern("a[bc]?d").compile();
		matcher.setSequence(seq);
		Assert.assertEquals(seq.minPathLength,2);
		Assert.assertEquals(seq.maxPathLength,3);
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		assertTrue(matcher.match("ad".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
	}
	
	
	@Test
	public void testToJson() {
		Sequence seq = new Pattern("a([bc])d(mn)?").compile();
		System.out.println(Util.toJson(seq));
	}
	
	
	@Test
	public void testLazyMatch() {
		os.nushi.jfree.matcher.LazyMatcher matcher = new os.nushi.jfree.matcher.LazyMatcher();
		Sequence seq = new Pattern("a([bc])d(mn|o)\\1a\\2").compile();

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
		
		seq = new Pattern("a(bc|d?e)?").compile();
		matcher.setSequence(seq);
		Assert.assertEquals(PASSED,matcher.match('a'));//PASSED
		Assert.assertEquals(PASSED,matcher.match('b','c'));//PASSED
		Assert.assertEquals(FAILED,matcher.match('e'));//FAILED
	}
	
	@Test
	public void testProgressiveMatch() {
		Sequence seq = new Pattern("a([bc])d(mn|o)\\1a\\2").compile();
		ProgressiveMatcher matcher = new ProgressiveMatcher(seq);
		
		Assert.assertEquals(FAILED,matcher.match());
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));
		Assert.assertEquals(MATCHED,matcher.match("abdob".toCharArray()));
		Assert.assertEquals(PASSED,matcher.match("abdobao".toCharArray()));
		System.out.println(seq.matchingGroups);
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));
		Assert.assertEquals(MATCHED,matcher.match("abdob".toCharArray()));
		Assert.assertEquals(FAILED,matcher.match("abdobamn".toCharArray()));
		System.out.println(seq.matchingGroups);
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));
		Assert.assertEquals(MATCHED,matcher.match("abdmnb".toCharArray()));
		Assert.assertEquals(PASSED,matcher.match("abdmnbamn".toCharArray()));
		System.out.println(seq.matchingGroups);
		
		
		matcher.reset();
		Assert.assertEquals(MATCHED,matcher.match("ab".toCharArray()));//MATCHED
		Assert.assertEquals(MATCHED,matcher.match("abdmnb".toCharArray()));//MATCHED
		Assert.assertEquals(MATCHED,matcher.match("abdmnbam".toCharArray()));//FAILED
		Assert.assertEquals(PASSED,matcher.match("abdmnbamn".toCharArray()));//FAILED Value of lazy node should not be broken
		System.out.println(seq.matchingGroups);
		
		seq = new Pattern("a(bc|d?e)?").compile();
		matcher.setSequence(seq);
		System.out.println(Util.toJson(seq));
		Assert.assertEquals(PASSED,matcher.match('a'));//PASSED
		Assert.assertEquals(PASSED,matcher.match("abc".toCharArray()));//PASSED
		Assert.assertEquals(FAILED,matcher.match("abce".toCharArray()));//FAILED
	}
	
	@Test
	public void test() {
		Sequence rootSeq = new Pattern("").compile();
		Sequence angleSeq = new Pattern("-?0(\\.0)?°", TokenType.ANGLE).compile();
		Sequence cordinateSeq = new Pattern("-?0(\\.0)?° -?0(\\.0)?['′] -?0(\\.0)?[\"″] a",TokenType.G_Cordinate).compile();
		Sequence tempratureSeq = new Pattern("-?0(\\.0)?° ?a",TokenType.TEMPRATURE).compile();
		
		//System.out.println(RESequenceUtil.toJson(angleSeq));
		//System.out.println(RESequenceUtil.toJson(cordinateSeq));
		//System.out.println(RESequenceUtil.toJson(tempratureSeq));
		
		//angleSeq.merge(cordinateSeq).merge(tempratureSeq);
		
		rootSeq.merge(angleSeq).merge(cordinateSeq).merge(tempratureSeq);
		System.out.println(Util.count(rootSeq));
		System.out.println(Util.toJson(rootSeq));
		
		ProgressiveMatcher matcher = new ProgressiveMatcher(rootSeq);
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
		Pattern pattern = new Pattern("\\.d(ab?|c)e?[x-z].\\1");
		//Pattern pattern = new Pattern("(abc)|abd");
		Sequence seq = pattern.compile();

		System.out.println(Util.toJson(seq));
		System.out.println(Util.count(seq));

		Matcher matcher = new CoreMatcher(seq);;


		Assert.assertEquals(PASSED,matcher.match(".dabey;ab".toCharArray()));

	}
}
