package ru.netology.mapwithpoints.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.mapwithpoints.data.PointData
import ru.netology.mapwithpoints.data.PointRepository

class PointViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PointRepository
    val allPoints: List<Point>

    init {
        val dao = PointData.getData(application).pointDao()
        repository = PointRepository(dao)
        allPoints = repository.allPoints

    }
    fun deletePoint(point: Point) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(point)
    }
    fun insertPoint(point: Point) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(point)
    }
    fun updatePoint(point: Point) = viewModelScope.launch(Dispatchers.IO){
        repository.update(point)
    }
}