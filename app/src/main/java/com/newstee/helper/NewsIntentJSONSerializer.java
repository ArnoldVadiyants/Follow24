package com.newstee.helper;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class NewsIntentJSONSerializer {
	private Context mContext;
	private String mFilename;

	public NewsIntentJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}
	

	public JSONArray loadLkedNewsId () throws IOException, JSONException {
	
		BufferedReader reader = null;
		JSONArray array = new JSONArray();
		try {
		// �������� � ������ ����� � StringBuilder
		InputStream in = mContext.openFileInput(mFilename);
		reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder jsonString = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
		// Line breaks are omitted and irrelevant
		jsonString.append(line);
		}
		// ������ JSON � �������������� JSONTokener
					array = (JSONArray) new JSONTokener(jsonString.toString())
		.nextValue();
		
			} catch (FileNotFoundException e) {
			// ���������� ��� ������ "� ����"; �� ��������� ��������
			} finally {
			if (reader != null)
			reader.close();
			}
			return array;
			}
	public void saveLkedNewsId(JSONArray array)
			throws JSONException, IOException {
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename,
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}

}