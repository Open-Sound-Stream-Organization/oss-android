package open_sound_stream.ossapp.db.daos;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import open_sound_stream.ossapp.db.entities.User;
import open_sound_stream.ossapp.db.entities.UserWithTracks;

@Dao
public interface UserDao {
    @Insert
    public void insertUser(User user);
    @Update
    void updateUser(User user);
    @Delete
    void deleteUser(User user);

    @Transaction
    @Query("SELECT * FROM user")
    LiveData<List<UserWithTracks>> getAllUsers();

    @Transaction
    @Query("SELECT * FROM user WHERE lower(userName) LIKE lower(:userName) LIMIT 1")
    LiveData<UserWithTracks> getUserByName(String userName);

    @Transaction
    @Query("SELECT * FROM user WHERE userId = :id LIMIT 1")
    LiveData<UserWithTracks> getUserById(long id);
}
