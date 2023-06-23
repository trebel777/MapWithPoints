package ru.netology.mapwithpoints.activitys


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.netology.mapwithpoints.R
import ru.netology.mapwithpoints.data.PointDao
import ru.netology.mapwithpoints.data.PointData
import ru.netology.mapwithpoints.models.Point


class Map : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var pointDao: PointDao

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                mMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                // TODO: show sorry dialog
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            showAddPointDialog(latLng)
        }
        if (arguments != null && arguments?.containsKey("latitude") == true && arguments?.containsKey("longitude") == true) {
            val latitude = arguments?.getDouble("latitude") ?: 0.0
            val longitude = arguments?.getDouble("longitude") ?: 0.0
            val latLng = LatLng(latitude, longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            mMap.addMarker(MarkerOptions().position(latLng))
        } else {
            val moscowLatLng = LatLng(55.751244, 37.618423)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moscowLatLng, 10f))
            mMap.addMarker(MarkerOptions().position(moscowLatLng))
        }
//        val latitude = arguments?.getDouble("latitude", 0.0) ?: 0.0
//        val longitude = arguments?.getDouble("longitude", 0.0) ?: 0.0
//        val latLng = LatLng(latitude, longitude)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
//        mMap.addMarker(MarkerOptions().position(latLng))

        val pointDatabase = PointData.getData(requireContext())
        pointDao = pointDatabase.pointDao()

        lifecycle.coroutineScope.launchWhenCreated {
            mMap.apply {
                isTrafficEnabled = true
                isBuildingsEnabled = true

                uiSettings.apply {
                    isZoomControlsEnabled = true
                    setAllGesturesEnabled(true)
                }
            }
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    mMap.apply {
                        isMyLocationEnabled = true
                        uiSettings.isMyLocationButtonEnabled = true
                    }

                    val fusedLocationProviderClient = LocationServices
                        .getFusedLocationProviderClient(requireActivity())

                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                        println(it)
                    }
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    // TODO: показать диалог с объяснением
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }


    private fun showAddPointDialog(latLng: LatLng) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.add_point_dialog, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Point")
            .setPositiveButton("Save") { dialog, _ ->
                val title = titleEditText.text.toString()
                val description = descriptionEditText.text.toString()
                savePoint(latLng, title, description)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun savePoint(latLng: LatLng, title: String, description: String) {
        val point = Point(latitude = latLng.latitude, longitude = latLng.longitude)
        point.title = title
        point.description = description

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                pointDao.insert(point)
            }
            mMap.addMarker(MarkerOptions().position(latLng).title(title))
        }
    }
}

