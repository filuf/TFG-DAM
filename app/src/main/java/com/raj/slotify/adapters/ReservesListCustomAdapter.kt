package com.raj.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.models.ReserveRecyclerItem

class ReservesListCustomAdapter(private val dataSet: MutableList<ReserveRecyclerItem>,
                                private val onClick: (ReserveRecyclerItem) -> Unit) :
    RecyclerView.Adapter<ReservesListCustomAdapter.ViewHolder>() {

    fun setItems(newItems: List<ReserveRecyclerItem>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reserveTitleText: TextView
        val reserveDateText: TextView
        val reservePeriodText: TextView
        val remainingOrClientText: TextView

        init {
            // Define click listener for the ViewHolder's View
            reserveTitleText = view.findViewById(R.id.reserveTitleText)
            reserveDateText = view.findViewById(R.id.reserveDateText)
            reservePeriodText = view.findViewById(R.id.reservePeriodText)
            remainingOrClientText = view.findViewById(R.id.remainingOrClientText)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_reserve_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val reserve = dataSet[position]

        viewHolder.reserveTitleText.text = reserve.title
        viewHolder.reserveDateText.text = reserve.date
        viewHolder.reservePeriodText.text = reserve.period
        viewHolder.remainingOrClientText.text = reserve.remainingOrClient

        viewHolder.itemView.setOnClickListener {
            onClick(reserve)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}