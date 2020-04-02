package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "artist")
public class Artist {
    @PrimaryKey(autoGenerate = true)
    private int artistId;
    @ColumnInfo(name = "artistname")
    private String artistName;

    // Getter and Setter
    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
