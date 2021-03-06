package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.Playlist;
import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;
import open_sound_stream.ossapp.db.entities.Track;

@Dao
public interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPlaylist(Playlist playlist);
    @Update
    void updatePlaylist(Playlist playlist);
    @Delete
    void deletePlaylist(Playlist playlist);

    // Return all playlists without the tracks
    @Transaction
    @Query("SELECT * FROM playlist")
    LiveData<List<PlaylistWithTracks>> getPlaylists();

    // Return playlist by name without it's tracks
    @Transaction
    @Query("SELECT * FROM playlist WHERE lower(playlistName) LIKE lower(:name) LIMIT 1")
    LiveData<PlaylistWithTracks> getPlaylistByName(String name);

    // Return playlist by id without tracks
    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistId = :id LIMIT 1")
    LiveData<PlaylistWithTracks> getPlaylistById(long id);

    // Return all playlists with their tracks
    @Transaction
    @Query("SELECT * FROM playlist")
    LiveData<List<PlaylistWithTracks>> getPlaylistsWithTracks();


}
