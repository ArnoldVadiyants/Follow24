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
public class NewsByArgumentActivity extends AppCompatActivity {
    public static final String ARG_SORT_BY_ARGUMENT = "sort_by_argument";
    public static final String ARG_ID = "argumentId";
    public static final String ARG_TITLE = "argumentTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getIntent().getStringExtra(ARG_TITLE);
        if(title !=null)
        {
            setTitle(title);
        }
        else
        {
            setTitle("");
        }
        String argument = getIntent().getStringExtra(ARG_SORT_BY_ARGUMENT);
        setContentView(R.layout.activity_fragment);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.fragmentContainer);
        frameLayout.setPadding(16,16,16,16);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = NewsThreadListFragment.newInstance(argument,Constants.CATEGORY_ALL,getIntent().getStringExtra(ARG_ID));
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


