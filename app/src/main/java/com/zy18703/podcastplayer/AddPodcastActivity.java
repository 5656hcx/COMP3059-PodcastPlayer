package com.zy18703.podcastplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddPodcastActivity extends AppCompatActivity {
    // Activity for user to enter custom podcast URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_podcast);
    }

    public void buttonAction(View view) {
        int id = view.getId();
        if (id == R.id.button_cancel) {
            // User clicks CANCEL or moves focus out of the view
            setResult(RESULT_CANCELED);
            finish();
        }
        else if (id == R.id.button_add) {
            // User click ADD
            EditText editText = findViewById(R.id.edit_add_url);
            Uri uri = Uri.parse(editText.getText().toString().trim());
            // Examine whether it is an HTTP/HTTPS URL
            if (uri != null && uri.getScheme() != null &&
                    (uri.getScheme().toLowerCase().equals("http") ||
                            uri.getScheme().toLowerCase().equals("https"))) {
                Intent result = new Intent(this, MainActivity.class);
                result.setData(uri);
                setResult(RESULT_OK, result);
                finish();
            } else
                Toast.makeText(this, R.string.toast_invalid_url, Toast.LENGTH_SHORT).show();
        }
    }
}
