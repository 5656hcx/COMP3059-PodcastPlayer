package com.zy18703.podcastplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Toolbar toolbar = findViewById(R.id.toolBarPlaylist);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        final ArrayList<String> playlist = getIntent().getStringArrayListExtra("playlist");
        if (playlist != null && !playlist.isEmpty()) {
            // Fill the listView with current playlist
            ListView listView = findViewById(R.id.playlistView);
            ArrayList<String> list = new ArrayList<>();
            int index = 1;
            for (String url: playlist) {
                Uri uri = Uri.parse(url);
                String title = uri.getQuery();
                if (title == null || title.isEmpty()) {
                    title = uri.getLastPathSegment();
                    if (title == null || title.isEmpty())
                        title = getString(R.string.text_unknown_source);
                }
                list.add(index + ".\t" + title);
                index++;
            }
            listView.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, list));
            // Set click operation: play the selected podcast immediately
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent result = new Intent(PlaylistActivity.this, MainActivity.class);
                    result.putExtra("data", position);
                    setResult(RESULT_OK, result);
                    finish();
                }
            });
            // Set long press operation: Play the podcast in other application
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(playlist.get(pos)), "audio/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Setup back button in toolbar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
