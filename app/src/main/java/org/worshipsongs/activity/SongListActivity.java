package org.worshipsongs.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.fragment.SongsListFragment;
import org.worshipsongs.service.PresentationScreenService;
import org.worshipsongs.worship.R;

/**
 * author: Seenivasan, Madasamy
 * version: 2.1.0
 */
public class SongListActivity extends AbstractActivity
{
    private PresentationScreenService presentationScreenService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        initSetUp();
        setListView();
        setFragment();
    }

    private void initSetUp()
    {
        presentationScreenService = new PresentationScreenService(this);
        setActionBar();
    }

    private void setActionBar()
    {
        String title = getIntent().getStringExtra(CommonConstants.TITLE_KEY);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
    }

    private void setListView()
    {
        ListView songListView = (ListView) findViewById(R.id.song_list_view);
        songListView.setVisibility(View.GONE);
    }

    private void setFragment()
    {
        FragmentActivity fragmentActivity = (FragmentActivity) this;
        Intent intent = getIntent();
        String type = intent.getStringExtra(CommonConstants.TYPE);
        int id = intent.getIntExtra(CommonConstants.ID, 0);
        SongsListFragment songsListFragment = SongsListFragment.newInstance(type, id);
        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.song_list_fragment, songsListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        presentationScreenService.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        presentationScreenService.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        presentationScreenService.onStop();
    }

}
