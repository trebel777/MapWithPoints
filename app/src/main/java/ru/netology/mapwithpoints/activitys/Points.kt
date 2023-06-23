package ru.netology.mapwithpoints.activitys

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.netology.mapwithpoints.R
import ru.netology.mapwithpoints.adapter.PointsAdapter

import ru.netology.mapwithpoints.data.PointDao
import ru.netology.mapwithpoints.data.PointData

import ru.netology.mapwithpoints.models.Point


class Points : Fragment(), PointsAdapter.OnPointInteractionListener {

    private lateinit var pointsAdapter: PointsAdapter
    private lateinit var pointDao: PointDao



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_points, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        pointsAdapter = PointsAdapter(requireContext(), this, this)
        recyclerView.adapter = pointsAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pointDatabase = PointData.getData(requireContext())
        pointDao = pointDatabase.pointDao()
        loadPointsFromDatabase()
    }

    private fun loadPointsFromDatabase() {
        lifecycleScope.launch {
            val points = withContext(Dispatchers.IO) {
                pointDao.getAllPoints()
            }
            pointsAdapter.setData(points)
            pointsAdapter.updateList(points)
        }
    }

    fun navigateToMapFragment(point: Point) {
        val args = Bundle().apply {
            putDouble("latitude", point.latitude!!)
            putDouble("longitude", point.longitude!!)
        }
        val mapFragment = Map()
        mapFragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, mapFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onPointDelete(point: Point) {
        // Удалить точку из базы данных
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                pointDao.delete(point)
            }
            // Загрузить обновленные данные из базы данных
            val points = withContext(Dispatchers.IO) {
                pointDao.getAllPoints()
            }
            // Обновить список точек в адаптере
            pointsAdapter.updateList(points)
        }
    }
    override fun onPointEdit(point: Point) {
        showEditDialog(point)
    }
    private fun showEditDialog(point: Point) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_point, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.dialogEditTextTitle)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.dialogEditTextDescription)

        editTextTitle.setText(point.title)
        editTextDescription.setText(point.description)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Edit Point")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val newTitle = editTextTitle.text.toString()
                val newDescription = editTextDescription.text.toString()

                // Update point with new title and description
                updatePoint(point, newTitle, newDescription)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }
    private fun updatePoint(point: Point, newTitle: String, newDescription: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                point.title = newTitle
                point.description = newDescription
                pointDao.update(point.id, newTitle, newDescription)
            }
            val points = withContext(Dispatchers.IO) {
                pointDao.getAllPoints()
            }
            pointsAdapter.updateList(points)
        }
    }


}

