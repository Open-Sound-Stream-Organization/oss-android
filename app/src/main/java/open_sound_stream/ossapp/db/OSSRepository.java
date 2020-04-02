package open_sound_stream.ossapp.db;

import android.app.Application;

import androidx.room.Room;
import open_sound_stream.ossapp.db.daos.AlbumDao;
import open_sound_stream.ossapp.db.daos.ArtistDao;
import open_sound_stream.ossapp.db.daos.PlaylistDao;
import open_sound_stream.ossapp.db.daos.PlaylistTrackCrossRefDao;
import open_sound_stream.ossapp.db.daos.TrackDao;

public class OSSRepository {
    // Member
    private PlaylistDao playlistDao;
    private TrackDao trackDao;
    private AlbumDao albumDao;
    private ArtistDao artistDao;
    private PlaylistTrackCrossRefDao playlistTrackCrossRefDao;

    // Constructor. Initializes the Repository with DAOs
    public OSSRepository(Application application) {
        OSSDatabase db = OSSDatabase.getInstance(application);
        playlistDao = db.playlistDao();
        trackDao = db.trackDao();
        albumDao = db.albumDao();
        artistDao = db.artistDao();
        playlistTrackCrossRefDao = db.playlistTrackCrossRefDao();
    }



}
