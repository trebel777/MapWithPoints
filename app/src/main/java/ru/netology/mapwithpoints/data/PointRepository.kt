package ru.netology.mapwithpoints.data

import ru.netology.mapwithpoints.models.Point

class PointRepository (private val pointDao: PointDao) {

    val allPoints: List<Point> = pointDao.getAllPoints()

    suspend fun insert(point: Point) {
        pointDao.insert(point)
    }

    suspend fun delete(point: Point) {
        pointDao.delete(point)
    }

    suspend fun update(point: Point) {
        pointDao.update(point.id, point.title, point.description)
    }
}