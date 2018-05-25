package com.example.nihongo;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nihongo.util.DownloadPool;
import com.example.nihongo.util.FileUtil;
import com.example.nihongo.util.IdThread;
import com.example.nihongo.util.StringUtil;

public class SearchActivity extends Activity {
	EditText mEditText;
	ListView mListView;
	LinkedList<WordBean> allWords;
	MyListViewAdapter mAdapter;
	private Myhandler<SearchActivity> mHandler;// 要不要静态或者final?

	private boolean shouldStop;// 如果onTextChanged会被并行调用的话,不止这个变量,onTextChanged里面的代码都要加上线程保护
	DownloadPool mThreadPool = DownloadPool.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mEditText = (EditText) findViewById(R.id.et_search);
		mListView = (ListView) findViewById(R.id.lv_search_result);
		mHandler = new Myhandler<>(this);
		allWords = FileUtil.getAllVocabulories(this);
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				final String keyWord = s.toString();
				if (keyWord.length() >= 20) {
					Toast.makeText(SearchActivity.this, "输入太多了",
							Toast.LENGTH_SHORT).show();
					return;
				}

				IdThread thread = new IdThread() {
					public void run() {// 线程池,如果有一条就加进去,如果有两条就去除掉较新的一条然后加进去
						if (shouldStop) {
							return;
						}
						LinkedList<WordBean> words = searchWords(keyWord);
						Message msg = Message.obtain();
						msg.obj = words;
						if (shouldStop) {
							return;
						}
						mHandler.sendMessage(msg);
					};
				};
				while (!mThreadPool.isAllThreadSilenced()) {
					shouldStop = true;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					shouldStop = true;// tryStop();
				}// 直到之前的线程都是terminated,其实达到的效果是之前只有一个,而且是停止的
				/**
				 * 其实这里想要更少的卡顿的话就要把上面这个循环判断的工作放到thread中执行,
				 * 目测就是在工作线程中而不是伴随线程,在工作线程中先做这个循环来确保其之前的所有线程都terminated了,
				 * 然后才进行此工作线程的工作
				 */
				shouldStop = false;
				// Log.e("aaaaaaaaaa", "是否isAlive:" + thread.isAlive());
				// Log.e("aaaaaaaaaa", "线程的State:" + thread.getState());
				mThreadPool.execute(thread);

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
			if (shouldStop) {
				return wordsRet;
			}
			WordBean wordCandidate = allWords.get(i);
			float similarity = compare(keyWord, wordCandidate);
			if (similarity > 0.5) {// 符合条件的候选单词装入待返回的list中
				wordCandidate.setSimilarity(similarity);
				wordsRet.add(wordCandidate);
			}
			if (shouldStop) {
				return wordsRet;
			}
		}
		if (shouldStop) {
			return wordsRet;
		}
		Collections.sort(wordsRet);// 按照相似性排序
		return wordsRet;
	}

	private float compare(String keyWord, WordBean wordCandidate) {
		if (shouldStop) {
			return 0;
		}
		float similarity = 0f;
		String jiaming = wordCandidate.getJiaming();
		String yisi = wordCandidate.getFanyi();
		String hanzi = wordCandidate.getHanzi();
		// 对1先处理掉星号hai有音调,对于2,3先检查括号,再处理逗号,3的话还要处理分号
		LinkedList<String> strCandidates = new LinkedList<>();
		strCandidates.add(jiaming);
		if (hanzi != null) {
			String[] split = hanzi.split("[,]");
			for (String string : split) {
				if(string.contains("(")){
					//TODO 处理括号的情况
				}
				strCandidates.add(string.trim());
			}
		}
		if (yisi != null) {
			String[] split1 = yisi.split("[,;]");
			for (String string : split1) {
				if(string.contains("(")){
					//TODO 处理括号的情况
				}
				strCandidates.add(string.trim());
			}
		}

		for (int i = 0; i < strCandidates.size(); i++) {
			if (shouldStop) {
				return 0;
			}
			String candidate = strCandidates.get(i);
			float sim = StringUtil.levenshtein(keyWord.trim(), candidate);
			if (sim > 0.5) {
				similarity = sim;
				break;
			}
		}
		return similarity;
	}

	/**
	 * 要线程保护一下
	 * 
	 * @param words
	 * @param listView
	 */
	public synchronized void initOrRefreshListView(LinkedList<WordBean> words,
			ListView listView) {
		if (mAdapter == null) {
			mAdapter = new MyListViewAdapter(SearchActivity.this, words);
			listView.setAdapter(mAdapter);
		} else {
			mAdapter.resetBeanList(words);
			mAdapter.notifyDataSetChanged();
		}
	}

	private static class Myhandler<T extends SearchActivity> extends Handler {
		private WeakReference<T> mWeakReference;

		public Myhandler(T t) {
			mWeakReference = new WeakReference<T>(t);
		}

		@Override
		public void handleMessage(Message msg) {
			T t = mWeakReference.get();
			if (t != null) {
				LinkedList<WordBean> words = (LinkedList<WordBean>) msg.obj;
				t.initOrRefreshListView(words, t.mListView);
			}
		}
	}

}
