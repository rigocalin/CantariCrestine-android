package org.worshipsongs.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.adapter.NavDrawerListAdapter;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.NavDrawerItem;
import org.worshipsongs.domain.Song;
import org.worshipsongs.fragment.AboutWebViewFragment;
import org.worshipsongs.fragment.AuthorListFragment;
import org.worshipsongs.fragment.ServiceListFragment;
import org.worshipsongs.fragment.SettingsPreferenceFragment;
import org.worshipsongs.fragment.SongBookListFragment;
import org.worshipsongs.fragment.SongsListFragment;
import org.worshipsongs.worship.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity
{
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private SongDao songDao;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Context context = WorshipSongApplication.getContext();
    // nav drawer title
    private CharSequence drawerTitle;
    private List<Song> songList;
    // used to store app title
    private CharSequence appTitle;

    // slide menu items
    private String[] navigationMenuTitles;
    private TypedArray navigationMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter navDrawerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        appTitle = drawerTitle = getTitle();
        songDao = new SongDao(this);
        songDao.open();
        songList = songDao.findTitles();
        // load slide menu items
        navigationMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        // nav drawer icons from resources
        navigationMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.list_slidermenu);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        // Songs
        navDrawerItems.add(new NavDrawerItem(navigationMenuTitles[0], navigationMenuIcons.getResourceId(0, -1), true, Integer.toString(songList.size())));
        // Authors
        navDrawerItems.add(new NavDrawerItem(navigationMenuTitles[1], navigationMenuIcons.getResourceId(1, -1)));
        // Song books
        navDrawerItems.add(new NavDrawerItem(navigationMenuTitles[2], navigationMenuIcons.getResourceId(2, -1)));
        // Services
        navDrawerItems.add(new NavDrawerItem(navigationMenuTitles[3], navigationMenuIcons.getResourceId(3, -1)));
        // Settings
        navDrawerItems.add(new NavDrawerItem(navigationMenuTitles[4], navigationMenuIcons.getResourceId(4, -1)));
        // About
        navDrawerItems.add(new NavDrawerItem(navigationMenuTitles[5], navigationMenuIcons.getResourceId(5, -1)));
        // Recycle the typed array
        navigationMenuIcons.recycle();
        drawerListView.setOnItemClickListener(new SlideMenuClickListener());
        // setting the nav drawer list adapter
        navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        drawerListView.setAdapter(navDrawerListAdapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer,
                R.string.app_name, R.string.app_name)
        {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(appTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(drawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        if (savedInstanceState == null) {
            displaySelectedFragment(0);
        }
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int fragmentPosition, long id)
        {
            displaySelectedFragment(fragmentPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // toggle nav drawer on selecting action bar app icon/title
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
//		case R.id.action_settings:
//			return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerListView);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void displaySelectedFragment(int fragmentPosition)
    {
        Fragment fragment = null;
        PreferenceFragment preferenceFragment = null;
        switch (fragmentPosition) {
            case 0:
                fragment = new SongsListFragment();
                break;
            case 1:
                fragment = new AuthorListFragment();
                break;
            case 2:
                fragment = new SongBookListFragment();
                break;
            case 3:
                fragment = new ServiceListFragment();
                break;
            case 4:
                preferenceFragment = new SettingsPreferenceFragment();
                break;
            case 5:
                fragment = new AboutWebViewFragment();
                break;
            default:
                break;
        }

        if (fragment != null || preferenceFragment != null) {
            if (preferenceFragment != null) {
                getFragmentManager().beginTransaction().replace(R.id.frame_container, new SettingsPreferenceFragment()).commit();
            } else {
                getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
            // update selected item and title, then close the drawer
            drawerListView.setItemChecked(fragmentPosition, true);
            drawerListView.setSelection(fragmentPosition);
            setTitle(navigationMenuTitles[fragmentPosition]);
            drawerLayout.closeDrawer(drawerListView);
        } else {
            Log.w(this.getClass().getSimpleName(), "No fragment are selected");
        }
    }

    @Override
    public void setTitle(CharSequence title)
    {
        appTitle = title;
        getActionBar().setTitle(appTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

}
