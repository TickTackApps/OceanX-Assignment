package com.ticktackapps.oceanxassignment.Adapters


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ticktackapps.oceanxassignment.OrderDetailActivity
import com.ticktackapps.oceanxassignment.R
import com.ticktackapps.oceanxassignment.Utils.BOOKED_AGAIN_ORDER
import com.ticktackapps.oceanxassignment.Utils.CANCELLED_ORDER
import com.ticktackapps.oceanxassignment.Utils.COMPLETED_ORDER
import com.ticktackapps.oceanxassignment.Utils.FOUR_WHEELER
import com.ticktackapps.oceanxassignment.Utils.OrderData
import com.ticktackapps.oceanxassignment.Utils.PreferencesHelper
import com.ticktackapps.oceanxassignment.Utils.THREE_WHEELER
import com.ticktackapps.oceanxassignment.Utils.TWO_WHEELER

class OrderListAdapter(private val orderList : ArrayList<OrderData>, context: Context, private val listener: OnOrderActionListener) : RecyclerView.Adapter<OrderListAdapter.ViewHolderClass>() {


    interface OnOrderActionListener {
        fun reloadListData(order: OrderData)
        fun openDetailActivity(order: OrderData)

    }


    val enoughContext = context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderClass {

        val orderView = LayoutInflater.from(parent.context).inflate(R.layout.order_list_item, parent, false)
        return ViewHolderClass(orderView)

    }

    override fun onBindViewHolder(
        holder: ViewHolderClass,
        position: Int
    ) {

        holder.bind(orderList[position],position)
        val currentOrder = orderList[position]

        holder.vehicleType.text = currentOrder.vehicleType
        holder.orderPrice.text = currentOrder.orderCost
        holder.orderID.text = "#ORD"+currentOrder.orderID
        holder.orderStart.text = currentOrder.orderStart
        holder.orderEnd.text = currentOrder.orderEnd
        holder.orderStatus.text = currentOrder.orderStatus

        val monthsNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        val date = currentOrder.orderDate.split(" ")

        holder.timeDate.text = date[0] + " " + monthsNames[date[1].toInt()-1] + ", " + currentOrder.orderTime

        when(currentOrder.orderStatus){

            COMPLETED_ORDER -> holder.orderStatus.setBackgroundResource(R.drawable.status_green).also {
                holder.orderStatus.setTextColor(ContextCompat.getColor(enoughContext,R.color.green))
            }
            BOOKED_AGAIN_ORDER -> holder.orderStatus.setBackgroundResource(R.drawable.status_yellow).also {
                holder.orderStatus.setTextColor(ContextCompat.getColor(enoughContext,R.color.yellow))
            }
            CANCELLED_ORDER -> holder.orderStatus.setBackgroundResource(R.drawable.status_red).also {
                holder.orderStatus.setTextColor(ContextCompat.getColor(enoughContext,R.color.red))
            }

        }

        when(currentOrder.vehicleType){

            FOUR_WHEELER -> holder.vehicleImg.setImageResource(R.drawable.truck)
            TWO_WHEELER -> holder.vehicleImg.setImageResource(R.drawable.scooter)
            THREE_WHEELER -> holder.vehicleImg.setImageResource(R.drawable.rickshaw)
            else -> holder.vehicleImg.setImageResource(R.drawable.truck)

        }

        holder.orderBA.setOnClickListener {

            newOrder(enoughContext, currentOrder)
            listener.reloadListData(currentOrder)

        }
        holder.orderInvoice.setOnClickListener {

            Toast.makeText(enoughContext, currentOrder.orderInvoice, Toast.LENGTH_SHORT).show()

        }

        holder.itemView.setOnClickListener {

            listener.openDetailActivity(currentOrder)

        }

    }

    override fun getItemCount(): Int {

        return orderList.size

    }




    fun deleteOrder(context: Context, orderID : OrderData){

        PreferencesHelper.removeOrder(context, orderID)

    }

    fun newOrder(context: Context, order: OrderData){

        val newOrderItem = order

        newOrderItem.orderStatus = "Booked Again"

        var excluded = mutableListOf<String>()

        for (i in PreferencesHelper.getOrderList(enoughContext)) {
            excluded.add(i.orderID)
        }

        var foundUnique = true

        while (foundUnique) {

            var random = (10000..99999).random().toString()

            if (!excluded.contains(random)) {

                newOrderItem.orderID = (10000..99999).random().toString()
                foundUnique = false

            }

        }

        newOrderItem.orderTime = "0"+(1..9).random().toString()+":"+(0..5).random().toString()+(0..9).random().toString() + listOf(" AM", " PM").random()

        PreferencesHelper.addOrder(enoughContext,newOrderItem)

    }


    inner class ViewHolderClass(orderView : View) : RecyclerView.ViewHolder(orderView) {

        val vehicleType = orderView.findViewById<TextView>(R.id.vehicle_type)
        val vehicleImg = orderView.findViewById<ImageView>(R.id.order_vehicle)
        val orderPrice = orderView.findViewById<TextView>(R.id.order_cost)
        val timeDate = orderView.findViewById<TextView>(R.id.order_date)
        val orderID = orderView.findViewById<TextView>(R.id.order_id)
        val orderStart = orderView.findViewById<TextView>(R.id.order_start)
        val orderEnd = orderView.findViewById<TextView>(R.id.order_end)
        val orderStatus = orderView.findViewById<TextView>(R.id.order_status)
        val orderBA = orderView.findViewById<Button>(R.id.order_b_a)
        val orderInvoice = orderView.findViewById<Button>(R.id.order_invoice)


        fun bind(order: OrderData, position: Int) {

            val moreButton = itemView.findViewById<ImageView>(R.id.order_menu)
            moreButton.setOnClickListener { view ->
                showPopupMenu(view, order, position)
            }
        }


        private fun showPopupMenu(view: View, order: OrderData, position: Int) {
            val contextWrapper = ContextThemeWrapper(view.context, R.style.WhitePopupTheme)

            val popup = PopupMenu(contextWrapper, view)
            popup.menuInflater.inflate(R.menu.order_item_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.order_item_menu_delete -> {
                        deleteOrder(enoughContext, order)
                        listener.reloadListData(order)
                        true
                    }
                    R.id.order_item_menu_b_a -> {
                        newOrder(enoughContext, order)
                        listener.reloadListData(order)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}