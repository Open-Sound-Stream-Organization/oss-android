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
import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.AlbumWithArtists;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;

@Dao
public interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbum(Album album);
    @Update
    void updateAlbum(Album album);
    @Delete
    void deleteAlbum(Album album);

    // Return all Albums with their Tracks
    @Transaction
    @Query("SELECT * FROM album")
    LiveData<List<AlbumWithTracks>> getAlbumsWithTracks();

    // Return the album with the specified ID and it's Tracks
    @Transaction
    @Query("SELECT * FROM album WHERE albumId = :id LIMIT 1")
    LiveData<AlbumWithTracks> getAlbumById(long id);

    @Transaction
    @Query("SELECT * FROM album WHERE albumName = :albumName LIMIT 1")
    LiveData<AlbumWithArtists> getAlbumWithArtistsByName(String albumName);

    @Transaction
    @Query("SELECT * FROM album WHERE albumId = :id LIMIT 1")
    LiveData<AlbumWithArtists> getAlbumWithArtistsById(long id);

    // Return the album from the given name
    @Transaction
    @Query("SELECT * FROM album WHERE albumName = :albumName LIMIT 1")
    LiveData<AlbumWithTracks> getAlbumByName(String albumName);
}
