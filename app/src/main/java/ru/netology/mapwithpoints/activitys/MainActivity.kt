package ru.netology.mapwithpoints.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.netology.mapwithpoints.R
import ru.netology.mapwithpoints.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Map())

        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.map -> replaceFragment(Map())
                R.id.points -> replaceFragment(Points())
                else ->{

                }
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}