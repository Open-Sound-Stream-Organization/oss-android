package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(primaryKeys = {"userId", "trackId"}, indices = {@Index("trackId")})
public class UserTrackCrossRef {
    public long userId;
    @ColumnInfo(name = "trackId")
    public long trackId;

    public UserTrackCrossRef(long userId, long trackId) {
        this.userId = userId;
        this.trackId = trackId;
    }
}
