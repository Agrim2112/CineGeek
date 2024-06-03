package com.example.cinegeek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.cinegeek.databinding.ActivityDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDashboardBinding.inflate(layoutInflater)
    setContentView(binding.root)
    replaceFragment(ExploreFragment())

        binding.bottomnavbar.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.explore) replaceFragment(ExploreFragment())
            else if (item.itemId == R.id.chat) replaceFragment(ChatListFragment())
            else if (item.itemId == R.id.favourites) replaceFragment(FavouritesFragment())
            else if (item.itemId == R.id.profile) replaceFragment(ProfileFragment())
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