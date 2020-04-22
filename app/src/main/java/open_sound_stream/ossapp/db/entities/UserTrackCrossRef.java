package open_sound_stream.ossapp.db.entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"userId", "trackId"})
public class UserTrackCrossRef {
    public long userId;
    public long trackId;

    public UserTrackCrossRef(long userId, long trackId) {
        this.userId = userId;
        this.trackId = trackId;
    }
}
