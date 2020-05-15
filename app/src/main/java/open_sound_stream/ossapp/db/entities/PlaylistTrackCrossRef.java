package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;


@Entity(primaryKeys = {"playlistId", "trackId"}, indices = {@Index("trackId")})
public class PlaylistTrackCrossRef implements Serializable {
    @ColumnInfo(name = "playlistId")
    private long playlistId;
    @ColumnInfo(name = "trackId")
    private long trackId;

    // Constructor
    public PlaylistTrackCrossRef(long playlistId, long trackId) {
        this.playlistId = playlistId;
        this.trackId = trackId;
    }

    // Getter and Setter
    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long pId) {
        this.playlistId = pId;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long tId) {
        this.trackId = tId;
    }
}
