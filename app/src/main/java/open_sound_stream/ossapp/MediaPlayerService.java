package open_sound_stream.ossapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class MediaPlayerService extends IntentService {

    private final IBinder mBinder = new LocalBinder();

    private SeekBar mSeekbarAudio;
    public PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking;
    private MediaSessionCompat mMediaSession;
    private static final String CHANNEL_ID = "media_playback_channel";

    private static final String MUSIC_PLAY = "PLAY";
    private static final String MUSIC_PAUSE = "PAUSE";
    private static final String MUSIC_NEXT = "NEXT";
    private static final String MUSIC_PREV = "PREV";

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        setupMediaSession();
        buildNotification(false);
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public MediaPlayerService() {
        super("MediaPlayerService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        handleIntent(workIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupMediaSession();
        buildNotification(false);
        return super.onStartCommand(intent, flags, startId);
    }

    public void initializeUI(ImageButton mPlayPauseButton, ImageButton mPrevButton, ImageButton mNextButton, SeekBar seekBar) {
        mSeekbarAudio = seekBar;

        mPlayPauseButton.setOnClickListener(
                view -> {
                    if (mPlayerAdapter.isPlaying()) {
                        mPlayerAdapter.pause();
                    }
                    else {
                        mPlayerAdapter.play();
                    }
                });

        mPrevButton.setOnClickListener(
                view -> {
                    if (mPlayerAdapter.getCurrentPlaybackPosition() <= 5000)
                    {
                        mPlayerAdapter.previous();
                    } else {
                        mPlayerAdapter.seekTo(0);
                    }
                });

        mNextButton.setOnClickListener(
                view -> {
                    mPlayerAdapter.skip();
                }
        );

        initializeSeekbar();
        initializePlaybackController();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaSession.release();
        return super.onUnbind(intent);
    }

    private void setupMediaSession() {
        mMediaSession = new MediaSessionCompat(getApplicationContext(), "OSSApp");
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
           public void onPlay() {
               mPlayerAdapter.play();
               buildNotification(true);
           }

           @RequiresApi(api = Build.VERSION_CODES.O)
           @Override
           public void onPause() {
               mPlayerAdapter.pause();
               buildNotification(false);
           }

           @Override
           public void onSkipToNext() {
               mPlayerAdapter.skip();
           }

           @Override
           public void onSkipToPrevious() {
               mPlayerAdapter.previous();
           }

           @Override
           public void onSeekTo(long pos) {
               mPlayerAdapter.seekTo((int) pos);
           }
        });

        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS |
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification(boolean play) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.icons8_circled_play_48);
        builder.setContentTitle("Title");
        builder.setContentText("Artist - Album");
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mMediaSession.getSessionToken()));
        builder.addAction(createAction(R.drawable.icons8_circled_play_48, "Previous", MUSIC_PREV));
        if(!play) {
            builder.addAction(createAction(R.drawable.icons8_circled_play_48, "Play", MUSIC_PLAY));
        } else {
            builder.addAction(createAction(R.drawable.icons8_circled_play_48, "Pause", MUSIC_PAUSE));
        }
        builder.addAction(createAction(R.drawable.icons8_circled_play_48, "Next", MUSIC_NEXT));

        Notification notification = builder.build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = "Media Playback";
        String description = "Media playback controls";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        manager.createNotificationChannel(channel);
        manager.notify(1, notification);
    }

    private NotificationCompat.Action createAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    private void handleIntent (Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case MUSIC_PLAY:
                mMediaSession.getController().getTransportControls().play();
                Toast.makeText(getApplicationContext(), "play", Toast.LENGTH_SHORT).show();
                break;
            case MUSIC_PAUSE:
                mMediaSession.getController().getTransportControls().pause();
                Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
                break;
            case MUSIC_NEXT:
                mMediaSession.getController().getTransportControls().skipToNext();
                Toast.makeText(getApplicationContext(), "next", Toast.LENGTH_SHORT).show();
                break;
            case MUSIC_PREV:
                mMediaSession.getController().getTransportControls().skipToPrevious();
                Toast.makeText(getApplicationContext(), "prev", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void initializeUI(ImageButton mPlayButton, ImageButton mPauseButton, ImageButton mPrevButton, ImageButton mNextButton, SeekBar seekBar) {
        mSeekbarAudio = seekBar;

        mPlayButton.setOnClickListener(
                view -> {
                    mPlayerAdapter.play();
                }
        );

        mPauseButton.setOnClickListener(
                view -> {
                    mPlayerAdapter.pause();
                }
        );

        mPrevButton.setOnClickListener(
                view -> {
                    if (mPlayerAdapter.getCurrentPlaybackPosition() <= 5000) {
                        mPlayerAdapter.previous();
                    } else {
                        mPlayerAdapter.seekTo(0);
                    }
                }
        );

        mNextButton.setOnClickListener(
                view -> {
                    mPlayerAdapter.skip();
                }
        );

        initializeSeekbar();
        initializePlaybackController();
    }

    public void initializePlayback() {
        mPlayerAdapter.initializePlayback();
    }

    public List<Integer> getCurrentPlaylist() {
        return mPlayerAdapter.getCurrentPlaylist();
    }

    public void addToCurrentPlaylist(int resourceId) {
        mPlayerAdapter.addToCurrentPlaylist(resourceId);
    }

    public void addToCurrentPlaylist(int index, int resourceId) {
        mPlayerAdapter.addToCurrentPlaylist(index, resourceId);
    }

    public void removeFromCurrentPlaylist(int index) {
        mPlayerAdapter.removeFromCurrentPlaylist(index);
    }

    public void loadPlaylist(List<Integer> playlist) {
        for (int id: playlist) {
            mPlayerAdapter.addToCurrentPlaylist(id);
        }
    }

    public void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(this);
        mMediaPlayerHolder.setPlaybackInfoListener(new MediaPlayerService.PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
    }

    public void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            mSeekbarAudio.setMax(duration);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onPositionChanged(int position) {
            if (!mUserIsSeeking) {
                mSeekbarAudio.setProgress(position, true);
            }
        }

        @Override
        public void onStateChanged(@State int state) {
            PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
            switch(state) {
                case State.COMPLETED:
                    builder.setState(PlaybackStateCompat.STATE_STOPPED, 0, 1);
                    break;
                case State.PAUSED:
                    builder.setState(PlaybackStateCompat.STATE_PAUSED, mPlayerAdapter.getCurrentPlaybackPosition(), 1);
                    break;
                case State.PLAYING:
                    builder.setState(PlaybackStateCompat.STATE_PLAYING, mPlayerAdapter.getCurrentPlaybackPosition(), 1);
                    break;
                default:
                    builder.setState(PlaybackStateCompat.STATE_NONE, 0, 1);
                    break;
            }
            mMediaSession.setPlaybackState(builder.build());
        }

        @Override
        public void onPlaybackCompleted() {
        }
    }

    public void release() {
        mPlayerAdapter.release();
    }

    public boolean isPlaying() {
        return mPlayerAdapter.isPlaying();
    }
}
