package com.ticktackapps.oceanxassignment.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.errorprone.annotations.ForOverride
import com.ticktackapps.oceanxassignment.R
import com.ticktackapps.oceanxassignment.Utils.BOOKED_AGAIN_ORDER
import com.ticktackapps.oceanxassignment.Utils.CANCELLED_ORDER
import com.ticktackapps.oceanxassignment.Utils.COMPLETED_ORDER
import com.ticktackapps.oceanxassignment.Utils.CREATED_ORDERS_DATA
import com.ticktackapps.oceanxassignment.Utils.FOUR_WHEELER
import com.ticktackapps.oceanxassignment.Utils.OrderData
import com.ticktackapps.oceanxassignment.Utils.PreferencesHelper
import com.ticktackapps.oceanxassignment.Utils.THREE_WHEELER
import com.ticktackapps.oceanxassignment.Utils.TWO_WHEELER
import java.time.Year
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var saveOrderDataBtn: Button
    private lateinit var vehicleTypeInput: Spinner
    private lateinit var orderDateInput: TextView
    private lateinit var orderTimeInput: TextView
    private lateinit var orderStartInput: EditText
    private lateinit var orderEndInput: EditText
    private lateinit var orderCostInput: EditText
    private lateinit var orderStatusInput: Spinner
    private lateinit var orderIDInput: EditText
    private lateinit var dataToSave: OrderData
    private lateinit var calendar: Calendar
    private lateinit var excluded: MutableList<String>

    private var addable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_home, container, false)

        vehicleTypeInput = view.findViewById(R.id.vehicle_type_input)
        orderDateInput = view.findViewById(R.id.order_date_input)
        orderTimeInput = view.findViewById(R.id.order_time_input)
        orderStartInput = view.findViewById(R.id.order_start_input)
        orderEndInput = view.findViewById(R.id.order_end_input)
        orderCostInput = view.findViewById(R.id.order_cost_input)
        orderStatusInput = view.findViewById(R.id.order_status_input)
        orderIDInput = view.findViewById(R.id.order_id_input)
        saveOrderDataBtn = view.findViewById(R.id.order_data_save_btn)

        dataToSave = OrderData(
            TWO_WHEELER, "500",
            uniqueIDgenerator(),
            "01 01 2000", "12:00 PM",
            CANCELLED_ORDER, "Gurugram",
            "Kanpur", "Unavailable"
        )

        vehicleTypeSelection()
        dateAndTimeSelection()
        orderStatusSelection()
        saveAll()


        return view
    }

    fun uniqueIDgenerator(): String {

        excluded = mutableListOf()

        for (i in PreferencesHelper.getOrderList(requireContext())) {
            excluded.add(i.orderID)
        }

        var foundUnique = true

        while (foundUnique) {

            var random = (10000..99999).random().toString()

            if (!excluded.contains(random)) {

                return random

            }

        }

        return ""

    }

    fun saveAll() {
        saveOrderDataBtn.setOnClickListener {

            if (orderIDInput.text.toString().isBlank()){

                dataToSave.orderID = uniqueIDgenerator()


            }else if(orderIDInput.text.toString().length != 5){

                Toast.makeText(requireContext(), "Order ID should be 5 digit long", Toast.LENGTH_SHORT).show()
                addable = false

            }else if(excluded.contains(orderIDInput.text.toString())){

                Toast.makeText(requireContext(), "Order ID should be Unique", Toast.LENGTH_SHORT).show()
                addable = false

            }else{

                dataToSave.orderID = orderIDInput.text.toString()

            }

            if (orderStartInput.text.toString().isBlank()) addable = false

            if (orderEndInput.text.toString().isBlank()) addable = false

            if (orderCostInput.text.toString().isBlank()) addable = false

            if (addable) {

                dataToSave.orderCost = orderCostInput.text.toString()
                dataToSave.orderStart = orderStartInput.text.toString()
                dataToSave.orderEnd = orderEndInput.text.toString()

                PreferencesHelper.addOrder(requireContext(), dataToSave)

            } else {
                Toast.makeText(requireContext(), "Fill All Fields", Toast.LENGTH_SHORT).show()
                addable = true
            }

        }
    }

    fun vehicleTypeSelection() {

        val vehicleTypes = arrayListOf(TWO_WHEELER, THREE_WHEELER, FOUR_WHEELER)

        val typeAdapter =
            ArrayAdapter(requireContext(), R.layout.order_sort_dropdown_spinner, vehicleTypes)

        typeAdapter.setDropDownViewResource(R.layout.order_sort_dropdown_spinner)

        vehicleTypeInput.adapter = typeAdapter

        vehicleTypeInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val selected = parent?.getItemAtPosition(position)

                dataToSave.vehicleType = selected.toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

    }

    fun dateAndTimeSelection() {

        calendar = Calendar.getInstance()

        orderDateInput.setOnClickListener {


            val datePicker = DatePickerDialog(
                requireContext(), { _, year, month, dayOfMonth ->
                    val selectDate = "$dayOfMonth ${month + 1} $year"
                    orderDateInput.text = selectDate
                    dataToSave.orderDate = selectDate
                    Toast.makeText(requireContext(), selectDate, Toast.LENGTH_SHORT).show()

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()

        }


        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        orderTimeInput.setOnClickListener {

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->

                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    val formattedTime = formatter.format(calendar.time)

                    orderTimeInput.text = formattedTime.uppercase()
                    dataToSave.orderTime = formattedTime.uppercase()
                },
                hour,
                minute,
                false
            )
            timePicker.show()

        }

    }


    fun orderStatusSelection() {

        val statusType = arrayListOf(CANCELLED_ORDER, COMPLETED_ORDER, BOOKED_AGAIN_ORDER)

        val typeAdapter =
            ArrayAdapter(requireContext(), R.layout.order_sort_dropdown_spinner, statusType)

        typeAdapter.setDropDownViewResource(R.layout.order_sort_dropdown_spinner)

        orderStatusInput.adapter = typeAdapter

        orderStatusInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val selected = parent?.getItemAtPosition(position)

                dataToSave.orderStatus = selected.toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

    }

    companion object {

    }
}