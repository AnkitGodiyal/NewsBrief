package com.ankit.newsBrief.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [News::class], version = 2)
abstract class NewsDatabase :RoomDatabase(){

    abstract fun newsDao():NewsDao

    companion object{
        @Volatile
        var instance: NewsDatabase? = null

        fun getDatabase(context:Context): NewsDatabase? {

            return instance?: synchronized(this){
                val inst = Room.databaseBuilder(
                    context,
                    NewsDatabase::class.java,
                    "newsDatabase")
                    .fallbackToDestructiveMigration()
                    .build()
                instance=inst
                instance
            }
        }
    }



}