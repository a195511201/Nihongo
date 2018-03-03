package com.example.nihongo;

import java.util.LinkedList;

public class WordBean {
	public String jiaming;
	public String hanzi;
	public String fanyi;
	public int num;
	public int lessonNum;
	
	public WordBean(LinkedList<String> wordLines,int lessonNum){
		if(wordLines == null || wordLines.size() ==0){
			throw new RuntimeException("wordLines is wrong");
		}
		this.lessonNum = lessonNum;
		if(wordLines.size() == 3){
			jiaming = wordLines.get(0).substring(4);
			hanzi = wordLines.get(1);
			fanyi = wordLines.get(2);
		}else if(wordLines.size() == 2){
			//TODO 解决2行的情况
		}else if(wordLines.size() == 1){
			jiaming = wordLines.get(0).substring(4);
			num = Integer.parseInt(wordLines.get(0).substring(0,3));
		}
	}
}
