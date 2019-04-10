package com.zy18703.podcastplayer;

import android.media.MediaPlayer;
import android.net.Uri;

import com.example.ryd.podcast.StreamingPlayer;

import java.io.File;

class PodcastPlayer extends StreamingPlayer {
    // Extended player of provided StreamingPlayer

    void load(String filePath, MediaPlayer.OnCompletionListener listener) {
        // Specify listener to call when one playback completes
        // Help to play next song automatically
        super.load(filePath);
        mediaPlayer.setOnCompletionListener(listener);
    }

    String getTitle() {
        if (getFilePath() == null)
            return null;
        Uri uri = Uri.parse(getFilePath());
        String title = uri.getQuery();
        return title == null ? Uri.parse(getFilePath()).getLastPathSegment() : title;
    }

    int getDuration() {
        // Return duration of current podcast
        if (mediaPlayer != null && getState() != StreamingPlayerState.ERROR) {
            try {
                return mediaPlayer.getDuration();
            } catch (IllegalStateException e) { e.printStackTrace(); }
        }
        return -1;
    }

    void setProgress(int progress) {
        // Seek progress of current playback to specified value
        if (progress >= 0 && progress <= getDuration()) {
            try {
                mediaPlayer.seekTo(progress);
            } catch (IllegalStateException e) { e.printStackTrace(); }
        }
    }

    void setFilePath(String path) {
        if (path != null && !path.isEmpty()) {
            filePath = path;
        }
    }

}
