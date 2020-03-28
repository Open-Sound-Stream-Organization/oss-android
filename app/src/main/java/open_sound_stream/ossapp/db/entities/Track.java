package open_sound_stream.ossapp.db.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "track")
public class Track {
    @PrimaryKey(autoGenerate = true)
    private int trackId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "localPath")
    private String localPath;

    public Track(String title, String localPath) {
        this.title = title;
        this.localPath = localPath;
    }

    // Setter
    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    // Getter
    public int getTrackId() {
        return trackId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocalPath() {
        return localPath;
    }
}