package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.PlaylistTrackCrossRef;

@Dao
public interface PlaylistTrackCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlaylistTrackCrossRef(PlaylistTrackCrossRef crossRef);
    @Update
    void updatePlaylistTrackCrossRef(PlaylistTrackCrossRef crossRef);
    @Delete
    void deletePlaylistTrackCrossRef(PlaylistTrackCrossRef crossRef);

    @Query("SELECT * FROM playlisttrackcrossref")
    LiveData<List<PlaylistTrackCrossRef>> getPlaylistTrackCrossRefs();

    @Query("SELECT * FROM playlisttrackcrossref WHERE playlistId = :playlistId")
    LiveData<PlaylistTrackCrossRef> getPlaylistTrackCrossRefById(long playlistId);
}
