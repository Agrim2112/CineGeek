package com.example.cinegeek

import SliderAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityDashboardBinding
import com.example.models.Movies
import com.example.models.Result
import com.example.models.Slider
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDashboardBinding.inflate(layoutInflater)
    setContentView(binding.root)
    replaceFragment(ExploreFragment())

        binding.bottomnavbar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.explore -> replaceFragment(ExploreFragment())
                R.id.cart -> replaceFragment(CartFragment())
                R.id.favourites -> replaceFragment(FavouritesFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
            }
            true
        }
}
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}