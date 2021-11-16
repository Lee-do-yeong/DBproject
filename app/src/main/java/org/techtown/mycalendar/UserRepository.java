package org.techtown.mycalendar;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserRepository {
    @Query("SELECT * FROM data")
    List<Data> findAll();

    @Query("SELECT * FROM data where uid=:uid")
    Data findById(int uid);

    @Insert
    void insert(Data data);

    @Update
    void update(Data data);

    @Query("DELETE FROM data WHERE uid=:uid")
    void delete(int uid);

}
