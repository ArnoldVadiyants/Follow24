package com.newstee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class CarModeListActivity extends AppCompatActivity {
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final int TAB_STREAM = 0;
	public static final int TAB_MY_PLAYLIST = 1;
	public static final int TAB_RECENT = 2;
	public static final int TAB_RECOMMEND = 3;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		if (fragment == null) {
			fragment =  CarModeListFragment.newInstance(getIntent().getIntExtra(ARG_SECTION_NUMBER, 0));
			fm.beginTransaction().add(R.id.fragmentContainer, fragment)
					.commit();
		}
	}
}
