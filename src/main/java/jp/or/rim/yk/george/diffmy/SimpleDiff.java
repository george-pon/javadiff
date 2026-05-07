//
// ものすごく単純なdiffの実装
//
// よく知られたdiffアルゴリズムは３次元空間が必要なので、tomcat用にありがちな256Mbytesのヒープメモリだとメモリ不足になる。
// ここでは、総当り比較を実施する。
//
// 2008.08.02 by Jun Obama
//

package jp.or.rim.yk.george.diffmy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SimpleDiff {
	// ログ
	private java.util.logging.Logger log = java.util.logging.Logger.getLogger(this.getClass().getName());

	private String[] arrOld = null;
	private String[] arrNew = null;
	ArrayList<DiffChunk> listChunk = null;
	private int oldCur = 0;
	private int newCur = 0;
	private String oldFileName = "old";
	private String newFileName = "new";
	private int diffResultCode = 0;

	public int getDiffResultCode() {
		return this.diffResultCode;
	}

	// intの値の小さいほうを返す
	public static int min(int a, int b) {
		if (a < b) {
			return a;
		}
		return b;
	}

	// intの値の大きいほうを返す
	public static int max(int a, int b) {
		if (a > b) {
			return a;
		}
		return b;
	}

	// クラス内クラス oldPosOffset, newPosOffsetを保持する
	private class DiffOffsets {
		public int oldPosOffset;
		public int newPosOffset;

		DiffOffsets(int oldpos, int newpos) {
			oldPosOffset = oldpos;
			newPosOffset = newpos;
		}
	}

	// クラス内クラスの比較クラス。
	private class DiffOffsetsCompare implements Comparator<DiffOffsets> {
		@Override
		public int compare(DiffOffsets o1, DiffOffsets o2) {
			int o1_val = SimpleDiff.min(o1.oldPosOffset, o1.newPosOffset);
			int o2_val = SimpleDiff.min(o2.oldPosOffset, o2.newPosOffset);
			return o1_val - o2_val;
		}
	}

	// コンストラクタ
	public SimpleDiff() {
		this.listChunk = new ArrayList<DiffChunk>();
	}

	// 普通のdiffインタフェース
	public String[] diff(String[] arrOld, String[] arrNew) {
		this.arrOld = arrOld;
		this.arrNew = arrNew;

		// diff chunk list 調査と作成
		createDiffChunkList(arrOld, arrNew);

		// diff 差分の有無を保存
		this.diffResultCode = diffChunkToInt(this.listChunk);

		// diff chunk から 結果を作成する
		return diffChunkToUnifiedDiffString(this.listChunk).toArray(new String[0]);
	}

	// 表示形式指定でdiffを行う
	public String[] diff(String[] arrOld, String[] arrNew, DiffDispInterface disp) {
		this.arrOld = arrOld;
		this.arrNew = arrNew;

		// diff chunk list 調査と作成
		createDiffChunkList(arrOld, arrNew);

		// diff 差分の有無を保存
		this.diffResultCode = diffChunkToInt(this.listChunk);

		// diff chunk から 結果を作成する
		return generateDisplayStringList(this.listChunk, disp).toArray(new String[0]);
	}

	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
	}

	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
	}

	// 差分チェック処理
	private void createDiffChunkList(String[] arrOld, String[] arrNew) {
		this.arrOld = arrOld;
		this.arrNew = arrNew;

		while (true) {
			// 終了判定１
			if (oldCur == arrOld.length && newCur == arrNew.length) {
				break;
			}
			// 終了判定２
			if (oldCur == arrOld.length && newCur != arrNew.length) {
				DiffChunk chunk = new DiffChunk(ChangeMode.INSERT_MODE, oldCur, newCur, arrNew.length - newCur);
				this.listChunk.add(chunk);
				break;
			}
			// 終了判定３
			if (oldCur != arrOld.length && newCur == arrNew.length) {
				DiffChunk chunk = new DiffChunk(ChangeMode.DELETE_MODE, oldCur, newCur, arrOld.length - oldCur);
				this.listChunk.add(chunk);
				break;
			}

			// 無変更・追加・削除・変更判定
			DiffChunk chunk = checkSameInsertDeleteModify(oldCur, newCur, 3);
			if (chunk == null) {
				// 差分判定が起きなかった。1行差分として生成
				DiffChunk chunk1 = new DiffChunk(ChangeMode.MODIFY_MODE, oldCur, newCur, 1);
				this.listChunk.add(chunk1);
				oldCur++;
				newCur++;
				continue;
			}

			// DiffChunkに従ってカーソル移動
			log.finest("chunk:" + chunk.toString());
			switch (chunk.mode) {
				case SAME_MODE:
					oldCur += chunk.length;
					newCur += chunk.length;
					this.listChunk.add(chunk);
					break;
				case INSERT_MODE:
					newCur += chunk.length;
					this.listChunk.add(chunk);
					break;
				case DELETE_MODE:
					oldCur += chunk.length;
					this.listChunk.add(chunk);
					break;
				case MODIFY_MODE:
					oldCur += chunk.length;
					newCur += chunk.length;
					this.listChunk.add(chunk);
					break;
				default:
					log.warning("Hummmm....\n");
					break;
			} // end of switch

		} // end of while true
	}

	// Chunkリストを文字列diff形式に変換する
	// 結果はこんな感じ
	// diff -u old1 new1
	// --- old1 Sat Aug 2 23:18:14 2008
	// +++ new1 Sat Aug 2 23:18:10 2008
	// @@ -1,2 +1,2 @@
	// a
	// -a
	// +b
	private ArrayList<String> diffChunkToUnifiedDiffString(ArrayList<DiffChunk> listChunk) {
		ArrayList<String> listResult = new ArrayList<String>();
		int oldPos = 0;
		int newPos = 0;
		for (DiffChunk chunk : listChunk) {
			switch (chunk.mode) {
				case SAME_MODE:
					listResult.add("--- " + this.oldFileName + "   same");
					listResult.add("+++ " + this.newFileName + "   same");
					for (int pos = 0; pos < chunk.length; pos++) {
						listResult.add(" " + arrOld[oldPos]);
						oldPos++;
						newPos++;
					}
					break;
				case INSERT_MODE:
					listResult.add("--- " + this.oldFileName + "");
					listResult.add("+++ " + this.newFileName + "     insert");
					listResult
							.add("@@ -" + oldPos + "," + (oldPos) + " +" + newPos + "," + (newPos + chunk.length)
									+ " @@");
					for (int pos = 0; pos < chunk.length; pos++) {
						listResult.add("+" + arrNew[newPos]);
						newPos++;
					}
					break;
				case DELETE_MODE:
					listResult.add("--- " + this.oldFileName + "     delete");
					listResult.add("+++ " + this.newFileName + "");
					listResult
							.add("@@ -" + oldPos + "," + (oldPos + chunk.length) + " +" + newPos + "," + (newPos)
									+ " @@");
					for (int pos = 0; pos < chunk.length; pos++) {
						listResult.add("-" + arrOld[oldPos]);
						oldPos++;
					}
					break;
				case MODIFY_MODE:
					listResult.add("--- " + this.oldFileName + "    modify");
					listResult.add("+++ " + this.newFileName + "    modify");
					listResult.add("@@ -" + oldPos + "," + (oldPos + chunk.length) + " +" + newPos + ","
							+ (newPos + chunk.length) + " @@");
					for (int pos = 0; pos < chunk.length; pos++) {
						listResult.add("-" + arrOld[oldPos]);
						oldPos++;
					}
					for (int pos = 0; pos < chunk.length; pos++) {
						listResult.add("+" + arrNew[newPos]);
						newPos++;
					}
					break;
				default:
					System.err.println("Hummmm....\n");
					break;
			} // end of switch chunk.mode
		} // end of for listChunk
		return listResult;
	}

	// Chunkリストをスキャンして差分があるかどうかチェック。
	//
	// 差分がある場合は 0 ではない値を返却。
	//
	// 差分が無い場合は 0 を返却。
	//
	private int diffChunkToInt(ArrayList<DiffChunk> listChunk) {
		for (DiffChunk chunk : listChunk) {
			switch (chunk.mode) {
				case SAME_MODE:
					continue;
				case INSERT_MODE:
					return 1;
				case DELETE_MODE:
					return 1;
				case MODIFY_MODE:
					return 1;
				default:
					System.err.println("Hummmm....\n");
					break;
			} // end of switch chunk.mode
		} // end of for listChunk
		return 0;
	}

	//
	// DiffDispInterfaceを使った表示を行う
	//
	public ArrayList<String> generateDisplayStringList(ArrayList<DiffChunk> listChunk, DiffDispInterface disp) {
		ArrayList<String> listResult = new ArrayList<String>();
		int oldPos = 0;
		int newPos = 0;
		for (DiffChunk chunk : listChunk) {
			switch (chunk.mode) {
				case SAME_MODE: {
					ArrayList<String> lines = new ArrayList<String>();
					for (int pos = 0; pos < chunk.length; pos++) {
						lines.add(arrOld[oldPos]);
						oldPos++;
						newPos++;
					}
					listResult.addAll(disp.same(lines));
				}
					break;
				case INSERT_MODE: {
					ArrayList<String> lines = new ArrayList<String>();
					for (int pos = 0; pos < chunk.length; pos++) {
						lines.add(arrNew[newPos]);
						newPos++;
					}
					listResult.addAll(disp.ins(lines));
				}
					break;
				case DELETE_MODE: {
					ArrayList<String> lines = new ArrayList<String>();
					for (int pos = 0; pos < chunk.length; pos++) {
						lines.add(arrOld[oldPos]);
						oldPos++;
					}
					listResult.addAll(disp.del(lines));
				}
					break;
				case MODIFY_MODE: {
					ArrayList<String> oldlines = new ArrayList<String>();
					ArrayList<String> newlines = new ArrayList<String>();
					for (int pos = 0; pos < chunk.length; pos++) {
						oldlines.add(arrOld[oldPos]);
						oldPos++;
					}
					for (int pos = 0; pos < chunk.length; pos++) {
						newlines.add(arrNew[newPos]);
						newPos++;
					}
					listResult.addAll(disp.mod(oldlines, newlines));
				}
					break;
				default:
					break;
			} // end of switch chunk.mode
		} // end of for listChunk
		return listResult;
	}

	// 変更、追加、削除を探索していく。
	private DiffChunk checkSameInsertDeleteModify(int oldpos, int newpos, int len) {
		log.finest(String.format("enter (oldpos=%d, newpos=%d, len=%d)", oldpos, newpos, len));
		int sameLength = lengthOfSame(oldpos, newpos);
		int sameOffset = 0;
		int insertOffset = indexOfNew(newpos, oldpos, len) - newpos;
		int deleteOffset = indexOfOld(oldpos, newpos, len) - oldpos;
		int modifyOffset = offsetOfModify(oldpos, newpos, len);
		ArrayList<DiffOffsets> sameBlockList = new ArrayList<DiffOffsets>();
		int sameBlockOffset = offsetOfNextMatchBlock(oldpos, newpos, len, sameBlockList);
		DiffChunk chunk = null;
		ChangeMode mode;
		int offset;

		if (sameLength < 0) {
			sameOffset = Integer.MAX_VALUE;
		}
		if (insertOffset < 0) {
			insertOffset = Integer.MAX_VALUE;
		}
		if (deleteOffset < 0) {
			deleteOffset = Integer.MAX_VALUE;
		}
		if (modifyOffset < 0) {
			modifyOffset = Integer.MAX_VALUE;
		}
		if (sameBlockOffset < 0) {
			sameBlockOffset = Integer.MAX_VALUE;
		}

		log.finest(String.format(
				"oldpos=%d, newpos=%d, len=%d, sameLength=%d  insertOffset=%d  deleteOffset=%d  modifyOffset=%d  sameBlockOffset=%d",
				oldpos, newpos, len, sameLength, insertOffset, deleteOffset, modifyOffset, sameBlockOffset));

		mode = ChangeMode.DEFAULT_MODE;
		offset = Integer.MAX_VALUE;

		// offsetが小さいものを選択
		if (sameOffset < offset) {
			mode = ChangeMode.SAME_MODE;
			offset = sameOffset;
		}
		if (insertOffset < offset) {
			mode = ChangeMode.INSERT_MODE;
			offset = insertOffset;
		}
		if (deleteOffset < offset) {
			mode = ChangeMode.DELETE_MODE;
			offset = deleteOffset;
		}
		if (modifyOffset < offset) {
			mode = ChangeMode.MODIFY_MODE;
			offset = modifyOffset;
		}
		// 複合モード。ここでsameBlockOffsetが最小ならいきなりreturnする。
		if (sameBlockOffset < offset) {
			int old_off = sameBlockList.get(0).oldPosOffset;
			int new_off = sameBlockList.get(0).newPosOffset;
			if (old_off != 0 && new_off != 0) {
				if (old_off < new_off) {
					return new DiffChunk(ChangeMode.DELETE_MODE, oldpos, newpos, old_off);
				} else {
					return new DiffChunk(ChangeMode.INSERT_MODE, oldpos, newpos, new_off);
				}
			}
		}

		switch (mode) {
			case DEFAULT_MODE:
				return null;
			case SAME_MODE:
				chunk = new DiffChunk();
				chunk.mode = mode;
				chunk.oldPos = oldpos;
				chunk.newPos = newpos;
				chunk.length = sameLength;
				return chunk;
			case INSERT_MODE:
				chunk = new DiffChunk();
				chunk.mode = mode;
				chunk.oldPos = oldpos;
				chunk.newPos = newpos;
				chunk.length = insertOffset;
				return chunk;
			case DELETE_MODE:
				chunk = new DiffChunk();
				chunk.mode = mode;
				chunk.oldPos = oldpos;
				chunk.newPos = newpos;
				chunk.length = deleteOffset;
				return chunk;
			case MODIFY_MODE:
				chunk = new DiffChunk();
				chunk.mode = mode;
				chunk.oldPos = oldpos;
				chunk.newPos = newpos;
				chunk.length = modifyOffset;
				return chunk;
			default:
				log.finest("hummmmmm......");
				return null;
		}
	}

	private int lengthOfSame(int oldPos, int newPos) {
		int pos = 0;
		boolean bSame = false;
		while (true) {
			if (arrOld[oldPos + pos].equals(arrNew[newPos + pos])) {
				bSame = true;
			} else {
				break;
			}
			pos++;
			if (oldPos + pos >= arrOld.length) {
				break;
			}
			if (newPos + pos >= arrNew.length) {
				break;
			}
		}
		if (bSame == true) {
			return pos;
		}
		return -1;
	};

	// new側配列をnewpos以降から探索し、oldposからlen個の内容とマッチする先頭位置を返す
	private int indexOfNew(int newPos, int oldPos, int len) {
		log.finest(String.format("enter (newPos=%d  oldPos=%d)", newPos, oldPos));
		for (int pos = newPos; pos < arrNew.length; pos++) {
			if (arrOld[oldPos].equals(arrNew[pos])) {
				boolean bAllHit = true;
				for (int hitCount = 0; hitCount < len; hitCount++) {
					if (oldPos + hitCount >= arrOld.length) {
						continue;
					}
					if (pos + hitCount >= arrNew.length) {
						continue;
					}
					if (!arrOld[oldPos + hitCount].equals(arrNew[pos + hitCount])) {
						bAllHit = false;
						break;
					}
				}
				if (bAllHit == true) {
					return pos;
				}
			}
		}
		return -1;
	}

	// old側配列をoldpos以降から探索し、newposからlen個の内容とマッチする先頭位置を返す
	private int indexOfOld(int oldPos, int newPos, int len) {
		log.finest(String.format("enter (oldpos=%d, newpos=%d)", oldPos, newPos));
		for (int pos = oldPos; pos < arrOld.length; pos++) {
			if (arrOld[pos].equals(arrNew[newPos])) {
				boolean bAllHit = true;
				for (int hitCount = 0; hitCount < len; hitCount++) {
					if (pos + hitCount >= arrOld.length) {
						continue;
					}
					if (newPos + hitCount >= arrNew.length) {
						continue;
					}
					if (!arrOld[pos + hitCount].equals(arrNew[newPos + hitCount])) {
						bAllHit = false;
						break;
					}
				}
				if (bAllHit == true) {
					return pos;
				}
			}
		}
		return -1;
	}

	// 現在位置から同じ行数だけスキップしてlen個連続してマッチするオフセットを返す
	private int offsetOfModify(int oldPos, int newPos, int len) {
		log.finest(String.format("enter (oldpos=%d, newpos=%d)", oldPos, newPos));
		int hitCount = 0;
		String strOld = null;
		String strNew = null;
		for (int pos = 0; true; pos++) {
			strOld = null;
			strNew = null;
			if (hitCount >= len) {
				return pos - len;
			}
			if (oldPos + pos < arrOld.length) {
				strOld = arrOld[oldPos + pos];
			} else {
				return -1;
			}
			if (newPos + pos < arrNew.length) {
				strNew = arrNew[newPos + pos];
			} else {
				return -1;
			}
			if (strOld.equals(strNew)) {
				hitCount++;
				continue;
			}
			hitCount = 0;
		}
	}

	// 現在位置から異なる行数をスキップして、len個連続してマッチするオフセットを返す
	private int offsetOfNextMatchBlock(int oldPos, int newPos, int len, ArrayList<DiffOffsets> results) {
		int hitCount = 0;
		int i;
		String strOld = null;
		String strNew = null;
		for (int off_old = 0; off_old < 50; off_old++) {
			for (int off_new = 0; off_new < 50; off_new++) {
				hitCount = 0;
				for (i = 0; i < len; i++) {
					if (oldPos + off_old + i < arrOld.length) {
						strOld = arrOld[oldPos + off_old + i];
					} else {
						break;
					}
					if (newPos + off_new + i < arrNew.length) {
						strNew = arrNew[newPos + off_new + i];
					} else {
						break;
					}
					if (strOld.equals(strNew)) {
						hitCount++;
						if (hitCount >= len) {
							results.add(new DiffOffsets(off_old, off_new));
							hitCount = 0;
						}
					} else {
						break;
					}
				}
			}
		}

		// 結果集計
		if (results.size() == 0) {
			return -1;
		}

		// ソート
		Collections.sort(results, new DiffOffsetsCompare());

		// オフセットが一番小さなものを返す
		return SimpleDiff.min(results.get(0).oldPosOffset, results.get(0).newPosOffset);
	}

}
