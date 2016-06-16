package com.newstee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MediaPlayerFragmentActivity extends AppCompatActivity {

	public  final static String ARG_AUDIO_ID = "audio_id";
	public  final static String ARG_AUDIO_PLAY = "audio_play";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment  fragment = MediaPlayerFragment.newInstance(getIntent().getStringExtra(ARG_AUDIO_ID));
			fm.beginTransaction().replace(R.id.fragmentContainer, fragment)
					.commit();
	}
}
