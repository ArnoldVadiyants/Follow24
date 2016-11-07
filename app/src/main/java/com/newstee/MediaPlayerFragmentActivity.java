package com.newstee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.newstee.model.data.News;

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
		createFragment(newsId);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

			super.onSaveInstanceState(outState);
		News n = PlayList.getInstance().getCurrent();
		if(n == null)
		{
			Log.e(TAG, "@@@@ current news = null");
			return;
		}
			outState.putString(ARG_AUDIO_ID, n.getId());
			Log.d(TAG, "onSaveInstanceState");

	}
/*	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		createFragment();
		Log.d(TAG, "onRestoreInstanceState");
	}*/
	public void createFragment(String newsId)
	{
		FragmentManager fm = getSupportFragmentManager();
		Fragment  fragment = MediaPlayerFragment.newInstance(newsId);
		fm.beginTransaction().replace(R.id.fragmentContainer, fragment)
				.commit();
	}

}
