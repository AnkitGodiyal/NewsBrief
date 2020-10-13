package com.ankit.newsBrief.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newsList:List<News>)

    @Query("select * from tbl_News")
    fun getAllNews():LiveData<List<News>>

}