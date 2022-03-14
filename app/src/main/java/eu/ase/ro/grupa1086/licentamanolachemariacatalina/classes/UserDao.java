package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Query("select * from users")
    List<User> getAll();

    @Delete
    void delete(User user);

    @Query("delete from users")
    void deleteAll();
}
