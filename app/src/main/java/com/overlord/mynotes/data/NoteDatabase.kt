package com.overlord.mynotes.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.overlord.mynotes.model.Note

@Database(
    entities = [
        Note::class
    ],
    version = 2,
    exportSchema = true,
)
abstract class NoteDatabase : RoomDatabase(){

    abstract fun noteDao(): NoteDao

    companion object{
        private const val DB_NAME = "note-database"
        private var INSTANCE: NoteDatabase? = null


        fun getInstance(context: Context): NoteDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DB_NAME
                ).addCallback(object : RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}