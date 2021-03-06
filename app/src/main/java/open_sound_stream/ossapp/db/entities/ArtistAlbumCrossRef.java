package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;

@Entity(primaryKeys = {"artistId", "albumId"}, indices = {@Index("albumId")})
public class ArtistAlbumCrossRef implements Serializable {
    @ColumnInfo(name = "artistId")
    public long artistId;
    @ColumnInfo(name = "albumId")
    public long albumId;

    public ArtistAlbumCrossRef(long artistId, long albumId) {
        this.artistId = artistId;
        this.albumId = albumId;
    }
}
