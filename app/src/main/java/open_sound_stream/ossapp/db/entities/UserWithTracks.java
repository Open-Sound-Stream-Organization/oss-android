package open_sound_stream.ossapp.db.entities;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class UserWithTracks {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "userId",
            entityColumn = "trackId",
            associateBy = @Junction(UserTrackCrossRef.class)
    )
    public List<Track> trackList;
}
