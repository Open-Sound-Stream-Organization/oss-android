package open_sound_stream.ossapp.db.entities;

import java.io.Serializable;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class ArtistWithAlbums implements Serializable {
    @Embedded
    public Artist artist;
    @Relation(
            parentColumn = "artistId",
            entityColumn = "albumId",
            associateBy = @Junction(ArtistAlbumCrossRef.class)
    )
    public List<Album> artistAlbums;

    @Override
    public String toString(){
        return this.artist.getArtistName();
    }
}
