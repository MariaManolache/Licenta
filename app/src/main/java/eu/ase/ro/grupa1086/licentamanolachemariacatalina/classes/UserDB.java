package eu.ase.ro.grupa1086.licentamanolachemariacatalina.classes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDB extends RoomDatabase{

    private static final String DB_NAME = "carti.db";
    private static UserDB instance;

    public static UserDB getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context, UserDB.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract UserDao getUserDao();
}
