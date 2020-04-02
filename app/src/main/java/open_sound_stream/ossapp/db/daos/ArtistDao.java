package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.annotation.TransitionRes;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.Artist;
import open_sound_stream.ossapp.db.entities.ArtistWithAlbums;

@Dao
public interface ArtistDao {
    @Insert
    void insertArtist(Artist artist);
    @Update
    void updateArtist(Artist artist);
    @Delete
    void deleteArtist(Artist artist);

    @Transaction
    @Query("SELECT * FROM artist")
    List<ArtistWithAlbums> getArtistsWithAlbums();

    @Transaction
    @Query("SELECT * FROM artist WHERE artistId = :id")
    ArtistWithAlbums getArtistById(int id);
}
