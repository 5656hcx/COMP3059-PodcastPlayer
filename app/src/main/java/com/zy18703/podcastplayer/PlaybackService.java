package com.zy18703.podcastplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class PlaybackService extends Service implements MediaPlayer.OnCompletionListener {
    // Background playback service
    // This service holds a playlist and a podcast player
    // Will go through all podcasts in the playlist

    public final static int NOTIFICATION_ID = 0;
    public final static String CHANNEL_ID = "Playback";
    private final PlayerBinder binder = new PlayerBinder();
    private final PodcastPlayer player = new PodcastPlayer();
    private ArrayList<String> playList;
    private static int currentSong = -1;

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Will be call when previous playback completes
        playNext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // will be call by every startService()

        if (playList == null) {
            // if playlist has not been initialized
            playList = intent.getStringArrayListExtra("playlist");
            if (!playList.isEmpty()) {
                // load, pause and send notification
                currentSong = 0;
                player.load(playList.get(currentSong), this);
                player.pause();
                sendNotification(false);
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        // delete all existing notification when service destroyed
        player.stop();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // delete all existing notification when user eliminate the application process
        stopSelf();
    }

    private boolean playNext() {
        player.stop();
        if (playList != null && currentSong < playList.size()-1) {
            // if there is song in playlist to play
            player.load(playList.get(++currentSong), this);
            sendNotification(true);
            return true;
        }
        return false;
    }

    private boolean playPrev() {
        player.stop();
        if (currentSong > 0 && playList != null) {
            // if current song is not the first song
            player.load(playList.get(--currentSong), this);
            sendNotification(true);
            return true;
        }
        return false;
    }

    private boolean add(String source) {
        // add a song to playlist and play this song
        // do nothing if the song is already in the playlist
        if (playList.indexOf(source) < 0) {
            currentSong = 0;
            playList.add(currentSong, source);
            player.stop();
            player.load(source, this);
            sendNotification(true);
            return true;
        }
        return false;
    }

    private void sendNotification(boolean onGoing) {
        // build and send a notification
        // when user click the notification system will start MainActivity using pending intent
        // onGoing flag specifies whether the notification can be removed by user
        String info = " (" + (currentSong + 1) + "/" + playList.size() + ")";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.baseline_notify_podcast_24)
                        .setContentTitle(player.getTitle() + info)
                        .setContentText(player.getFilePath())
                        .setOngoing(onGoing)
                        .setContentIntent(PendingIntent.getActivity(this, 0,
                                new Intent(this, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    class PlayerBinder extends Binder {
        String getUrl() { return player.getFilePath(); }
        String getTitle() { return player.getTitle(); }
        int getProgress() { return player.getProgress(); }
        int getDuration() { return player.getDuration(); }
        PodcastPlayer.StreamingPlayerState getState() { return player.getState(); }
        boolean playNext() { return PlaybackService.this.playNext(); }
        boolean playPrev() { return PlaybackService.this.playPrev(); }
        boolean add(String source) { return PlaybackService.this.add(source); }
        ArrayList<String> getPlaylist() { return playList; }
        void stop() { player.stop(); }
        void setProgress(int progress) { player.setProgress(progress); }

        void play() {
            // resume playback or pause playback
            switch (player.getState()) {
                case PLAYING:
                    player.pause();
                    sendNotification(false);
                    break;
                case PAUSED:
                    player.play();
                    sendNotification(true);
                    break;
                case ERROR:
                case STOPPED:
                    if (playList != null) {
                        player.load(playList.get(currentSong), PlaybackService.this);
                        sendNotification(true);
                    }
                    break;
            }
        }

        void play(int index) {
            // play specific podcast in the playlist
            if (index != -1 && index != currentSong) {
                currentSong = index;
                player.stop();
                player.load(playList.get(currentSong), PlaybackService.this);
                sendNotification(true);
            }
        }
    }

}