package com.example.nihongo.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.nihongo.WordBean;

public class FileUtil {
	public static LinkedList<String> readFromFile(InputStream is) {
		if (is == null) {
			return null;
		}
		LinkedList<String> ret = new LinkedList<>();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			InputStreamReader reader = new InputStreamReader(is);
			br = new BufferedReader(reader);
			String lineStr = "";
			while ((lineStr = br.readLine()) != null) {
				ret.add(lineStr);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			release(fr);
			release(br);
		}
		if (ret.size() == 0) {// 如果读取后长度仍为零
			return null;
		}
		return ret;

	}

	/**
	 * 释放closable的资源
	 * 
	 * @param closeobj
	 */
	public static void release(Closeable closeobj) {
		if (closeobj != null) {
			try {
				closeobj.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			closeobj = null;
		}
	}

	/**
	 * 得到utf8文件头的char
	 * 
	 * @return
	 */
	private static Character getTHEChar() {
		byte a = (byte) 239;// 0xef(byte)0xef;eb
		byte b = (byte) 187;// (byte)0xbb;bc
		byte c = (byte) 191;// 0xbf;80
		// char ret =
		// (char)(((a&0x000000ff)<<24)|(b&0x000000ff)<<16|(c&0x000000ff)<<8|0x00000000);
		// return (Character)ret;
		char[] chars = getChars(new byte[] { a, b, c });
		return (Character) chars[0];
	}

	/**
	 * bytes转chars
	 * 
	 * @param bytes
	 * @return
	 */
	private static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}

	/**
	 * 
	 * @param lessonNum
	 *            1~32
	 * @return
	 */
	public static LinkedList<String> getLineStrsofLesson(Context context,
			int lessonNum) {
		AssetManager am = context.getResources().getAssets();
		String lessonName = "";
		if (lessonNum < 10) {
			lessonName = "lesson0" + lessonNum;
		} else {
			lessonName = "lesson" + lessonNum;
		}

		try {
			String[] list = am.list("vocabularies");
			for (String string : list) {
				if (string.equals(lessonName)) {
					InputStream is = am.open("vocabularies/" + string);
					LinkedList<String> readFromFile = FileUtil.readFromFile(is);
					return readFromFile;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 词条开头应该有三个数字和一个点，形如“023.” 词条应该有1行或者3行
	 * 
	 * @param context
	 * @param lessonNum
	 * @return
	 */
	public static LinkedList<WordBean> getVocabulories(Context context,
			int lessonNum) {
		LinkedList<String> lineStrsofLesson = getLineStrsofLesson(context,
				lessonNum);
		LinkedList<WordBean> ret = new LinkedList<>();
		for (int i = 1; i <= lineStrsofLesson.size() - 1;) {// 按理说每次进来的i都是词条的头
			String lineStr = lineStrsofLesson.get(i - 1);
			if (!isStartofAWord(lineStr)) {
				throw new RuntimeException("!isStartofAWord(" + lineStr + ")");
			}
			LinkedList<String> wordLines = new LinkedList<>();
			wordLines.add(lineStr);

			while (true) {
				i++;
				if (i > lineStrsofLesson.size() - 1) {
					break;
				}
				String lineNow = lineStrsofLesson.get(i - 1);
				if (isStartofAWord(lineNow)) {
					// 到下一个词条了
					break;
				} else {
					// 还是这个词条
					wordLines.add(lineNow);
					continue;
				}
			}

			WordBean bean = new WordBean(wordLines,lessonNum);
			ret.add(bean);
		}
		return ret;
	}

	private static boolean isStartofAWord(String lineStr) {
		if (lineStr.startsWith("0") || lineStr.startsWith("1")) {
			if (lineStr.substring(3, 4).equals(".")) {
				// 说明是一个词条的开始
				return true;
			}
		}
		return false;
	}

	private static String getThreeNum(int num) {
		String ret = "";
		if (num < 10) {
			ret = "00" + num;
		} else if (num < 100) {
			ret = "0" + num;
		} else {
			ret = "" + num;
		}
		return ret;
	}

	public static LinkedList<WordBean> getAllVocabulories(Context context) {
		LinkedList<WordBean> ret = new LinkedList<WordBean>();
		for (int i = 1; i <= 32; i++) {
			LinkedList<WordBean> vocabulories = getVocabulories(context,i);
			ret.addAll(vocabulories);
		}
		return ret;
	}

}
