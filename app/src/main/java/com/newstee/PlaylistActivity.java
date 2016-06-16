package com.newstee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

/**
 * Created by Arnold on 07.04.2016.
 */
public class PlaylistActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String title = getString(R.string.playlist_title);
        if(title !=null)
        {
            setTitle(title);
        }
        else
        {
            setTitle("");
        }
        setContentView(R.layout.activity_fragment);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.fragmentContainer);
        frameLayout.setPadding(16,16,16,16);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = NewsThreadListFragment.newInstance(Constants.ARGUMENT_PLAYLIST,Constants.CATEGORY_ALL);
            fm.beginTransaction().replace(R.id.fragmentContainer, fragment)
                    .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }
}


