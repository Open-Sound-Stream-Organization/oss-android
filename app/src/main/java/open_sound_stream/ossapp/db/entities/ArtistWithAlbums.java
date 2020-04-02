package open_sound_stream.ossapp.db.entities;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ArtistWithAlbums {
    @Embedded
    public Artist artist;
    @Relation(
            parentColumn = "artistId",
            entityColumn = "albumId"
    )
    public List<Album> artistAlbums;
}
