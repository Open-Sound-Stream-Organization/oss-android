package open_sound_stream.ossapp.db.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"playlistId", "trackId"})
public class PlaylistTrackCrossRef {
    public int playlistId;
    public int trackId;

    // Getter and Setter
    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }
}
