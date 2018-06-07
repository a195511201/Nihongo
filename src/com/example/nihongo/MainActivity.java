package com.example.nihongo;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.nihongo.util.Constance;
import com.example.nihongo.util.DbDAO;
import com.example.nihongo.util.FileUtil;

/**
 * 总行数：2451 lesson1~5行数:81,44,75,65,74, lesson6~10行数:65,75,78,72,73,
 * lesson11~15行数:78,74,58,71,60, lesson16~20行数:66,98,90,112,110,
 * lesson21~25行数:94,106,108,68,84, lesson26~30行数:54,99,54,62,53,
 * lesson31,32行数:81,69.
 * 
 * @author administrator1
 * 
 */
public class MainActivity extends Activity {
	private EasyPickerView mPickerView;
	private Button btn_search;
	private Button btn_jiyibeici;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPickerView = (EasyPickerView) findViewById(R.id.pickerView);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_jiyibeici = (Button) findViewById(R.id.btn_jiyibeici);
		ArrayList<String> dataList = new ArrayList<>();
		for (int i = 1; i <= 32; i++) {
			dataList.add("" + i);
		}
		mPickerView.setDataList(dataList);
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SearchActivity.class);
				startActivity(intent);
			}
		});
		btn_jiyibeici.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						MemoryReciteActivity.class);
				startActivity(intent);
			}
		});
		if (isFirstStarted()) {
			initDataBase();
		}
	}

	private boolean isFirstStarted() {
		SharedPreferences sp = getSharedPreferences("sp_database_record", Context.MODE_PRIVATE);
		boolean isDataBaseInited = sp.getBoolean("isDataBaseInited", false);
		if(isDataBaseInited){
			return false;
		}else{
			return true;
		}
	}

	private void initDataBase() {
		DbDAO dao = DbDAO.getInstance(this);
		LinkedList<WordBean> allVocabulories = FileUtil
				.getAllVocabulories(this);
		for (int i = 0; i < allVocabulories.size(); i++) {
			WordBean bean = allVocabulories.get(i);
			dao.addOrUpdateWord(i, Constance.defaultImportance, bean.getLessonNum(), bean.getNum());
		}
		setDataBaseInited();
	}

	private void setDataBaseInited() {
		SharedPreferences sp = getSharedPreferences("sp_database_record", Context.MODE_PRIVATE);
		sp.edit().putBoolean("isDataBaseInited", true).commit();
	}

	public void onCick(View v) {
		int id = v.getId();
		Intent intent = new Intent(MainActivity.this, SecondActivity.class);
		switch (id) {
		case R.id.btn_choucha:
			intent.putExtra("param", "choucha100");
			startActivity(intent);
			break;
		case R.id.btn_select:
			int selectedLesson = mPickerView.getCurIndex() + 1;
			intent.putExtra("param", "lesson");
			intent.putExtra("lessonNum", selectedLesson);
			startActivity(intent);
		default:
			break;
		}
	}

}
