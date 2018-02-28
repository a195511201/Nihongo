package com.example.nihongo;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import com.example.nihongo.util.FileUtil;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView mTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTv = (TextView)findViewById(R.id.tv);
		
		AssetManager am = getResources().getAssets();
		try {
			String[] list = am.list("vocabularies");
			StringBuilder textStr = new StringBuilder();
			for (String string : list) {
				InputStream is = am.open("vocabularies/"+string);
				LinkedList<String> readFromFile = FileUtil.readFromFile(is);
				for (String string2 : readFromFile) {
					textStr.append(string2 + "\n\r");
				}
			}
			mTv.setText(textStr);
		} catch (IOException e) {
			e.printStackTrace();
			mTv.setText(e.getMessage());
		}
		
	}

}
