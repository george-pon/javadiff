//
// ものすごく単純なdiffの実装
//
// 2008.08.02 by Jun Obama
//

package jp.or.rim.yk.george.diffmy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static final String DEV_NULL_FILE = "/dev/null";

	public static void usage(String msg) {
		System.out.println("SimpleDiff - text diff program.");
		System.out.println("usage)SimpleDiff old new");
		System.out.println("usage)SimpleDiff -r olddir newdir");
		System.exit(1);
	}

	/**
	 * ファイルから読み込んでString配列に格納する
	 * 
	 * @param fileName
	 * @return
	 */
	public static String[] readFromFile(String fileName) {
		ArrayList<String> strList = new ArrayList<String>();

		if (DEV_NULL_FILE.equals(fileName)) {
			return strList.toArray(new String[0]);
		}

		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(new File(fileName))));
			while (true) {
				String line = lnr.readLine();
				if (line == null) {
					break;
				}
				strList.add(line);
			}
			lnr.close();
			lnr = null;
		} catch (Exception e) {
			e.printStackTrace();
			if (lnr != null) {
				try {
					lnr.close();
					lnr = null;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}

		return strList.toArray(new String[0]);
	}

	// ファイル単位の比較を実行。
	private static void diffFile(String oldFileName, String newFileName) {
		String[] arrOld = readFromFile(oldFileName);
		String[] arrNew = readFromFile(newFileName);
		SimpleDiff diffObj = new SimpleDiff();
		diffObj.setOldFileName(oldFileName);
		diffObj.setNewFileName(newFileName);
		String[] result = diffObj.diff(arrOld, arrNew);
		if (result == null) {
			System.out.println("result = null");
			return;
		}
		for (String s : result) {
			System.out.println(s);
		}
		System.out.println("");
	}

	// サブディレクトリ名だけに限定
	private static String getChildPathName(File parent, File f) {
		String child = f.getAbsolutePath();
		String child_short = child.substring(parent.getAbsolutePath().length());
		return child_short;
	}

	// ファイル名のリストを返す
	private static List<File> getFileList(List<File> resultList, File parent, File f) {
		if (f.isFile()) {
			String child_short = getChildPathName(parent, f);
			resultList.add(new File(parent, child_short));
			return resultList;
		}
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File f2 : files) {
				getFileList(resultList, parent, f2);
			}
		}
		return resultList;
	}

	// ディレクトリ同士の比較を実施
	private static void diffRecursive(String oldFileName, String newFileName) {
		// 親ディレクトリ設定
		File oldParent = new File(oldFileName);
		File newParent = new File(newFileName);
		// ファイル名のリストを作成
		List<File> oldFileList = getFileList(new ArrayList<File>(), oldParent, new File(oldParent, ""));
		List<File> newFileList = getFileList(new ArrayList<File>(), newParent, new File(newParent, ""));
		// 処理順ファイルリスト作成
		LinkedHashMap<String, File> procFileMap = new LinkedHashMap<String, File>();
		for (File f : oldFileList) {
			String key = getChildPathName(oldParent, f);
			procFileMap.put(key, f);
		}
		for (File f : newFileList) {
			String key = getChildPathName(newParent, f);
			procFileMap.put(key, f);
		}
		// ソートするのでキーリストに詰め替え
		List<String> procFileMapKeyList = new ArrayList<String>();
		for (String s : procFileMap.keySet()) {
			procFileMapKeyList.add(s);
		}
		// 格納用マップ作成
		Map<String, File> oldFileMap = new HashMap<String, File>();
		for (File f : oldFileList) {
			String oldChildName = getChildPathName(oldParent, f);
			oldFileMap.put(oldChildName, f);
		}
		// 比較対象格納用マップ作成
		Map<String, File> newFileMap = new HashMap<String, File>();
		for (File f : newFileList) {
			String newChildName = getChildPathName(newParent, f);
			newFileMap.put(newChildName, f);
		}
		// ソート実施
		Collections.sort(procFileMapKeyList);
		// 比較実施
		for (String key : procFileMapKeyList) {
			File oldfile = oldFileMap.get(key);
			File newfile = newFileMap.get(key);

			if (oldfile != null && newfile != null) {
				// ファイルが両方存在する
				diffFile(oldfile.getPath(), newfile.getPath());
			} else if (oldfile == null) {
				// 新規ファイル
				diffFile(DEV_NULL_FILE, newfile.getPath());
			} else if (newfile == null) {
				// 削除ファイル
				diffFile(oldfile.getPath(), DEV_NULL_FILE);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String oldFileName = null;
		String newFileName = null;
		int count = 0;
		boolean isRecursive = false;

		/* check args */
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-r")) {
					isRecursive = true;
					continue;
				}
				usage("unknown option.");
				return;
			} else {
				switch (count) {
					case 0:
						oldFileName = args[i];
						count++;
						break;
					case 1:
						newFileName = args[i];
						count++;
						break;
					default:
						usage("too many file names.");
				}
			}
		}

		/* check invalid arg */
		if (oldFileName == null || newFileName == null) {
			usage("needs two file names.");
		}

		/* check recursive */
		if (isRecursive) {
			// ディレクトリ同士の比較実行
			diffRecursive(oldFileName, newFileName);
		} else {
			// ファイル同士の比較実行
			diffFile(oldFileName, newFileName);
		}
	}

}
