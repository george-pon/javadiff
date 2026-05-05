package jp.or.rim.yk.george.diff;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;

/**
 * Differentialのテスト。
 */
@ExtendWith(MockitoExtension.class)
public class DifferentialTest {

	/**
	 * LCSと差分のテスト。
	 */
	@Test
	public void testLCS() {
		Differential diff;
		List<String> x;
		List<String> y;
		List<String> lcs;
		List<DifferentialDetail> rslt;

		x = new ArrayList<String>();
		y = new ArrayList<String>();

		x.add("A");
		x.add("B");
		x.add("C");
		x.add("B");
		x.add("D");
		x.add("A");
		x.add("E");

		y.add("B");
		y.add("E");
		y.add("C");
		y.add("A");
		y.add("B");
		y.add("A");

		diff = new Differential(x, y);
		lcs = diff.getLCS();

		for (Iterator<String> itr = lcs.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(lcs.get(0).equals("B"));
		assertTrue(lcs.get(1).equals("C"));
		assertTrue(lcs.get(2).equals("B"));
		assertTrue(lcs.get(3).equals("A"));

		rslt = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt.get(0).equals(new DifferentialDetail(true, false, "A")));
		assertTrue(rslt.get(1).equals(new DifferentialDetail(true, true, "B")));
		assertTrue(rslt.get(2).equals(new DifferentialDetail(false, true, "E")));
		assertTrue(rslt.get(3).equals(new DifferentialDetail(true, true, "C")));
		assertTrue(rslt.get(4).equals(new DifferentialDetail(false, true, "A")));
		assertTrue(rslt.get(5).equals(new DifferentialDetail(true, true, "B")));
		assertTrue(rslt.get(6).equals(new DifferentialDetail(true, false, "D")));
		assertTrue(rslt.get(7).equals(new DifferentialDetail(true, true, "A")));
		assertTrue(rslt.get(8).equals(new DifferentialDetail(true, false, "E")));
	}

	/**
	 * LCSと差分のテスト、非常に似ているケース。
	 */
	@Test
	public void testLCS2() {
		Differential diff;
		List<String> x;
		List<String> y;
		List<String> lcs;
		List<DifferentialDetail> rslt;

		x = new ArrayList<String>();
		y = new ArrayList<String>();

		x.add("A");
		x.add("B");
		x.add("C");
		x.add("D");
		x.add("E");
		x.add("F");
		x.add("G");

		y.add("A");
		y.add("B");
		y.add("C");
		y.add("D");
		y.add("X");
		y.add("F");
		y.add("G");

		diff = new Differential(x, y);
		lcs = diff.getLCS();

		for (Iterator<String> itr = lcs.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(lcs.get(0).equals("A"));
		assertTrue(lcs.get(1).equals("B"));
		assertTrue(lcs.get(2).equals("C"));
		assertTrue(lcs.get(3).equals("D"));
		assertTrue(lcs.get(4).equals("F"));
		assertTrue(lcs.get(5).equals("G"));

		rslt = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt.get(0).equals(new DifferentialDetail(true, true, "A")));
		assertTrue(rslt.get(1).equals(new DifferentialDetail(true, true, "B")));
		assertTrue(rslt.get(2).equals(new DifferentialDetail(true, true, "C")));
		assertTrue(rslt.get(3).equals(new DifferentialDetail(true, true, "D")));
		assertTrue(rslt.get(4).equals(new DifferentialDetail(true, false, "E")));
		assertTrue(rslt.get(5).equals(new DifferentialDetail(false, true, "X")));
		assertTrue(rslt.get(6).equals(new DifferentialDetail(true, true, "F")));
		assertTrue(rslt.get(7).equals(new DifferentialDetail(true, true, "G")));
	}

