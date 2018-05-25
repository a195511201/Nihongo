package com.example.nihongo;

import java.util.Collections;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.example.nihongo.util.FileUtil;
import com.example.nihongo.util.StringUtil;

public class SearchActivity extends Activity {
	EditText mEditText;
	ListView mListView;
	LinkedList<WordBean> allWords;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mEditText = (EditText) findViewById(R.id.et_search);
		mListView = (ListView) findViewById(R.id.lv_search_result);
		allWords = FileUtil.getAllVocabulories(this);
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String keyWord = s.toString();
				LinkedList<WordBean> words = searchWords(keyWord);
				refreshListView(words, mListView);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	/**
	 * 注意,这个可能是一个耗时的操作
	 */
	private LinkedList<WordBean> searchWords(String keyWord) {
		LinkedList<WordBean> wordsRet = new LinkedList<>();
		
		for (int i = 0; i < allWords.size(); i++) {
			WordBean wordCandidate = allWords.get(i);
			float similarity = compare(keyWord,wordCandidate);
			if (similarity > 0.5) {// 符合条件的候选单词装入待返回的list中
				wordCandidate.setSimilarity(similarity);
				wordsRet.add(wordCandidate);
			}
		}
		Collections.sort(wordsRet);//按照相似性排序
		return wordsRet;
	}
	
	private float compare(String keyWord,WordBean wordCandidate){
		float similarity = 0f;
		String jiaming = wordCandidate.getJiaming();
		String yisi = wordCandidate.getFanyi();
		String hanzi = wordCandidate.getHanzi();
		//对1先处理掉星号hai有音调,对于2,3先检查括号,再处理逗号,3的话还要处理分号
		LinkedList<String> strCandidates = new LinkedList<>();
		strCandidates.add(jiaming);
		if(hanzi != null){
			String[] split = hanzi.split("[,]");
			for (String string : split) {
				strCandidates.add(string.trim());
			}
		}
		if(yisi != null){
			String[] split1 = yisi.split("[,;]");
			for (String string : split1) {
				strCandidates.add(string.trim());
			}
		}
		
		for(int i = 0;i<strCandidates.size();i++){
			String candidate = strCandidates.get(i);
			float sim = StringUtil.levenshtein(keyWord.trim(), candidate);
			if(sim > 0.5){
				similarity = sim;
				break;
			}
		}
		return similarity;
	}

	private void refreshListView(LinkedList<WordBean> words, ListView listView) {
//		if (words != null && words.size() > 0) {
//			// TODO 处理这种情况
//		}

		MyListViewAdapter adapter = new MyListViewAdapter(SearchActivity.this,
				words);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

}
