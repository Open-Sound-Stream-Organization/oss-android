package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "album")
public class Album {
    @ColumnInfo(name = "albumId")
    @PrimaryKey()
    private long albumId;
    @ColumnInfo(name = "albumName")
    private String albumName;

    // Constructor
    public Album(String albumName)
    {
        this.albumName = albumName;
    }

    @Ignore
    public Album(long albumId, String albumName) {
        this.albumId = albumId;
        this.albumName = albumName;
    }

    // Getter and Setter needed for private columns
    public long getAlbumId() {
        return albumId;
    }
    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

}
