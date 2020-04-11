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
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.ArtistWithAlbums;

@Dao
public interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArtist(Artist artist);
    @Update
    void updateArtist(Artist artist);
    @Delete
    void deleteArtist(Artist artist);

    @Transaction
    @Query("SELECT * FROM artist")
    LiveData<List<ArtistWithAlbums>> getArtistsWithAlbums();

    @Transaction
    @Query("SELECT * FROM artist WHERE artistId = :id LIMIT 1")
    LiveData<ArtistWithAlbums> getArtistById(long id);

    @Transaction
    @Query("SELECT * FROM artist WHERE artistName LIKE :artistName LIMIT 1")
    LiveData<ArtistWithAlbums> getArtistByName(String artistName);
}
