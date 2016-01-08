package os.nushi.booleansequence;

import static os.nushi.booleansequence.BooleanIdentifier.FAILED;
import static os.nushi.booleansequence.BooleanIdentifier.MATCHED;
import static os.nushi.booleansequence.BooleanIdentifier.PASSED;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import os.nushi.booleansequence.matcher.CoreMatcher;
import os.nushi.booleansequence.matcher.LazyMatcher;
import os.nushi.booleansequence.matcher.ProgressiveMatcher;
import os.nushi.booleansequence.model.SequenceLength;

public class BooleanSequenceTest {
	
	private void assertTrue(ExpressionIdentifier ei){
		Assert.assertEquals(BooleanIdentifier.PASSED ,ei);
	}
	
	private void assertFalse(ExpressionIdentifier ei){
		Assert.assertEquals(BooleanIdentifier.FAILED,ei);
	}
	
	@Test
	public void testMinimizationAndOptimization() {
		BooleanSequence seq = new BooleanSequence("ab(cd|ef)gh");
		seq.compile();
		Assert.assertEquals(9,BooleanSequenceUtil.count(seq));
		seq.minimize();
		Assert.assertEquals(BooleanSequenceUtil.count(seq),8);
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,6);
		Assert.assertEquals(depth.max,6);
		
