package com.zy18703.podcastplayer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class CommentsActivity extends AppCompatActivity {
    // Read comments from SQLite database

    private SQLiteDatabase database;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolBarComm);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        // Retrieve rows that belong to current podcast file (using hash value)
        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        cursor = database.query(DBHelper.TABLE_NAME,
                new String [] { "_id", DBHelper.COL_SECOND }, DBHelper.COL_FIRST + "=?",
                new String[] { String.valueOf(getIntent().getIntExtra("data", 0)) },
                null, null, null);
        // If there is at least one comment
        if (cursor.moveToLast()) {
            findViewById(R.id.textView_empty).setVisibility(View.INVISIBLE);
            ListView listView = findViewById(R.id.listView);
            // Using simpleCursorAdapter to fill the listView with database scursor
            listView.setAdapter(new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor, new String[] { DBHelper.COL_SECOND },
                    new int[] { android.R.id.text1}, 0));
            // Set long press operation: Copy comment to clipboard
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                    cursor.moveToPosition(pos);
                    ClipboardManager clipboard = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText("comment", cursor.getString(1)));
                    Toast.makeText(CommentsActivity.this,
                            R.string.toast_copied, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        cursor.close();
        database.close();
        super.onDestroy();
    }
}
