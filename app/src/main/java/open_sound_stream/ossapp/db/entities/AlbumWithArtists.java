package open_sound_stream.ossapp.db.entities;

import java.io.Serializable;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class AlbumWithArtists implements Serializable {
    @Embedded
    public Album album;
    @Relation(
            parentColumn = "albumId",
            entityColumn = "artistId",
            associateBy = @Junction(ArtistAlbumCrossRef.class)
    )
    public List<Artist> artists;
}
