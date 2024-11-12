package com.example.aman_singh_myruns2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
//REFERENCES:
//https://www.youtube.com/watch?v=-wB_JE_PRTo
//https://www.youtube.com/watch?v=h41FnEH91D0
//https://www.youtube.com/watch?v=wtpRp2IpCSo
//https://www.youtube.com/watch?v=D0RKVAQ9ZQo
//https://www.youtube.com/watch?v=BhyavkT2UO4
//https://www.youtube.com/watch?v=WSOmYN8y0_k
//https://www.youtube.com/watch?v=K4CGYiQu52s
//https://www.youtube.com/watch?v=FjrKMcnKahY&list=PLEtMn0Sw3XCdLmYfWmVn82gW1lP65E1CL
//https://developer.android.com/reference/android/hardware/camera2/CameraCharacteristics
//https://developer.android.com/reference/android/hardware/camera2/CameraManager

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // Set up the ViewPager2 adapter
        viewPager.adapter = ViewPagerAdapter(this)

        // Link TabLayout and ViewPager2 using TabLayoutMediator
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Start"
                1 -> "History"
                2 -> "Settings"
                else -> null
            }
        }.attach()
    }

    // Adapter for ViewPager2
    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StartFragment()
                1 -> HistoryFragment()
                2 -> SettingsFragment()
                else -> StartFragment()
            }
        }
    }
}