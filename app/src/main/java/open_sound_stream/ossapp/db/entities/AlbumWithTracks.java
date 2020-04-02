package open_sound_stream.ossapp.db.entities;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class AlbumWithTracks {
    @Embedded
    public Album album;
    @Relation(
            parentColumn = "albumId",
            entityColumn = "inAlbumId"
    )
    public List<Track> albumTracks;
}
