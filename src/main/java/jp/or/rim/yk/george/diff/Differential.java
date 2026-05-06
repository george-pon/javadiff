package jp.or.rim.yk.george.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 差分を取得するクラス。
 * 
 * 比較要素の数だけ３次元で配列を確保するアルゴリズムなのでメモリ不足になる。
 * 
 * これは使用しない。
 */
public class Differential {
	private static final int MAX_LIST_LENGTH = 254;

	private List<String> x;
	private List<String> y;

	private int sizeOfX;
	private int sizeOfY;
	private int lcsLength;

	/**
	 * デフォルトコンストラクタ。 それぞれのリストのメンバーは、List#getで取得されるので、 リストの実装にはArrayListを強く推奨。
	 * 
	 * @param x リスト1。
	 * @param y リスト2。
	 */
	public Differential(List<String> x, List<String> y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * ふたつのリストのLCSを求める。 現在のアルゴリズムでは、LCS長計算のメモリ使用量がm*nなので、 よりメモリ使用量の 少ないアルゴリズムの検討要。
	 * また、LCSの検索も、改善の余地があると思われる。
	 * 
	 * @return LCS。リストが長すぎるなど、LCSを計算できない場合はnull。
	 */
	public List<String> getLCS() {
		List<String> lcs;
		int i;
		int j;
		byte[][] count;
		int xp, yp;

		sizeOfX = x.size();
		sizeOfY = y.size();

		// どちらかの要素が空ならLCSはなし
		if (sizeOfX == 0 || sizeOfY == 0) {
			return new ArrayList<String>();
		}

		// リスト長のチェック
		if (sizeOfX > MAX_LIST_LENGTH || sizeOfY > MAX_LIST_LENGTH) {
			return null;
		}

		// LCSの長さを計算
		count = new byte[sizeOfX + 1][sizeOfY + 1];
		for (i = 1; i <= sizeOfY; i++) {
			Object yobj = y.get(i - 1);

			for (j = 1; j <= sizeOfX; j++) {
				Object xobj = x.get(j - 1);

				if (xobj.equals(yobj)) {
					count[j][i] = (byte) (((int) count[j - 1][i - 1] & 0xff) + 1);
				} else {
					count[j][i] = (byte) Math.max(((int) count[j][i - 1] & 0xff), ((int) count[j - 1][i] & 0xff));
				}
			}
		}

		// for (i = 0; i <= sizeOfY; i++) {
		// for (j = 0; j <= sizeOfX; j++) {
		// String cs = " " + count[j][i];
		// System.out.print(cs.substring(cs.length() - 4));
		// }
		// System.out.println();
		// }

		lcsLength = (int) count[sizeOfX][sizeOfY] & 0xff;

		// LCSあり?
		if (lcsLength == 0) {
			return new ArrayList<String>();
		}

		// LCSを検索
		lcs = new ArrayList<String>(lcsLength);

		xp = sizeOfX;
		yp = sizeOfY;
		while (xp > 0 && yp > 0) {
			// countが同じものを左にたどる
			while (count[xp - 1][yp] == count[xp][yp]) {
				xp--;
			}
			// countが同じものを上にたどる
			while (count[xp][yp - 1] == count[xp][yp]) {
				yp--;
			}

			lcs.add(x.get(xp - 1));
			xp--;
			yp--;
			if (count[xp][yp] == 0) {
				break;
			}
		}

		if (lcs.size() != lcsLength) {
			throw new IllegalStateException("" + "Internal error. LCS length mismatch.");
		}

		Collections.reverse(lcs); // RANDOM_ACCESS_LISTでは、反転のコストは軽い
		return lcs;
	}

	/**
	 * 差分を取得する。
	 * 
	 * @return 差分のリスト。メンバーはDifferentialDetail。 差分が取得できなければnull。
	 */
	public List<DifferentialDetail> getDiff() {
		List<DifferentialDetail> diff;
		List<String> lcs;
		Iterator<String> itrx;
		Iterator<String> itry;
		Iterator<String> itrz;
		Object xn;
		Object yn;
		Object zn;

		lcs = getLCS();
		if (lcs == null) {
			return null;
		}

		diff = new ArrayList<DifferentialDetail>();

		itrx = x.iterator();
		itry = y.iterator();
		itrz = lcs.iterator();
		zn = itrz.hasNext() ? itrz.next() : null;

		while (itrx.hasNext()) {
			xn = itrx.next();

			if (zn == null || !xn.equals(zn)) {
				// xにしかない、xの要素を追加
				diff.add(new DifferentialDetail(true, false, xn));
			} else {
				// xとlcsで共通

				// yの要素を追加
				while (itry.hasNext()) {
					yn = itry.next();
					if (yn.equals(zn)) {
						break;
					}

					diff.add(new DifferentialDetail(false, true, yn));
				}

				// 共通の要素を追加
				diff.add(new DifferentialDetail(true, true, xn));

				zn = itrz.hasNext() ? itrz.next() : null;
			}
		}

		// 残りのyの要素を追加
		while (itry.hasNext()) {
			yn = itry.next();
			diff.add(new DifferentialDetail(false, true, yn));
		}

		return diff;
	}

}
