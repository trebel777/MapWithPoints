package ru.netology.mapwithpoints.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.mapwithpoints.models.Point
import ru.netology.mapwithpoints.utilities.DATABASE_NAME

@Database(entities = arrayOf(Point::class), version = 1, exportSchema = false)
abstract class PointData: RoomDatabase() {

    abstract fun pointDao(): PointDao

    companion object{

        @Volatile
        private var INSTANCE : PointData? = null

        fun getData(context: Context): PointData{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PointData::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}