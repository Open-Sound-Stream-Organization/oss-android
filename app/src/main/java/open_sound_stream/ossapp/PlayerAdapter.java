package open_sound_stream.ossapp;

/**
 * Allows {@link MainActivity} to control media playback of {@link MediaPlayerHolder}.
 */
public interface PlayerAdapter {

    void loadMedia(int resourceId);

    void addToCurrentPlaylist(int resourceId);

    void removeFromCurrentPlaylist(int index);

    void resetCurrentPlaylist();

    void initializePlayback();

    void release();

    boolean isPlaying();

    void play();

    void reset();

    void pause();

    void skip();

    void previous();

    void initializeProgressCallback();

    void seekTo(int position);

    int getCurrentPlaybackPosition();
}
