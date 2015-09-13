package org.worshipsongs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.domain.Song;
import org.worshipsongs.domain.Verse;
import org.worshipsongs.parser.VerseParser;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Seenivasan
 * version 1.0.0
 */
public class ServiceSongListActivity extends AppCompatActivity
{
    private ListView songListView;
    private VerseParser verseparser;
    private String songTitle;
    private SongDao songDao;
    private ArrayList<String> titles;
    private List<Verse> verseList;
    private ArrayAdapter<String> adapter;
    private String[] dataArray;
    private Song song;
    String serviceName;
    private Context context = WorshipSongApplication.getContext();
    private File serviceFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        Intent intent = getIntent();
        serviceName = intent.getStringExtra("serviceName");
        //System.out.println("Selected Service:" + serviceName);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(serviceName);


        songListView = (ListView) findViewById(R.id.song_list_view);
        songDao = new SongDao(this);
        verseparser = new VerseParser();
        loadSongs();

        final Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        songListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
            {
                vibrator.vibrate(15);
                songTitle = songListView.getItemAtPosition(position).toString();
                System.out.println("Selected title:" + songTitle);
                LayoutInflater li = LayoutInflater.from(ServiceSongListActivity.this);
                View promptsView = li.inflate(R.layout.service_delete_dialog, null);
                TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
                deleteMsg.setText("Do you want to delete the song?");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServiceSongListActivity.this);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        serviceFile = PropertyUtils.getPropertyFile(ServiceSongListActivity.this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
                        removeSong();
                        loadSongs();
                        Toast.makeText(ServiceSongListActivity.this, "Song " + songTitle + " Deleted...!", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });


        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                String selectedValue = songListView.getItemAtPosition(position).toString();
                Intent intent = new Intent(ServiceSongListActivity.this, SongContentViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, titles);
                bundle.putInt(CommonConstants.POSITION_KEY, position);
                intent.putExtras(bundle);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }

    private void loadSongs()
    {
        File serviceFile = PropertyUtils.getPropertyFile(this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
        String property = PropertyUtils.getProperty(serviceName, serviceFile);
        String propertyValues[] = property.split(";");
        titles = new ArrayList<>();
        for (String title : propertyValues) {
            titles.add(title);
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, propertyValues);
        songListView.setAdapter(adapter);
    }

    private void removeSong()
    {
        try {
            String propertyValue = "";
            System.out.println("Preparing to remove service:" + songTitle);
            File serviceFile = PropertyUtils.getPropertyFile(this, CommonConstants.SERVICE_PROPERTY_TEMP_FILENAME);
            String property = PropertyUtils.getProperty(serviceName, serviceFile);
            String propertyValues[] = property.split(";");
            System.out.println("File:" + serviceFile.getAbsolutePath());
            for (int i = 0; i < propertyValues.length; i++) {
                System.out.println("Property length: " + propertyValues.length);
                Log.i(this.getClass().getSimpleName(), "Property value  " + propertyValues[i]);
                if (StringUtils.isNotBlank(propertyValues[i]) && !propertyValues[i].equalsIgnoreCase(songTitle)) {
                    Log.i(this.getClass().getSimpleName(), "Append property value" + propertyValues[i]);
                    propertyValue = propertyValue + propertyValues[i] + ";";
                }
            }
            Log.i(this.getClass().getSimpleName(), "Property value after removed  " + propertyValue);
            PropertyUtils.setProperty(serviceName, propertyValue, serviceFile);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Error occurred while parsing verse", e);
        }
    }


    private List<Verse> getVerse(String lyrics)
    {
        return verseparser.parseVerseDom(this, lyrics);
    }

    private List<String> getVerseByVerseOrder(String verseOrder)
    {
        String split[] = verseOrder.split("\\s+");
        List<String> verses = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            verses.add(split[i].toLowerCase());
        }
        return verses;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
