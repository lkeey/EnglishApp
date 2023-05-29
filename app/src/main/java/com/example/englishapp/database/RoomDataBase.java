package com.example.englishapp.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.englishapp.interfaces.RoomDao;
import com.example.englishapp.models.WordModel;

@Database(entities = WordModel.class, version = 2, exportSchema = false)
public abstract class RoomDataBase extends RoomDatabase {

    private static RoomDataBase roomDataBase;

    @Override
    public void clearAllTables() {

    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(@NonNull DatabaseConfiguration databaseConfiguration) {
        return null;
    }

    public static synchronized RoomDataBase getDatabase(Context context) {
        if (roomDataBase == null) {
            roomDataBase = Room.databaseBuilder(
                    context,
                    RoomDataBase.class,
                    "words_db"
            ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return roomDataBase;
    }

    public abstract RoomDao roomDao();
}
