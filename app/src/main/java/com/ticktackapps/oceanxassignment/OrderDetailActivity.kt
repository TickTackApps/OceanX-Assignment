package com.ticktackapps.oceanxassignment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_COST
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_END
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_ID
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_START
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_STATUS
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_TIME
import com.ticktackapps.oceanxassignment.Utils.PASS_VEHICLE_TYPE
import com.ticktackapps.oceanxassignment.databinding.ActivityOrderDetailBinding
import kotlin.math.cos

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val vehicleType = intent.getStringExtra(PASS_VEHICLE_TYPE)
        val orderCost = intent.getStringExtra(PASS_ORDER_COST)
        val orderTime = intent.getStringExtra(PASS_ORDER_TIME)
        val orderID = intent.getStringExtra(PASS_ORDER_ID)
        val orderStart = intent.getStringExtra(PASS_ORDER_START)
        val orderEnd = intent.getStringExtra(PASS_ORDER_END)
        val orderStatus = intent.getStringExtra(PASS_ORDER_STATUS)



        binding.getOrderId.setText(orderID)
        binding.getDateTime.setText(orderTime)
        binding.getVehicleType.setText(vehicleType)
        binding.getCost.setText(orderCost)
        binding.getStarting.setText(orderStart)
        binding.getEnd.setText(orderEnd)
        binding.getStatus.setText(orderStatus)



    }
}