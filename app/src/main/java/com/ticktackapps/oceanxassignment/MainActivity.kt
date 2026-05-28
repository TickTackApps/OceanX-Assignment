package com.ticktackapps.oceanxassignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.ticktackapps.oceanxassignment.Fragments.HomeFragment
import com.ticktackapps.oceanxassignment.Fragments.OrdersFragment
import com.ticktackapps.oceanxassignment.Fragments.SampleFragment
import com.ticktackapps.oceanxassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left,0,systemBars.right,0)

            binding.sb.updateLayoutParams {
                height = systemBars.top
            }

            insets

        }
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        placeFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {

            when(it.itemId){
                R.id.bottom_home -> placeFragment(HomeFragment())
                R.id.bottom_order -> placeFragment(OrdersFragment())
                R.id.bottom_pay -> placeFragment(SampleFragment())
                R.id.bottom_Acc -> placeFragment(SampleFragment())
                else -> {}
            }
            true
        }
    }
    private fun placeFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragcont, fragment)
        transaction.commit()

    }

}