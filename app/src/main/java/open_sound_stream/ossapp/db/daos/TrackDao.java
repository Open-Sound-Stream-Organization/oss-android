package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.Track;

@Dao
public interface TrackDao {
    @Insert
    void insertTrack(Track track);
    @Update
    void updateTrack(Track track);
    @Delete
    void deleteTrack(Track track);

    // Queries
    @Query("SELECT * FROM track")
    List<Track> getAllTracks();

    @Query("SELECT * FROM track WHERE trackId = :id LIMIT 1")
    Track getTrackById(int id);
}
