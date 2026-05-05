//
// ものすごくショボいdiffの実装をちょっと動かす
//
// 2008.08.02 by Jun Obama
//
package jp.or.rim.yk.george.diffmy;

public class TestSimpleDiff {

	public static void test(String msg, String[] old1, String[] new1) {
		int maxlen = 0;
		System.out.println("==== " + msg + " ====");
		if (maxlen < old1.length) {
			maxlen = old1.length;
		}
		if (maxlen < new1.length) {
			maxlen = new1.length;
		}
		for (int i = 0; i < maxlen; i++) {
			String strold = "(null)";
			String strnew = "(null)";
			if (i < old1.length) {
				strold = old1[i];
			}
			if (i < new1.length) {
				strnew = new1[i];
			}
			System.out.format("[%02d]  %-18s  %-18s\n", i, strold, strnew);
		}
		System.out.flush();
		String[] result = new SimpleDiff().diff(old1, new1);
		if (result == null) {
			System.out.println("result = null");
			return;
		}
		for (String s : result) {
			System.out.println(s);
		}
		System.out.println("");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		{
			String[] old1 = new String[] { "a" };
			String[] new1 = new String[] { "a" };
			test("same1", old1, new1);
		}
		{
			String[] old1 = new String[] { "a" };
			String[] new1 = new String[] { "b" };
			test("diff1", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "a" };
			String[] new1 = new String[] { "a", "a" };
			test("same2", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "a" };
			String[] new1 = new String[] { "a", "b" };
			test("diff2", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "a" };
			String[] new1 = new String[] { "a", "b" };
			test("complex2", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "a" };
			String[] new1 = new String[] { "b", "a" };
			test("complex2", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e" };
			String[] new1 = new String[] { "a", "b", "c", "d", "e" };
			test("same5", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e" };
			String[] new1 = new String[] { "a", "b", "c", "z", "d", "e" };
			test("insert5", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "a", "b", "c", "x", "y", "z", "d", "e", "f", "g", "h", "i" };
			test("insert", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "x", "y", "z" };
			test("insert last", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "x", "y", "z", "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			test("insert first", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "a", "b", "c", "g", "h", "i" };
			test("delete", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "a", "b", "c", "d", "e", "f" };
			test("delete last", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "d", "e", "f", "g", "h", "i" };
			test("delete first", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "a", "b", "c", "x", "y", "z", "g", "h", "i" };
			test("modify", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "a", "b", "c", "d", "e", "f", "x", "y", "z" };
			test("modify last", old1, new1);
		}
		{
			String[] old1 = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
			String[] new1 = new String[] { "a", "b", "c", "x", "y", "z", "d", "e", "f", };
			test("complex", old1, new1);
		}
	}

}