	/**
	 * LCSと差分のテスト、片方が片方を含む。
	 */
	@Test
	public void testLCS3() {
		Differential diff;
		List<String> x;
		List<String> y;
		List<String> lcs;
		List<DifferentialDetail> rslt;

		x = new ArrayList<String>();
		y = new ArrayList<String>();

		x.add("A");
		x.add("B");
		x.add("C");
		x.add("D");
		x.add("E");
		x.add("F");
		x.add("G");

		y.add("B");
		y.add("C");
		y.add("D");
		y.add("E");

		diff = new Differential(x, y);
		lcs = diff.getLCS();

		for (Iterator<String> itr = lcs.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(lcs.get(0).equals("B"));
		assertTrue(lcs.get(1).equals("C"));
		assertTrue(lcs.get(2).equals("D"));
		assertTrue(lcs.get(3).equals("E"));

		rslt = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt.get(0).equals(new DifferentialDetail(true, false, "A")));
		assertTrue(rslt.get(1).equals(new DifferentialDetail(true, true, "B")));
		assertTrue(rslt.get(2).equals(new DifferentialDetail(true, true, "C")));
		assertTrue(rslt.get(3).equals(new DifferentialDetail(true, true, "D")));
		assertTrue(rslt.get(4).equals(new DifferentialDetail(true, true, "E")));
		assertTrue(rslt.get(5).equals(new DifferentialDetail(true, false, "F")));
		assertTrue(rslt.get(6).equals(new DifferentialDetail(true, false, "G")));
	}

	/**
	 * LCSと差分のテスト、片方が片方を、末尾に含む。
	 */
	@Test
	public void testLCS4() {
		Differential diff;
		List<String> x;
		List<String> y;
		List<String> lcs1;
		List<String> lcs2;
		List<DifferentialDetail> rslt1;
		List<DifferentialDetail> rslt2;

		x = new ArrayList<String>();
		y = new ArrayList<String>();

		x.add("A");
		x.add("B");
		x.add("C");
		x.add("D");
		x.add("E");
		x.add("F");
		x.add("G");

		y.add("D");
		y.add("E");
		y.add("F");
		y.add("G");

		diff = new Differential(x, y);
		lcs1 = diff.getLCS();

		for (Iterator<String> itr = lcs1.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(lcs1.get(0).equals("D"));
		assertTrue(lcs1.get(1).equals("E"));
		assertTrue(lcs1.get(2).equals("F"));
		assertTrue(lcs1.get(3).equals("G"));

		rslt1 = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt1.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt1.get(0).equals(new DifferentialDetail(true, false, "A")));
		assertTrue(rslt1.get(1).equals(new DifferentialDetail(true, false, "B")));
		assertTrue(rslt1.get(2).equals(new DifferentialDetail(true, false, "C")));
		assertTrue(rslt1.get(3).equals(new DifferentialDetail(true, true, "D")));
		assertTrue(rslt1.get(4).equals(new DifferentialDetail(true, true, "E")));
		assertTrue(rslt1.get(5).equals(new DifferentialDetail(true, true, "F")));
		assertTrue(rslt1.get(6).equals(new DifferentialDetail(true, true, "G")));

		// 反転して確認
		diff = new Differential(y, x);
		lcs2 = diff.getLCS();
		assertTrue(lcs1.equals(lcs2));

		rslt2 = diff.getDiff();
		assertTrue(rslt2.get(0).equals(new DifferentialDetail(false, true, "A")));
		assertTrue(rslt2.get(1).equals(new DifferentialDetail(false, true, "B")));
		assertTrue(rslt2.get(2).equals(new DifferentialDetail(false, true, "C")));
		assertTrue(rslt2.get(3).equals(new DifferentialDetail(true, true, "D")));
		assertTrue(rslt2.get(4).equals(new DifferentialDetail(true, true, "E")));
		assertTrue(rslt2.get(5).equals(new DifferentialDetail(true, true, "F")));
		assertTrue(rslt2.get(6).equals(new DifferentialDetail(true, true, "G")));
	}

