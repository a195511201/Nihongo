package com.example.nihongo.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;

import android.util.Log;

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

}
