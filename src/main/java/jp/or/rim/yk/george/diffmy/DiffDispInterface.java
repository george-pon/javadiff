//
// Diffを表示する時の加工方法を指定するインタフェースクラス
//
// 同じ、挿入、削除、変更の時にそれぞれ呼ばれる。
//
// 2009.11.04
//

package jp.or.rim.yk.george.diffmy;

import java.util.List;

public interface DiffDispInterface {
	public List<String> same(List<String> line);

	public List<String> ins(List<String> line);

	public List<String> del(List<String> line);

	public List<String> mod(List<String> oldline, List<String> newline);
}
