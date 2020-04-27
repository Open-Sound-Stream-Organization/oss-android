package open_sound_stream.ossapp;

import java.util.List;

/**
 * Allows {@link MainActivity} to control media playback of {@link MediaPlayerHolder}.
 */
public interface PlayerAdapter {

    void loadMedia(int resourceId);

    List<Integer> getCurrentPlaylist();

    void addToCurrentPlaylist(int index, int resourceId);

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

    int getCurrentPlaylistPosition();
}
