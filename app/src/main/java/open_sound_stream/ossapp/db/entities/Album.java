package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "album")
public class Album {
    @PrimaryKey(autoGenerate = true)
    private int albumId;
    @ColumnInfo(name = "albumName")
    private String albumName;

    // Constructor
    public Album(String albumName)
    {
        this.albumName = albumName;
    }

    // Getter and Setter needed for private columns
    public int getAlbumId() {
        return albumId;
    }
    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

}
