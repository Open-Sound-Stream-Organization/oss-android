package open_sound_stream.ossapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
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
    private MediaControllerCompat mController;
    private uiCallback mCallback;
    private NotificationManager mManager;
    private NotificationChannel mChannel;
    private Notification mNotification;
    private static final String CHANNEL_ID = "media_playback_channel";

    private String currentTitle = "";
    private String currentArtist = "";
    private String currentAlbum = "";
    private String currentAlbumPath = "";

    private static final String MUSIC_PLAY = "PLAY";
    private static final String MUSIC_PAUSE = "PAUSE";
    private static final String MUSIC_NEXT = "NEXT";
    private static final String MUSIC_PREV = "PREV";

    //Loop nothing as default
    private int loopMode = 2;

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        setupMediaSession();
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = "Media Playback";
        String description = "Media playback controls";
        int importance = NotificationManager.IMPORTANCE_LOW;
        mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        mChannel.setDescription(description);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mManager.createNotificationChannel(mChannel);
        buildNotification(false);
        startForeground(1, mNotification);
        super.onCreate();
    }

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

    public void setCallback(uiCallback callback) {
        mCallback = callback;
    }

    public void initializeUI(ImageButton mPlayPauseButton, ImageButton mPrevButton, ImageButton mNextButton,
                             SeekBar seekBar, ImageButton toggleRepeat, ImageButton shuffle) {
        mSeekbarAudio = seekBar;

        mPlayPauseButton.setOnClickListener(
                view -> {
                    if (mPlayerAdapter.isPlaying()) {
                        mMediaSession.getController().getTransportControls().pause();
                    }
                    else {
                        mMediaSession.getController().getTransportControls().play();
                    }
                });
        mPrevButton.setOnClickListener(
                view -> {
                    mMediaSession.getController().getTransportControls().skipToPrevious();
                });
        mNextButton.setOnClickListener(
                view -> {
                    mMediaSession.getController().getTransportControls().skipToNext();
                });
        toggleRepeat.setOnClickListener(
                view -> {
                    if (loopMode >= 2) {
                        loopMode = 0;
                    } else {
                        loopMode++;
                    }
                    mPlayerAdapter.setLoopMode(loopMode);
                    switch (loopMode) {
                        case 0:
                            toggleRepeat.setImageResource(R.drawable.repeat_one_36);
                            break;
                        case 1:
                            toggleRepeat.setImageResource(R.drawable.repeat_36);
                            break;
                        default:
                            toggleRepeat.setImageResource(R.drawable.repeat_grey_36);
                            break;
                    }
                });
        shuffle.setOnClickListener(
                view -> {
                    mPlayerAdapter.shuffle();
                });

        initializeSeekbar();
        initializePlaybackController();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayerAdapter.release();
        mMediaSession.release();
        stopForeground(true);
        mManager.cancelAll();
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
        mMediaSession.setActive(true);
        mController = new MediaControllerCompat(getApplicationContext(), mMediaSession);
        mController.registerCallback(new MediaControllerCompat.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onMetadataChanged (MediaMetadataCompat metadata) {
                if (mController.getMetadata() != null) {
                    currentTitle = mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE);
                    currentArtist = mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                    currentAlbum = mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
                    currentAlbumPath = mController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
                }
                if(mCallback != null) {
                    mCallback.updateUI();
                }
                if (mPlayerAdapter != null && !mPlayerAdapter.isPlaying()) {
                    buildNotification(false);
                } else {
                    buildNotification(true);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                if(mCallback != null) {
                    mCallback.updateUI();
                }
                if (mPlayerAdapter != null && !mPlayerAdapter.isPlaying()) {
                    buildNotification(false);
                } else {
                    buildNotification(true);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification(boolean playing) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.icons8_circled_play_48);
        builder.setContentTitle(currentTitle);
        builder.setContentText(currentArtist + " - " + currentAlbum);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mMediaSession.getSessionToken()));
        builder.addAction(createAction(R.drawable.baseline_skip_previous_white_48, "Previous", MUSIC_PREV));
        if(!playing) {
            builder.addAction(createAction(R.drawable.baseline_play_arrow_white_48, "Play", MUSIC_PLAY));
        } else {
            builder.addAction(createAction(R.drawable.baseline_pause_white_48, "Pause", MUSIC_PAUSE));
            builder.setOngoing(true);
        }
        builder.addAction(createAction(R.drawable.baseline_skip_next_white_48, "Next", MUSIC_NEXT));

        mNotification = builder.build();

        mManager.notify(1, mNotification);
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

    public void initializePlayback() {
        mPlayerAdapter.setMediaSession(mMediaSession);
        mPlayerAdapter.initializePlayback();
        buildNotification(false);
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

    public void resetCurrentPlaylist() {
        mPlayerAdapter.resetCurrentPlaylist();
    }

    public void loadPlaylist(List<Integer> playlist) {
        for (int id: playlist) {
            mPlayerAdapter.addToCurrentPlaylist(id);
        }
    }

    public void shuffle() {
        mPlayerAdapter.shuffle();
    }

    public void setLoopMode(int mode) {
        mPlayerAdapter.setLoopMode(mode);
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public String getCurrentArtist() {
        return currentArtist;
    }

    public String getCurrentAlbum() {
        return currentAlbum;
    }

    public String getCurrentAlbumPath() {
        return currentAlbumPath;
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

    public interface uiCallback {
        void updateUI();
    }
}
