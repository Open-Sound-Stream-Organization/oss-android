package open_sound_stream.ossapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;

import java.util.List;

public class MediaPlayerService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private SeekBar mSeekbarAudio;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking;

    public class LocalBinder extends Binder {
        MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void initializeUI(Button mPlayPauseButton, Button mPrevButton, Button mNextButton, SeekBar seekBar) {
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

    public void initializeUI(Button mPlayButton, Button mPauseButton, Button mPrevButton, Button mNextButton, SeekBar seekBar) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadPlaylist(List<Integer> playlist) {
        playlist.forEach(id -> mPlayerAdapter.addToCurrentPlaylist(id));
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
