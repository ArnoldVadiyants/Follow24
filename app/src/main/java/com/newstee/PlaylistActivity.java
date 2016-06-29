package com.newstee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

/**
 * Created by Arnold on 07.04.2016.
 */
public class PlaylistActivity extends AppCompatActivity {
    public static final String DATA_EXTRA_EXIT = "exit";
    private static final int MENU_ITEM_EXIT_ID = 999;
    public static final int REQUEST_CODE = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String title = PlayList.getInstance().getListTitle();
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
            case MENU_ITEM_EXIT_ID:
                Intent intent = new Intent();
                intent.putExtra(DATA_EXTRA_EXIT,true);
                setResult(RESULT_OK, intent);
                finish();
                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_EXIT_ID, Menu.NONE, getString(R.string.btn_exit)).setIcon(R.drawable.exit_image_white).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
}


