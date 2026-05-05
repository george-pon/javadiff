//
// diff -u 形式の出力を作るクラスの実装
//
// 2009.11.04
//

package jp.or.rim.yk.george.diffmy;

import java.util.ArrayList;
import java.util.List;

public class DiffDispUnified implements DiffDispInterface {
	@Override
	public List<String> same(List<String> lines) {
		List<String> list = new ArrayList<String>();
		for (String s : lines) {
			list.add(" " + s);
		}
		return list;
	}

	@Override
	public List<String> ins(List<String> lines) {
		List<String> list = new ArrayList<String>();
		for (String s : lines) {
			list.add("+" + s);
		}
		return list;
	}

	@Override
	public List<String> del(List<String> lines) {
		List<String> list = new ArrayList<String>();
		for (String s : lines) {
			list.add("-" + s);
		}
		return list;
	}

	@Override
	public List<String> mod(List<String> oldlines, List<String> newlines) {
		List<String> list = new ArrayList<String>();
		for (String s : oldlines) {
			list.add("-" + s);
		}
		for (String s : newlines) {
			list.add("+" + s);
		}
		return list;
	}
}