	/**
	 * LCSと差分のテスト、片方が片方を、先頭に含む。
	 */
	@Test
	public void testLCS5() {
		Differential diff;
		List<String> x;
		List<String> y;
		List<String> lcs1;
		List<String> lcs2;
		List<DifferentialDetail> rslt1;
		List<DifferentialDetail> rslt2;

		x = new ArrayList<String>();
		y = new ArrayList<String>();

		x.add("A");
		x.add("B");
		x.add("C");
		x.add("D");
		x.add("E");
		x.add("F");
		x.add("G");

		y.add("A");
		y.add("B");
		y.add("C");
		y.add("D");

		diff = new Differential(x, y);
		lcs1 = diff.getLCS();

		for (Iterator<String> itr = lcs1.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(lcs1.get(0).equals("A"));
		assertTrue(lcs1.get(1).equals("B"));
		assertTrue(lcs1.get(2).equals("C"));
		assertTrue(lcs1.get(3).equals("D"));

		rslt1 = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt1.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt1.get(0).equals(new DifferentialDetail(true, true, "A")));
		assertTrue(rslt1.get(1).equals(new DifferentialDetail(true, true, "B")));
		assertTrue(rslt1.get(2).equals(new DifferentialDetail(true, true, "C")));
		assertTrue(rslt1.get(3).equals(new DifferentialDetail(true, true, "D")));
		assertTrue(rslt1.get(4).equals(new DifferentialDetail(true, false, "E")));
		assertTrue(rslt1.get(5).equals(new DifferentialDetail(true, false, "F")));
		assertTrue(rslt1.get(6).equals(new DifferentialDetail(true, false, "G")));

		// 反転して確認
		diff = new Differential(y, x);
		lcs2 = diff.getLCS();
		assertTrue(lcs1.equals(lcs2));

		rslt2 = diff.getDiff();
		assertTrue(rslt2.get(0).equals(new DifferentialDetail(true, true, "A")));
		assertTrue(rslt2.get(1).equals(new DifferentialDetail(true, true, "B")));
		assertTrue(rslt2.get(2).equals(new DifferentialDetail(true, true, "C")));
		assertTrue(rslt2.get(3).equals(new DifferentialDetail(true, true, "D")));
		assertTrue(rslt2.get(4).equals(new DifferentialDetail(false, true, "E")));
		assertTrue(rslt2.get(5).equals(new DifferentialDetail(false, true, "F")));
		assertTrue(rslt2.get(6).equals(new DifferentialDetail(false, true, "G")));
	}

	/**
	 * 空リストのテスト。
	 */
	@Test
	public void testEmpty() {
		Differential diff;
		List<String> x;
		List<String> y;
		List<DifferentialDetail> rslt;

		x = new ArrayList<String>();
		y = new ArrayList<String>();

		x.add("A");
		x.add("B");
		x.add("C");
		x.add("B");

		diff = new Differential(x, y);
		rslt = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt.get(0).equals(new DifferentialDetail(true, false, "A")));
		assertTrue(rslt.get(1).equals(new DifferentialDetail(true, false, "B")));
		assertTrue(rslt.get(2).equals(new DifferentialDetail(true, false, "C")));
		assertTrue(rslt.get(3).equals(new DifferentialDetail(true, false, "B")));

		diff = new Differential(y, x);
		rslt = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt.get(0).equals(new DifferentialDetail(false, true, "A")));
		assertTrue(rslt.get(1).equals(new DifferentialDetail(false, true, "B")));
		assertTrue(rslt.get(2).equals(new DifferentialDetail(false, true, "C")));
		assertTrue(rslt.get(3).equals(new DifferentialDetail(false, true, "B")));
	}

	/**
	 * LCS長が0のテスト。
	 */
	@Test
	public void testNoLcs() {
		Differential diff;
		List<String> x;
		List<String> y;
		List<DifferentialDetail> rslt;

		x = new ArrayList<String>();
		y = new ArrayList<String>();

		x.add("A");
		x.add("B");
		x.add("C");
		x.add("B");

		y.add("E");
		y.add("F");
		y.add("X");
		y.add("Y");
		y.add("Z");

		diff = new Differential(x, y);
		rslt = diff.getDiff();

		for (Iterator<DifferentialDetail> itr = rslt.iterator(); itr.hasNext();) {
			System.out.println(itr.next());
		}

		assertTrue(rslt.get(0).equals(new DifferentialDetail(true, false, "A")));
		assertTrue(rslt.get(1).equals(new DifferentialDetail(true, false, "B")));
		assertTrue(rslt.get(2).equals(new DifferentialDetail(true, false, "C")));
		assertTrue(rslt.get(3).equals(new DifferentialDetail(true, false, "B")));
		assertTrue(rslt.get(4).equals(new DifferentialDetail(false, true, "E")));
		assertTrue(rslt.get(5).equals(new DifferentialDetail(false, true, "F")));
		assertTrue(rslt.get(6).equals(new DifferentialDetail(false, true, "X")));
		assertTrue(rslt.get(7).equals(new DifferentialDetail(false, true, "Y")));
		assertTrue(rslt.get(8).equals(new DifferentialDetail(false, true, "Z")));
	}

}
