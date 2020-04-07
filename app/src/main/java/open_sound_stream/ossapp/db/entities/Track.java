package open_sound_stream.ossapp.db.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "track")
public class Track {
    @ColumnInfo(name = "trackId")
    @PrimaryKey(autoGenerate = true)
    private long trackId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "localPath")
    private String localPath;
    @ColumnInfo(name = "inAlbumId")
    private int inAlbumId;

    public Track(String title, String localPath) {
        this.title = title;
        this.localPath = localPath;
    }

    @Ignore
    public Track(long trackId, String title, String localPath) {
        this.trackId = trackId;
        this.localPath = localPath;
        this.title = title;
    }

    // Getter and Setter
    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getInAlbumId() {
        return inAlbumId;
    }

    public void setInAlbumId(int inAlbumId) {
        this.inAlbumId = inAlbumId;
    }

    public long getTrackId() {
        return trackId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocalPath() {
        return localPath;
    }
}