		seq = new BooleanSequence("ab(cde|c)?mn");
		seq.compile();
		Assert.assertEquals(10,BooleanSequenceUtil.count(seq));
		seq.minimize();
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,4);
		Assert.assertEquals(depth.max,7);
		Assert.assertEquals(BooleanSequenceUtil.count(seq),7);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abmn".toCharArray()));
		assertTrue(matcher.match("abcdemn".toCharArray()));
		assertTrue(matcher.match("abcmn".toCharArray()));
		
	}
	
	@Test
	public void testMerge() {
		BooleanSequence seq = new BooleanSequence("ab(cd|ef)?");
		seq.compile().minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
		
		BooleanSequence seq2 = new BooleanSequence("ab(cd|gh)i");
		seq2.compile().minimize();
		SequenceLength depth2 = BooleanSequenceUtil.calculateDepth(seq2);
		Assert.assertEquals(depth2.min,5);
		Assert.assertEquals(depth2.max,5);
		BooleanSequenceUtil.mergeSequences(seq,seq2);
		depth = BooleanSequenceUtil.calculateDepth(seq);
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
		BooleanSequence seq = new BooleanSequence("ab?c");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
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
		
		seq = new BooleanSequence("(ab|cd)?ef");
		seq.compile();seq.minimize();
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,4);
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
	public void testOr() {
		BooleanSequence seq = new BooleanSequence("ab|cd");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,2);
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("ab".toCharArray()));
		assertTrue(matcher.match("cd".toCharArray()));
		assertFalse(matcher.match("ac".toCharArray()));
		assertFalse(matcher.match("abfaltu".toCharArray()));
	}
	
	@Test
	public void testIteration() {
		BooleanSequence seq = new BooleanSequence("ab{53}cd");
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
		BooleanSequence seq = new BooleanSequence("ab\\?c");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
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
		BooleanSequence seq = new BooleanSequence("ab[a-z0-9A-Z]\\[c");
		seq.compile();
		System.out.println(BooleanSequenceUtil.toJson(seq));
		seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		System.out.println(BooleanSequenceUtil.toJson(seq));
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
		BooleanSequence seq = new BooleanSequence("ab.\\.c");
		seq.compile();
		seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
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
		BooleanSequence seq = new BooleanSequence("a[bc]d");
		seq.compile();seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,3);
		Assert.assertEquals(depth.max,3);
		System.out.println(BooleanSequenceUtil.toJson(seq));
		CoreMatcher matcher = seq.getCoreMatcher();
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		
		seq = new BooleanSequence("a[bc]?d");
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		assertTrue(matcher.match("abd".toCharArray()));
		assertTrue(matcher.match("acd".toCharArray()));
		assertTrue(matcher.match("ad".toCharArray()));
		assertFalse(matcher.match("abc".toCharArray()));
		assertFalse(matcher.match("abcd".toCharArray()));
		
		seq = new BooleanSequence("a([bc]|d)?e");
		seq.compile();
		System.out.println(BooleanSequenceUtil.count(seq));
		seq.minimize();
		System.out.println(BooleanSequenceUtil.count(seq));
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,2);
		Assert.assertEquals(depth.max,3);
		matcher = seq.getCoreMatcher();
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
	public void testToJson() {
		BooleanSequence seq = new BooleanSequence("a([bc])d(mn)?");
		seq.capture = true;
		seq.compile();seq.minimize();
		System.out.println(BooleanSequenceUtil.toJson(seq));
	}
	
	
	@Test
	public void testLazyMatch() {
		LazyMatcher matcher = new LazyMatcher();
		BooleanSequence seq = new BooleanSequence("a([bc])d(mn|o)\\1a\\2");
		seq.capture = true;
		seq.compile().minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
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
		
		seq = new BooleanSequence("a(bc|d?e)?");
		seq.capture = false;
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min,1);
		Assert.assertEquals(depth.max,3);
		Assert.assertEquals(PASSED,matcher.match('a'));//PASSED
		Assert.assertEquals(PASSED,matcher.match('b','c'));//PASSED
		Assert.assertEquals(FAILED,matcher.match('e'));//FAILED
	}
	
	@Test
	public void testProgressiveMatch() {
		BooleanSequence seq = new BooleanSequence("a([bc])d(mn|o)\\1a\\2");
		seq.capture = true;
		seq.compile();
		seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
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
		
		seq = new BooleanSequence("a(bc|d?e)?");
		seq.capture = false;
		seq.compile();seq.minimize();
		matcher.setSequence(seq);
		System.out.println(BooleanSequenceUtil.toJson(seq));
		Assert.assertEquals(PASSED,matcher.match('a'));//PASSED
		Assert.assertEquals(PASSED,matcher.match("abc".toCharArray()));//PASSED
		Assert.assertEquals(FAILED,matcher.match("abce".toCharArray()));//FAILED
	}
	
	@Test
	public void testPathLength() {
		BooleanSequence seq = new BooleanSequence("[abc](ab|c?d)?ef");
		//seq.capture = true;
		seq.compile();
		seq.minimize();
		SequenceLength depth = BooleanSequenceUtil.calculateDepth(seq);
		Assert.assertEquals(depth.min, 3);
		Assert.assertEquals(depth.max, 5);
	}
	
	@Test
	public void perfTest(){
		for(int i = 0 ; i < 100000; i++);
		
		Pattern pattern = Pattern.compile("^[abc](ab|c?d)?ef$");
		BooleanSequence seq = new BooleanSequence("[abc](ab|c?d)?ef");
		seq.compile();seq.minimize();
		long startTime = 0L;
		System.gc();
		for(int i = 0 ; i < 100000; i++);
		Matcher matcher2 = pattern.matcher("");
		
		startTime = System.nanoTime();
		CoreMatcher matcher = seq.getCoreMatcher();
		for(int i = 0 ; i < 100000; i++){
			matcher.match("cabef".toCharArray());//long
			matcher.match("cef".toCharArray());//short
			matcher.match("ce".toCharArray());//invalid
			//matcher.match("cabefasde".toCharArray());//invalid long
		}
		System.out.println((System.nanoTime() - startTime)/100000);
		
		System.gc();
		for(int i = 0 ; i < 100000; i++);
		startTime = System.nanoTime();
		for(int i = 0 ; i < 100000; i++){
			/*matcher.reset("cabef");//long
			matcher.matches();
			matcher.reset("cef"); //short
			matcher.matches();
			matcher.reset("ce"); //invalid
			matcher.matches();*/
			
			matcher2.reset("cabef");//long
			matcher2.lookingAt();
			matcher2.reset("cef"); //short
			matcher2.lookingAt();
			matcher2.reset("ce"); //invalid
			matcher2.lookingAt();
		}
		System.out.println((System.nanoTime() - startTime)/100000);
		
		System.gc();
		
	}
	
	@Test
	public void test() {
		BooleanSequence rootSeq = new BooleanSequence("");rootSeq.compile().minimize();
		BooleanSequence angleSeq = new BooleanSequence("-?0(\\.0)?°",TokenType.ANGLE); angleSeq.compile().minimize();
		BooleanSequence cordinateSeq = new BooleanSequence("-?0(\\.0)?° -?0(\\.0)?['′] -?0(\\.0)?[\"″] a",TokenType.G_Cordinate);cordinateSeq.compile().minimize();
		BooleanSequence tempratureSeq = new BooleanSequence("-?0(\\.0)?° ?a",TokenType.TEMPRATURE);tempratureSeq.compile().minimize();
		
		//System.out.println(RESequenceUtil.toJson(angleSeq));
		//System.out.println(RESequenceUtil.toJson(cordinateSeq));
		//System.out.println(RESequenceUtil.toJson(tempratureSeq));
		
		//angleSeq.merge(cordinateSeq).merge(tempratureSeq);
		
		rootSeq.merge(angleSeq).merge(cordinateSeq).merge(tempratureSeq);
		System.out.println(BooleanSequenceUtil.count(rootSeq));
		System.out.println(BooleanSequenceUtil.toJson(rootSeq));
		
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
}
