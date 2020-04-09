package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"artistId", "albumId"})
public class ArtistAlbumCrossRef {
    @ColumnInfo(name = "artistId")
    public long artistId;
    @ColumnInfo(name = "albumId")
    public long albumId;

    public ArtistAlbumCrossRef(long artistId, long albumId) {
        this.artistId = artistId;
        this.albumId = albumId;
    }
}
