package open_sound_stream.ossapp.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import open_sound_stream.ossapp.db.daos.AlbumDao;
import open_sound_stream.ossapp.db.daos.ArtistDao;
import open_sound_stream.ossapp.db.daos.PlaylistDao;
import open_sound_stream.ossapp.db.daos.PlaylistTrackCrossRefDao;
import open_sound_stream.ossapp.db.daos.TrackDao;
import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.Playlist;
import open_sound_stream.ossapp.db.entities.PlaylistTrackCrossRef;
import open_sound_stream.ossapp.db.entities.Track;

@Database(entities = {Playlist.class, Track.class, PlaylistTrackCrossRef.class, Album.class, Artist.class}, exportSchema = false, version = 8)
public abstract class OSSDatabase extends RoomDatabase {
    private static final String DB_NAME = "OSS_DB";
    private static OSSDatabase instance;

    public static synchronized OSSDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), OSSDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    // Declare DAOs here
    public abstract PlaylistDao playlistDao();
    public abstract PlaylistTrackCrossRefDao playlistTrackCrossRefDao();
    public abstract TrackDao trackDao();
    public abstract AlbumDao albumDao();
    public abstract ArtistDao artistDao();
}
