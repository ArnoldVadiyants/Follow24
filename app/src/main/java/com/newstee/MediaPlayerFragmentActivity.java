package com.newstee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MediaPlayerFragmentActivity extends AppCompatActivity {
	private  final static String TAG = "MPlayerFragmentActivity";
	public  final static String ARG_AUDIO_ID = "audio_id";
	public  final static String ARG_AUDIO_PLAY = "audio_play";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		String id = getIntent().getStringExtra(ARG_AUDIO_ID);
		Log.d(TAG, "@@@@@ id = " + id);
		Fragment  fragment = MediaPlayerFragment.newInstance(id);
			fm.beginTransaction().replace(R.id.fragmentContainer, fragment)
					.commit();
	}
}
