package com.example.englishapp.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.englishapp.models.WordModel;

import java.util.List;
@Dao
public interface RoomDao {

    @Query("SELECT * FROM words")
    List<WordModel> getAllWords();

    @Insert
    void insertWord(WordModel word);

    @Delete
    void deleteWord(WordModel word);

    @Query("DELETE FROM words")
    void deleteAll();

    @Query("SELECT COUNT(id) FROM words")
    int getRowCount();
}
