package com.example.nihongo;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nihongo.util.FileUtil;

public class SecondActivity extends Activity {
	ListView mListView;
	TextView mTitleTV;
	TextView mBottomTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		Intent intent = getIntent();
		String param = intent.getStringExtra("param");
		mListView = (ListView) findViewById(R.id.lv);
		mTitleTV = (TextView) findViewById(R.id.tv_title);
		mBottomTV = (TextView) findViewById(R.id.tv_end);

		switch (param) {
		case "choucha100"://抽查100词
			LinkedList<WordBean> chouXuanWords = getChouXuanWords(100);
			showWords(chouXuanWords, mListView,"随机100词");
			break;
		case "lesson"://选定某一课
			int lessonNum = intent.getIntExtra("lessonNum", 1);
			LinkedList<WordBean> vocabulories = FileUtil.getVocabulories(this,
					lessonNum);
			showWords(vocabulories, mListView,"lesson"+lessonNum);
		}
	}

	private void showWords(LinkedList<WordBean> words, ListView listView, String title) {
		mTitleTV.setText("========"+title+"========" + "\n\n");
		mBottomTV.setText("\n========fin"+words.size()+"========");
		MyListViewAdapter adapter = new MyListViewAdapter(SecondActivity.this, words);
		listView.setAdapter(adapter);
	}
	
//	private void showWordsWithNum(LinkedList<WordBean> words, TextView tv, String title) {
//		StringBuilder sBuilder = new StringBuilder();
//		sBuilder.append("========"+title+"========" + "\n\n");
//		for (int i = 0; i < words.size(); i++) {
//			WordBean bean = words.get(i);
//			sBuilder.append((i+1)+"."+bean.jiaming + "\n");
//		}
//		sBuilder.append("\n========fin"+words.size()+"========");
//		mTV.setText(sBuilder.toString());
//	}
	
	private static String getThreeNum(int num){
		String ret = "";
		if(num<10){
			ret = "00"+num;
		}else if(num <100){
			ret = "0"+num;
		}else{
			ret = ""+num;
		}
		return ret;
	}

	@Deprecated
	private void testRandom(int size) {
		int[] intArray = new int[size];
		for (int i : intArray) {// 初始化数组
			i = 0;
		}
		for (int i = 0; i < 1000000; i++) {
			int position = (int) (Math.random() * size);
			intArray[position] = intArray[position] + 1;
		}
		for (int i = 0; i < 100; i++) {
			Log.d("aaa", "位置" + i + "的次数为：" + intArray[i]);
		}

	}

	private LinkedList<WordBean> getChouXuanWords(int amount) {
		LinkedList<WordBean> allVocabulories = FileUtil
				.getAllVocabulories(this);
		int size = allVocabulories.size();
		LinkedList<WordBean> ret = new LinkedList<WordBean>();
		LinkedList<Integer> positionList = new LinkedList<Integer>();
		for (int i = 0; i < amount; i++) {
			int position = (int) (Math.random() * size);
			while(isIntInList(positionList,position)){//有重复的，position重选
				position = (int) (Math.random() * size);
			}
			positionList.add(position);
			ret.add(allVocabulories.get(position));
		}
		return ret;
	}

	private boolean isIntInList(LinkedList<Integer> positionList, int position) {
		if(positionList == null || positionList.size() == 0){
			return false;
		}
		boolean ret = false;
		for (Integer integer : positionList) {
			if(integer.equals(position)){//只要碰到有一样的就是有一样的
				ret = true;
			}
		}
		return ret;
	}

}
