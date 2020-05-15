package open_sound_stream.ossapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import open_sound_stream.ossapp.db.OSSRepository;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;
import open_sound_stream.ossapp.db.entities.ArtistWithAlbums;
import open_sound_stream.ossapp.db.entities.Track;
import open_sound_stream.ossapp.network.NetworkHandler;

import static java.sql.Types.NULL;

public final class MediaPlayerHolder implements PlayerAdapter {

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private int mResourceId;
    private MediaSessionCompat mMediaSession;
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
    private long currentAlbumId;
    private String currentAlbumName = "No album";
    private long currentArtistId;
    private String currentArtistName = "No artist";
    private String currentTrackTitle = "No title";

    private boolean mPlayAfterTitleChanged = false;

    public MediaPlayerHolder(Context context) {
        mContext = context.getApplicationContext();
        repo = new OSSRepository(context.getApplicationContext());
    }

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
        reset();
    }

    //Shuffles queue and keeps current track as first element
    public void shuffle() {
        if (currentPlaylist.size() > 0) {
            int currentTrackId = currentPlaylist.get(currentPlaylistPosition);
            currentPlaylist.remove(currentPlaylistPosition);
            Collections.shuffle(currentPlaylist);
            currentPlaylist.add(0, currentTrackId);
        }
    }

    public void setLoopMode(int mode) {
        if (mode < 0 || mode > 2) {
            return;
        }
        mLoopMode = mode;
    }
  
    public void initializePlayback() {
        loadMedia(currentPlaylist.get(0));
    }

    public void setMediaSession(MediaSessionCompat mediaSession) {
        mMediaSession = mediaSession;
    }

    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        mPlaybackInfoListener = listener;
    }


    public void loadMedia(int resourceId) {
        mResourceId = resourceId;

        repo.getTrackById(mResourceId).observeForever(new Observer<Track>() {
            @Override
            public void onChanged(Track track) {
                if(track != null) {
                    loadMedia(repo.getTrackFilePath(track.getTrackId()));
                    currentAlbumId = track.getInAlbumId();
                    currentArtistId = track.getArtistId();
                    currentTrackTitle = track.getTitle();
                    repo.getAlbumById(currentAlbumId).observeForever(new Observer<AlbumWithTracks>() {
                        @Override
                        public void onChanged(AlbumWithTracks albumWithTracks) {
                            if (albumWithTracks != null) {
                                currentAlbumName = albumWithTracks.album.getAlbumName();
                                repo.getArtistById(currentArtistId).observeForever(new Observer<ArtistWithAlbums>() {
                                    @Override
                                    public void onChanged(ArtistWithAlbums artistWithAlbums) {
                                        if (artistWithAlbums != null) {
                                            currentArtistName = artistWithAlbums.artist.getArtistName();
                                        }
                                        setMetadata();
                                    }
                                });
                            } else {
                                setMetadata();
                            }
                        }
                    });
                } else {
                    setMetadata();
                }
            }
        });
    }

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

    private void setMetadata() {
        NetworkHandler nh = new NetworkHandler(mContext);
        String coverPath = nh.getCoverFilePath(currentAlbumId);

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentAlbumName);
        if (coverPath != null) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, coverPath);
        } else {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "");
        }
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentArtistName);
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentTrackTitle);
        MediaMetadataCompat meta = builder.build();
        mMediaSession.setMetadata(meta);

        //Reset values
        currentTrackTitle = "No title";
        currentArtistName = "No artist";
        currentAlbumName = "No album";
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

    public void skip() {
        currentPlaylistPosition++;
        playNextTitle(isPlaying());
    }

    public void previous() {
        if (getCurrentPlaybackPosition() <= 5000) {
            currentPlaylistPosition--;
            playNextTitle(isPlaying());
        } else {
            seekTo(0);
        }
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

    public int getCurrentPlaylistPosition() { return currentPlaylistPosition; }

}
