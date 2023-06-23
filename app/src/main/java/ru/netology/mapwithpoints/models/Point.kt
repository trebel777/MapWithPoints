package ru.netology.mapwithpoints.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points_table")
data class Point(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "title") var title: String? = "",
    @ColumnInfo(name = "description") var description: String? = ""
)

