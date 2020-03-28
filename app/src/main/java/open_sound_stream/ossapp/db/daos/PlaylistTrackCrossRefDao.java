package open_sound_stream.ossapp.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.PlaylistTrackCrossRef;

@Dao
public interface PlaylistTrackCrossRefDao {
    @Insert
    void insertPlaylistTrackCrossRef(PlaylistTrackCrossRef crossRef);
    @Update
    void updatePlaylistTrackCrossRef(PlaylistTrackCrossRef crossRef);
    @Delete
    void deletePlaylistTrackCrossRef(PlaylistTrackCrossRef crossRef);
}
