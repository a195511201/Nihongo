package com.example.nihongo;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nihongo.util.Constance;
import com.example.nihongo.util.DbDAO;
import com.example.nihongo.util.FileUtil;

public class MemoryReciteActivity extends Activity {
	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory_recite);
		mListView = (ListView) findViewById(R.id.lv_memory);
		LinkedList<WordBean> beanList = method(100);
		if (beanList.size() == 0) {
			// 说明全背完了,这时候应该显示背完了
			Toast.makeText(this, "背完了", Toast.LENGTH_SHORT).show();
		} else {
			MyListViewAdapter_memory adapter = new MyListViewAdapter_memory(
					this, beanList);
			mListView.setAdapter(adapter);
		}
	}

	private LinkedList<WordBean> method(int amount) {
		LinkedList<WordBean> allVocabulories = FileUtil
				.getAllVocabulories(this);
		int size = allVocabulories.size();
		LinkedList<WordBean> tmpList = new LinkedList<WordBean>();
		for (int i = 0; i < size; i++) {
			int importance = getImportance(i);
			for (int j = 0; j < importance; j++) {// 根据其重要程度进行不同概率的装填
				tmpList.add(allVocabulories.get(i));
			}
		}
		LinkedList<WordBean> ret = new LinkedList<WordBean>();
		if (tmpList.size() == 0) {
			return ret;// 说明都背完了
		}

		for (int i = 0; i < amount; i++) {
			if (tmpList.size() == 0) {
				return ret;
			}
			int position = (int) (Math.random() * tmpList.size());
			WordBean beanToAdd = tmpList.get(position);
			ret.add(beanToAdd);
			// 去掉tmpList中的所有是这个单词的节点
			while (tmpList.remove(beanToAdd)) {
				if (Constance.isDebug) {
					Log.e("aaaaaaaaaa", beanToAdd + "被移除");
				}
			}
		}
		return ret;
	}

	private LinkedList<WordBean> getChouXuanWords(int amount) {
		LinkedList<WordBean> allVocabulories = FileUtil
				.getAllVocabulories(this);
		int size = allVocabulories.size();
		LinkedList<WordBean> ret = new LinkedList<WordBean>();
		LinkedList<Integer> positionList = new LinkedList<Integer>();
		for (int i = 0; i < amount; i++) {
			int position = (int) (Math.random() * size);
			while (isIntInList(positionList, position)) {// 有重复的，position重选
				position = (int) (Math.random() * size);
			}
			positionList.add(position);
			ret.add(allVocabulories.get(position));
		}
		return ret;
	}

	/**
	 * 
	 * @param wordNum也是fileId
	 * @return 越大越需要背诵
	 */
	private int getImportance(int wordNum) {
		DbDAO dao = DbDAO.getInstance(this);
		return dao.queryImportance(wordNum);
	}

	private boolean isIntInList(LinkedList<Integer> positionList, int position) {
		if (positionList == null || positionList.size() == 0) {
			return false;
		}
		boolean ret = false;
		for (Integer integer : positionList) {
			if (integer.equals(position)) {// 只要碰到有一样的就是有一样的
				ret = true;
			}
		}
		return ret;
	}

}
