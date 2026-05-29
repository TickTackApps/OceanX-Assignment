package com.ticktackapps.oceanxassignment.Fragments


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.ticktackapps.oceanxassignment.Adapters.OrderListAdapter
import com.ticktackapps.oceanxassignment.HelpActivity
import com.ticktackapps.oceanxassignment.OrderDetailActivity
import com.ticktackapps.oceanxassignment.R
import com.ticktackapps.oceanxassignment.Utils.FOUR_WHEELER
import com.ticktackapps.oceanxassignment.Utils.OrderData
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_COST
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_END
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_ID
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_START
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_STATUS
import com.ticktackapps.oceanxassignment.Utils.PASS_ORDER_TIME
import com.ticktackapps.oceanxassignment.Utils.PASS_VEHICLE_TYPE
import com.ticktackapps.oceanxassignment.Utils.PreferencesHelper
import com.ticktackapps.oceanxassignment.Utils.THREE_WHEELER
import com.ticktackapps.oceanxassignment.Utils.TWO_WHEELER


class OrdersFragment : Fragment(), OrderListAdapter.OnOrderActionListener {

    private lateinit var orderList : ArrayList<OrderData>
    private lateinit var sortButton: Button
    private var sortMethod = 0
    private var filtersArray = mutableListOf<String>()
    private var statusFilter = ""
    private lateinit var filterBtn : Button
    private lateinit var spinner: Spinner
    private lateinit var crossIco : ImageView
    private lateinit var ordInfTxt : LinearLayout
    private lateinit var orderListView: RecyclerView
    private lateinit var orderNavigation : TabLayout
    private lateinit var searchBox: EditText
    private lateinit var CardContainer : CardView
    private lateinit var orderHelpButton: ExtendedFloatingActionButton
    private var searchText = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_orders, container, false)


        sortButton = view.findViewById(R.id.sort_btn)
        ordInfTxt = view.findViewById(R.id.order_info_txt)
        crossIco = view.findViewById(R.id.cross_ico)
        spinner = view.findViewById(R.id.order_sort_spinner)
        filterBtn = view.findViewById(R.id.filter_btn)
        orderListView = view.findViewById(R.id.order_list_view)
        orderNavigation = view.findViewById(R.id.order_nav)
        searchBox = view.findViewById(R.id.search_box)
        CardContainer = view.findViewById(R.id.order_cont_card)
        orderHelpButton = view.findViewById(R.id.order_help_btn)

        CardContainer.setBackgroundResource(R.drawable.order_container_back)

        fetchOrderData()
        sortSetup()
        tabSetup()

        orderHelpButton.setOnClickListener {

            val intent = Intent(requireContext(), HelpActivity::class.java)
            startActivity(intent)

        }

        searchBox.setOnEditorActionListener {_,actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH){

                val imm = requireContext().getSystemService(InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(searchBox.windowToken, 0)

                searchBox.clearFocus()

                searchText = searchBox.text.toString()
                fetchOrderData()

            }
            true

        }

        searchBox.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                searchText = searchBox.text.toString()
                fetchOrderData()
            }

        })

        crossIco.setOnClickListener {

            ordInfTxt.isVisible = false

        }

        filterBtn.setOnClickListener {

            filterDialogue(filtersArray)

        }


        return view

    }

    fun fetchOrderData(){

        orderList = PreferencesHelper.getOrderList(requireContext())
        searchFilter()

    }

    fun searchFilter(){

        if (!searchText.isEmpty()){

            orderList.removeAll {!it.orderID.lowercase().contains(searchText.lowercase()) &&
                    !it.orderStart.lowercase().contains(searchText.lowercase()) &&
                    !it.orderEnd.lowercase().contains(searchText.lowercase())}

        }

        selectionSorting()

    }

    fun selectionSorting(){

        if (statusFilter != ""){

            orderList.removeAll{it.orderStatus != statusFilter}

        }

        filterOrderList()
    }

    fun filterOrderList(){

        if(!filtersArray.isEmpty()){

            for (i in filtersArray){

                orderList.removeAll{!filtersArray.contains(it.vehicleType)}

            }

        }

        sortOrderList()

    }

    fun sortOrderList(){

        when(sortMethod){

            0 -> {
                orderList.sortBy { it.orderCost.toInt() }
            }
            1
                 -> {
                orderList.sortByDescending {
                    it.orderCost.toInt()
                }
            }

        }

        orderListSetup()

    }

    fun orderListSetup(){

        orderListView.layoutManager = LinearLayoutManager(requireContext())
        orderListView.hasFixedSize()
        val adapter = OrderListAdapter(orderList, requireContext(),this)
        adapter.notifyDataSetChanged()
        orderListView.adapter = adapter


    }

    fun tabSetup(){

        orderNavigation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {

                if (p0?.position != 0){
                    statusFilter = p0?.text.toString()
                }else{
                    statusFilter = ""
                }
                fetchOrderData()

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })

    }

    fun sortSetup(){

        val sortItem = listOf("Price:low to high", "Price:high to low")

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.order_sort_dropdown_spinner,
            sortItem
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                Toast.makeText(context, sortItem[position], Toast.LENGTH_SHORT).show()
                sortMethod = position
                fetchOrderData()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        sortButton.setOnClickListener {
            spinner.performClick()
        }

    }



    fun filterDialogue(selected : MutableList<String>){

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.filter_dialogue_layout, null)

        val flexbox = dialogView.findViewById<FlexboxLayout>(R.id.filter_chip_cont)

        val filterOptions = listOf(
            TWO_WHEELER, THREE_WHEELER, FOUR_WHEELER
        )

        for (option in filterOptions) {
            val chip = LayoutInflater.from(requireContext())
                .inflate(R.layout.order_filter_chip, flexbox, false) as TextView
            chip.text = option

            if (selected.contains(option)){
                chip.isSelected = true
                chip.setBackgroundResource(R.drawable.order_tab_indicator)
                chip.setTextColor(Color.WHITE)
            }else{
                chip.isSelected = false
                chip.setBackgroundResource(R.drawable.outlined_view)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            // Optional: handle chip click (e.g., toggle selection)
            chip.setOnClickListener {
                it.isSelected = !it.isSelected
                // Change background to show selection state
                if (it.isSelected) {
                    (it as TextView).setBackgroundResource(R.drawable.order_tab_indicator)
                    it.setTextColor(Color.WHITE)
                } else {
                    (it as TextView).setBackgroundResource(R.drawable.outlined_view)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
            flexbox.addView(chip)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.order_filter_apply).setOnClickListener {
            // Collect selected items (example – you can adapt)
            val selected = mutableListOf<String>()
            for (i in 0 until flexbox.childCount) {
                val child = flexbox.getChildAt(i) as TextView
                if (child.isSelected) selected.add(child.text.toString())
            }

            filtersArray = selected
            fetchOrderData()

            Toast.makeText(requireContext(), "Filters: ${selected.joinToString()}", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.order_filter_clear).setOnClickListener {

            filtersArray.clear()
            fetchOrderData()
            dialog.dismiss()

        }
        dialogView.findViewById<Button>(R.id.order_filter_cancel).setOnClickListener {

            dialog.dismiss()

        }

        dialog.show()

    }

    override fun reloadListData(order: OrderData) {

        fetchOrderData()

    }

    override fun openDetailActivity(order: OrderData) {

        val intent = Intent(requireContext(), OrderDetailActivity::class.java)

        intent.putExtra(PASS_VEHICLE_TYPE, order.vehicleType)
        intent.putExtra(PASS_ORDER_ID, order.orderID)
        intent.putExtra(PASS_ORDER_END, order.orderEnd)
        intent.putExtra(PASS_ORDER_START, order.orderStart)
        intent.putExtra(PASS_ORDER_STATUS, order.orderStatus)
        intent.putExtra(PASS_ORDER_TIME, order.orderDate+", "+order.orderTime)
        intent.putExtra(PASS_ORDER_COST, order.orderCost)

        startActivity(intent)


    }

    companion object {

    }
}