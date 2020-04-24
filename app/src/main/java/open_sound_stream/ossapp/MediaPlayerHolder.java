package open_sound_stream.ossapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.Track;

import static java.sql.Types.NULL;

public final class MediaPlayerHolder implements PlayerAdapter {

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private int mResourceId;
    private PlaybackInfoListener mPlaybackInfoListener;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;

    private final int LOOP_TITLE = 0;
    private final int LOOP_PLAYLIST = 1;
    private final int LOOP_NOTHING = 2;

    private int mLoopMode = LOOP_NOTHING;

    private OSSRepository repo;

    private List<Integer> currentPlaylist = new ArrayList<Integer>();
    private int currentPlaylistPosition = NULL;

    private boolean mPlayAfterTitleChanged = false;

    public MediaPlayerHolder(Context context) {
        mContext = context.getApplicationContext();
        repo = new OSSRepository(context.getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                stopUpdatingCallbackWithPosition(true);
                if (mPlaybackInfoListener != null) {
                    mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                    mPlaybackInfoListener.onPlaybackCompleted();
                }
                if (mLoopMode != LOOP_TITLE) {
                    currentPlaylistPosition++;
                }
                playNextTitle(true);
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void playNextTitle(boolean play) {
        mPlayAfterTitleChanged = play;
        if (currentPlaylist.size() == 0)
        {
            return;
        }
        if (currentPlaylistPosition >= currentPlaylist.size() || currentPlaylistPosition < 0) {
            currentPlaylistPosition = 0;
            if (mLoopMode != LOOP_PLAYLIST) {
                mPlayAfterTitleChanged = false;
            }
        }
        mResourceId = (currentPlaylist.get(currentPlaylistPosition));
        reset();
    }

    public List<Integer> getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void addToCurrentPlaylist(int index, int resourceId) {
        currentPlaylist.add(index, resourceId);
    }

    public void addToCurrentPlaylist(int resourceId) {
        currentPlaylist.add(resourceId);
    }

    public void removeFromCurrentPlaylist(int index) {
        currentPlaylist.remove(index);
    }

    public void resetCurrentPlaylist() {
        currentPlaylist.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializePlayback() {
        loadMedia(currentPlaylist.get(0));
    }

    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        mPlaybackInfoListener = listener;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadMedia(int resourceId) {
        mResourceId = resourceId;

        repo.getTrackById(mResourceId).observeForever(new Observer<Track>() {
            @Override
            public void onChanged(Track track) {
                loadMedia(track.getLocalPath());
            }
        });

        /*AssetFileDescriptor assetFileDescriptor =
                mContext.getResources().openRawResourceFd(mResourceId);
        try {
            mMediaPlayer.setDataSource(assetFileDescriptor);
        } catch (Exception e) {
        }

        try {
            mMediaPlayer.prepare();
        } catch (Exception e) {
        }

        initializeProgressCallback();*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadMedia(String path) {
        initializeMediaPlayer();
        Uri uri = Uri.parse(path);

        try {
            mMediaPlayer.setDataSource(mContext, uri);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            Log.e("ossapp", "exception", e);
        }

        initializeProgressCallback();

        if (mPlayAfterTitleChanged) {
            play();
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PLAYING);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.RESET);
            }
            stopUpdatingCallbackWithPosition(true);
            loadMedia(mResourceId);
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PAUSED);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void skip() {
        currentPlaylistPosition++;
        playNextTitle(isPlaying());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void previous() {
        currentPlaylistPosition--;
        playNextTitle(isPlaying());
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
            mPlaybackInfoListener.onPositionChanged(position);
        }
    }

    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition) {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(0);
            }
        }
    }

    private void updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void initializeProgressCallback() {
        final int duration = mMediaPlayer.getDuration();
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onDurationChanged(duration);
            mPlaybackInfoListener.onPositionChanged(0);
        }
    }

    public int getCurrentPlaybackPosition() {
        return mMediaPlayer.getCurrentPosition();
    }
}
