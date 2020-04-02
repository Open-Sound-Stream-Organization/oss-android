package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.Playlist;
import open_sound_stream.ossapp.db.entities.PlaylistWithTracks;

@Dao
public interface PlaylistDao {
    @Insert
    void insertPlaylist(Playlist playlist);
    @Update
    void updatePlaylist(Playlist playlist);
    @Delete
    void deletePlaylist(Playlist playlist);

    // Return All Playlists with all their tracks
    @Transaction
    @Query("SELECT * FROM playlist")
    List<PlaylistWithTracks> getPlaylistsWithTracks();

    // Return the playlist with its tracks with the specified id
    @Transaction
    @Query("SELECT * FROM playlist WHERE playlistId = :id")
    PlaylistWithTracks getPlaylistById(int id);

}
