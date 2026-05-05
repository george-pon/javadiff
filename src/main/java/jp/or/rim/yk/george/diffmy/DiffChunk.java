//
// ものすごくシンプルなdiffの実装用。
// 検出した差分の属性と行番号と長さを保持。
//
// 2008.08.02 by Jun Obama
//
package jp.or.rim.yk.george.diffmy;

enum ChangeMode {
	INSERT_MODE,
	DELETE_MODE,
	MODIFY_MODE,
	SAME_MODE,
	DEFAULT_MODE
};

class DiffChunk {
	public ChangeMode mode;
	public int oldPos;
	public int newPos;
	public int length;

	public DiffChunk() {
		this.mode = ChangeMode.DEFAULT_MODE;
		this.oldPos = -1;
		this.newPos = -1;
		this.length = -1;
	}

	public DiffChunk(ChangeMode mode, int oldPos, int newPos, int length) {
		this.mode = mode;
		this.oldPos = oldPos;
		this.newPos = newPos;
		this.length = length;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChangeMode=" + mode);
		sb.append(", oldPos=" + oldPos);
		sb.append(", newPos=" + newPos);
		sb.append(", length=" + length);
		return sb.toString();
	}
}
