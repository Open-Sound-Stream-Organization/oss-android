package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.ArtistAlbumCrossRef;

@Dao
public interface ArtistAlbumCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArtistAlbumCrossRef(ArtistAlbumCrossRef crossRef);
    @Update
    void updateArtistAlbumCrossRef(ArtistAlbumCrossRef crossRef);
    @Delete
    void deleteArtistAlbumCrossRef(ArtistAlbumCrossRef crossRef);

    @Query("SELECT * FROM artistalbumcrossref")
    LiveData<List<ArtistAlbumCrossRef>> getArtistAlbumCrossRefs();

    @Query("SELECT * FROM artistalbumcrossref WHERE artistId = :artistId AND albumId = :albumId LIMIT 1")
    LiveData<ArtistAlbumCrossRef> getArtistAlbumCrossRefById(long artistId, long albumId);

}
