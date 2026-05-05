package jp.or.rim.yk.george.diff;

/**
 * 差分の詳細を示すためのクラス。
 */
public class DifferentialDetail {
	/** xにこのオブジェクトが含まれる。 */
	public boolean x;

	/** yにこのオブジェクトが含まれる。 */
	public boolean y;

	/** オブジェクトそれ自身。 */
	public Object object;

	/**
	 * コンストラクタ。
	 * 
	 * @param x xに含まれるか。
	 * @param y yに含まれるか。
	 * @param o オブジェクト。
	 */
	public DifferentialDetail(boolean x, boolean y, Object o) {
		this.x = x;
		this.y = y;
		this.object = o;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[DifferentialDetail:" + x + "," + y + "," + object + "]";
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		DifferentialDetail od;

		if (!(o instanceof DifferentialDetail)) {
			return false;
		}

		od = (DifferentialDetail) o;
		return x == od.x && y == od.y
				&& ((object == null)
						? od.object == null
						: object.equals(od.object));
	}

}
