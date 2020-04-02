package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.Album;
import open_sound_stream.ossapp.db.entities.AlbumWithTracks;

@Dao
public interface AlbumDao {
    @Insert
    void insertAlbum(Album album);
    @Update
    void updateAlbum(Album album);
    @Delete
    void deleteAlbum(Album album);

    // Return all Albums with their Tracks
    @Transaction
    @Query("SELECT * FROM album")
    List<AlbumWithTracks> getAlbumsWithTracks();

    // Return the album with the specified ID and it's Tracks
    @Transaction
    @Query("SELECT * FROM album WHERE albumId = :id")
    AlbumWithTracks getAlbumById(int id);
}
