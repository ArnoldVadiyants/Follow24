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
private String newsId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		if(savedInstanceState !=null)
		{
			newsId = savedInstanceState.getString(ARG_AUDIO_ID);
		}
		else
		{
			newsId = getIntent().getStringExtra(ARG_AUDIO_ID);
		}
		Log.d(TAG, "@@@@@ id = " + newsId);
		createFragment();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

			super.onSaveInstanceState(outState);
			outState.putString(ARG_AUDIO_ID, PlayList.getInstance().getCurrent().getId());
			Log.d(TAG, "onSaveInstanceState");

	}
/*	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		createFragment();
		Log.d(TAG, "onRestoreInstanceState");
	}*/
	public void createFragment()
	{
		FragmentManager fm = getSupportFragmentManager();
		Fragment  fragment = MediaPlayerFragment.newInstance(newsId);
		fm.beginTransaction().replace(R.id.fragmentContainer, fragment)
				.commit();
	}

}
