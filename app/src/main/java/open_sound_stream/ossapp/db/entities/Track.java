package open_sound_stream.ossapp.db.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "track")
public class Track {
    @ColumnInfo(name = "trackId")
    @PrimaryKey()
    private long trackId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "localPath")
    private String localPath;
    @ColumnInfo(name = "inAlbumId")
    private long inAlbumId;
    @ColumnInfo(name = "artistId")
    private long artistId;
    @ColumnInfo(name = "serverUri")
    private String serverUri;

    public Track(String title, String localPath) {
        this.title = title;
        this.localPath = localPath;
    }

    @Ignore
    public Track(long trackId, String title) {
        this.trackId = trackId;
        this.title = title;
    }

    @Override
    public String toString(){
        return this.title;
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

    public long getInAlbumId() {
        return inAlbumId;
    }

    public void setInAlbumId(long inAlbumId) {
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

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }
}