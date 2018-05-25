package com.example.nihongo;

import java.util.LinkedList;

public class WordBean implements Comparable<WordBean> {
	public String jiaming;
	public String hanzi;
	public String fanyi;
	public int num;
	public int lessonNum;
	
	private float similarity;
	
	public WordBean(LinkedList<String> wordLines,int lessonNum){
		if(wordLines == null || wordLines.size() ==0){
			throw new RuntimeException("wordLines is wrong");
		}
		this.lessonNum = lessonNum;
		num = Integer.parseInt(wordLines.get(0).substring(0,3));
		if(wordLines.size() == 3){
			jiaming = wordLines.get(0).substring(4);
			hanzi = wordLines.get(1);
			fanyi = wordLines.get(2);
		}else if(wordLines.size() == 2){
			jiaming = wordLines.get(0).substring(4);
			fanyi = wordLines.get(1);
		}else if(wordLines.size() == 1){
			jiaming = wordLines.get(0).substring(4);
		}
	}

	public String getJiaming() {
		return jiaming;
	}

	public void setJiaming(String jiaming) {
		this.jiaming = jiaming;
	}

	public String getHanzi() {
		return hanzi;
	}

	public void setHanzi(String hanzi) {
		this.hanzi = hanzi;
	}

	public String getFanyi() {
		return fanyi;
	}

	public void setFanyi(String fanyi) {
		this.fanyi = fanyi;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getLessonNum() {
		return lessonNum;
	}

	public void setLessonNum(int lessonNum) {
		this.lessonNum = lessonNum;
	}
	
	public float getSimilarity() {
		return similarity;
	}

	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}

	@Override
	public int compareTo(WordBean another) {
		// jdk1.7要求相等时候返回0不然可能报错java.lang.IllegalArgumentException: Comparison
		// method violates its general contract!
		if (Math.abs(this.similarity - another.similarity) <= 1e-3) {
			return 0;
		}
		if (this.similarity > another.similarity) {
			return -1;
		} else {
			return 1;
		}
	}
	
	
}
