package com.raj.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.dtos.reserves.TimeIntervalDTO
import com.raj.slotify.tools.FormatUtils

class ListScheduleIntervalsAdapter (private val dataSet: MutableList<TimeIntervalDTO>,
                                    private val onClick: (TimeIntervalDTO) -> Unit) :
    RecyclerView.Adapter<ListScheduleIntervalsAdapter.ViewHolder>() {

    fun setItems(newItems: List<TimeIntervalDTO>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val intervalText: TextView

        init {
            intervalText = view.findViewById(R.id.slotIntervalText)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_interval_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val interval = dataSet[position]

        viewHolder.intervalText.text = "${FormatUtils.formatTime(interval.startTime)} - ${FormatUtils.formatTime(interval.endTime)}"
        viewHolder.itemView.setOnClickListener {
            selectItem(viewHolder)
            onClick(interval)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun selectItem(viewHolder: ViewHolder) {
        val itemView = viewHolder.itemView

        val cardView = itemView as com.google.android.material.card.MaterialCardView

        cardView.strokeColor = itemView.context.resources.getColor(R.color.md_theme_primary)
        cardView.strokeWidth = 4

        viewHolder.intervalText.setTextColor(itemView.context.resources.getColor(R.color.md_theme_primary))
        viewHolder.intervalText.setTypeface(null, android.graphics.Typeface.BOLD)
    }
}