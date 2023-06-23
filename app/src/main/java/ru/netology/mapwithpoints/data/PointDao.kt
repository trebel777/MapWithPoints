package ru.netology.mapwithpoints.data

import androidx.room.*
import ru.netology.mapwithpoints.models.Point

@Dao
interface PointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: Point)

    @Delete
    suspend fun delete(point: Point)

    @Query("Select * from points_table order by id ASC" )
    fun getAllPoints(): List<Point>

    @Query("UPDATE points_table Set title = :title, description = :description WHERE id = :id")
    suspend fun update(id: Int?, title: String?, description: String?)
}