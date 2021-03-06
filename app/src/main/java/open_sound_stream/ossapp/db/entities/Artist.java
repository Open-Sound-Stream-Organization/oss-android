package open_sound_stream.ossapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "artist")
public class Artist implements Serializable {
    @ColumnInfo(name = "artistId")
    @PrimaryKey()
    private long artistId;
    @ColumnInfo(name = "artistName")
    private String artistName;

    public Artist(String artistName) {
        this.artistName = artistName;
    }

    @Ignore
    public Artist(long artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
    }

    // Getter and Setter
    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    @Override
    public String toString(){
        return  this.artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
