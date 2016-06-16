package com.newstee;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.newstee.helper.NewsTeeInstructionsDialogFragment;
import com.newstee.helper.SessionManager;
import com.newstee.model.data.UserLab;
import com.newstee.utils.MPUtilities;

public class CarModeFragmentActivity extends AppCompatActivity implements   SeekBar.OnSeekBarChangeListener{
	private static  final String TAG = "CarModeFragmentActivity";
	private FrameLayout mProgressLayout;
	private ImageView headerTitle ;
	private LinearLayout headerPlayer;
	private MPUtilities utils = new MPUtilities();
	private TextView headerPlayerTitleCanal;
	private TextView headerPlayerTitleNews ;
	private ImageButton headerPlayerPlayButton ;
	private SeekBar headerPlayerDuringSeekBar;
	private int newSongValue = -1;
	private boolean mpPlayingValue = false;
	//private ImageLoader imageLoader = ImageLoader.getInstance();
	private Handler mHandler = new Handler();
	private MusicService musicSrv;
	private Intent playIntent;
	private boolean musicBound = false;
	public void showDialog() {
		if(!isFirstTime())
		{
			return;
		}

		FragmentManager fm = getSupportFragmentManager();
		NewsTeeInstructionsDialogFragment dialog = NewsTeeInstructionsDialogFragment.newInstance(R.drawable.tab_image_car_mode,getResources().getString(R.string.tab_car_mode),getResources().getString(R.string.instructions_car_mode),false);
		dialog.show(fm,NewsTeeInstructionsDialogFragment.DIALOG_INSTRUCTIONS);

	}
	private boolean isFirstTime()
	{
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		boolean ranBefore = preferences.getBoolean("RanBeforeCarMode", false);
		if (!ranBefore) {
			// first time
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("RanBeforeCarMode", true);
			editor.commit();
		}
		return !ranBefore;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
showDialog();
		setContentView(R.layout.car_mode_layout);

		View header = findViewById(R.id.header);
		 headerTitle =(ImageView) header.findViewById(R.id.car_mode_header_title);

		 headerPlayer = (LinearLayout)header.findViewById(R.id.car_mode_header_player);
		 headerPlayerTitleCanal = (TextView)header.findViewById(R.id.car_mode_header_title_canal);
		headerPlayerTitleCanal.setOnClickListener(mediaPlayerClickListener);
		 headerPlayerTitleNews = (TextView)header.findViewById(R.id.car_mode_header_title_news);
		headerPlayerTitleNews.setOnClickListener(mediaPlayerClickListener);
		 headerPlayerPlayButton = (ImageButton)header.findViewById(R.id.car_mode_header_play);
		headerPlayerDuringSeekBar  = (SeekBar)header.findViewById(R.id.car_mode_header_during_seekBar);
		mProgressLayout = (FrameLayout)findViewById(R.id.car_mode_progress);
		FrameLayout streamTab = (FrameLayout)findViewById(R.id.car_mode_tab_stream);
		FrameLayout playlistTab = (FrameLayout)findViewById(R.id.car_mode_tab_playlist);
		FrameLayout recentTab = (FrameLayout)findViewById(R.id.car_mode_tab_recent);
		FrameLayout recommendTab = (FrameLayout) findViewById(R.id.car_mode_tab_recommend);
		Button exitTab = ( Button) findViewById(R.id.car_mode_tab_exit);
		headerPlayerPlayButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check for already playing
				if (musicBound) {
					if (musicSrv != null) {
						if (musicSrv.isPlaying()) {

							musicSrv.pausePlayer();
							headerPlayerPlayButton.setImageResource(R.drawable.ic_media_play_car_mode);
						} else {
							// Resume song
							musicSrv.go();
							// Changing button image to pause button
							headerPlayerPlayButton.setImageResource(R.drawable.ic_media_pause_car_mode);

						}
					}
				}


			}
		});
		int color;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			color = getResources().getColor(android.R.color.white, null);
		}
		else {
			color = getResources().getColor(android.R.color.white);
		}
		headerPlayerDuringSeekBar.getProgressDrawable().setColorFilter(
				color, PorterDuff.Mode.SRC_IN);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			headerPlayerDuringSeekBar.getThumb().mutate().setAlpha(0);
		}
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mpDuring.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } */

		headerPlayerDuringSeekBar.setOnSeekBarChangeListener(this);
		headerPlayer.setVisibility(View.GONE);
		headerTitle.setVisibility(View.VISIBLE);

		streamTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = (new Intent(CarModeFragmentActivity.this, CarModeListActivity.class));
				i.putExtra(CarModeListActivity.ARG_SECTION_NUMBER, CarModeListActivity.TAB_STREAM);
				startActivity(i);
			}
		});
		playlistTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = (new Intent(CarModeFragmentActivity.this, CarModeListActivity.class));
				i.putExtra(CarModeListActivity.ARG_SECTION_NUMBER, CarModeListActivity.TAB_MY_PLAYLIST);
				startActivity(i);
			}
		});
		recentTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = (new Intent(CarModeFragmentActivity.this, CarModeListActivity.class));
				i.putExtra(CarModeListActivity.ARG_SECTION_NUMBER, CarModeListActivity.TAB_RECENT);
				startActivity(i);
			}
		});
		recommendTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = (new Intent(CarModeFragmentActivity.this, CarModeListActivity.class));
				i.putExtra(CarModeListActivity.ARG_SECTION_NUMBER, CarModeListActivity.TAB_RECOMMEND);
				startActivity(i);
			}
		});
		exitTab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SessionManager(getApplicationContext()).setCarMode(false);
				startActivity(new Intent(CarModeFragmentActivity.this, MainActivity.class));
				finish();
			}
		});
		new SessionManager(getApplicationContext()).setCarMode(true);
		if(UserLab.getInstance().isUpdated())
		{
			showContentData();
		}
		else {
			new LoadAsyncTask(this) {
				@Override
				void hideContent() {
					mProgressLayout.setVisibility(View.VISIBLE);
				}

				@Override
				void showContent() {
					showContentData();
				}
			}.execute();
		}


}
	public void showContentData()
	{
		mProgressLayout.setVisibility(View.GONE);

	}
	private View.OnClickListener mediaPlayerClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(CarModeFragmentActivity.this, MediaPlayerFragmentActivity.class);
			startActivity(i);
		}
	};
	@Override
	public void onStart() {
		super.onStart();
		connectService();
	}


	private ServiceConnection musicConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "******onServiceConnected*****");
			MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
			//get service
			musicSrv = binder.getService();
			//pass catalogue
			musicBound = true;
			if(musicSrv == null)
			{
				return;
			}
			if(musicSrv.isPlaying())
			{
				headerPlayer.setVisibility(View.VISIBLE);
				headerTitle.setVisibility(View.GONE);
			}
			//   updateMPNews();
		}

		/*
            @Override
            protected void onStart() {
                super.onSt
                connectService();
            }*/
		@Override
		public void onServiceDisconnected(ComponentName name) {
			musicBound = false;
		}
	};
	public void connectService() {
		if (playIntent == null) {
			playIntent = new Intent(this, MusicService.class);
			bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			startService(playIntent);
		}

	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = musicSrv.getDur();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

		// forward or backward to certain seconds
		musicSrv.seek(currentPosition);

		// updateFragment timer progress again
		updateMediaPlayer();
	}
	public void updateMediaPlayer() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if(musicSrv != null) {
				if (!musicSrv.isNullPlayer()) {
					if (!musicSrv.isPaused()) {

						long totalDuration = musicSrv.getDur();
						long currentDuration = musicSrv.getPosn();
						if (newSongValue != musicSrv.getNewSongValue()) {
							headerPlayerTitleNews.setText(musicSrv.getSongTitle());
							headerPlayerTitleCanal.setText(musicSrv.getCanalTitle());
						//	imageLoader.displayImage(musicSrv.getNewsPictureUrl(),mpPicture, DisplayImageLoaderOptions.getInstance());
							newSongValue = musicSrv.getNewSongValue();
						}
						if (mpPlayingValue != musicSrv.isPlaying()) {
							if (mpPlayingValue) {
								headerPlayerPlayButton.setImageResource(R.drawable.ic_media_play_car_mode);
							} else {
								headerPlayerPlayButton.setImageResource(R.drawable.ic_media_pause_car_mode);

							}
							mpPlayingValue = !mpPlayingValue;
						}

						// Updating progress bar
						int progress = (utils.getProgressPercentage(currentDuration, totalDuration));
						//Log.d("Progress", ""+progress);
						Log.d(TAG, "Progress " + progress + " bufferedProgress "+musicSrv.getBufferPosition());
						headerPlayerDuringSeekBar.setProgress(progress);
						headerPlayerDuringSeekBar.setSecondaryProgress(musicSrv.getBufferPosition());

					}
				}
			}

			mHandler.postDelayed(this, 100);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		headerTitle.setVisibility(View.VISIBLE);
		headerPlayer.setVisibility(View.GONE);
		if(musicBound)
		{
			if(musicSrv != null)
			{
				if(musicSrv.isPlaying())
				{
					headerPlayer.setVisibility(View.VISIBLE);
					headerTitle.setVisibility(View.GONE);
				}
			}
		}

		updateMediaPlayer();
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(musicConnection);
	}
}
