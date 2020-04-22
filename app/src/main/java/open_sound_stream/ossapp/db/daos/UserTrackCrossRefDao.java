package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.User;
import open_sound_stream.ossapp.db.entities.UserTrackCrossRef;

@Dao
public interface UserTrackCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserTrackCrossRefDao(UserTrackCrossRef crossRef);
    @Update
    void updateUserTrackCrossRefDao(UserTrackCrossRef crossRef);
    @Delete
    void deleteUserTrackCrossRefDao(UserTrackCrossRef crossRef);

    @Query("SELECT * FROM usertrackcrossref")
    LiveData<List<UserTrackCrossRef>> getUserTrackCrossRefs();

    @Query("SELECT * FROM usertrackcrossref WHERE userId = :userId")
    LiveData<List<UserTrackCrossRef>> getUserTrackCrossRefByUserId(long userId);

}
