package com.raj.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.tools.TextUtils
import java.time.LocalDateTime

class ReservesListCustomAdapter(private val dataSet: MutableList<ReserveSummary>,
                                private val onClick: (ReserveSummary) -> Unit) :
    RecyclerView.Adapter<ReservesListCustomAdapter.ViewHolder>() {

    fun setItems(newItems: List<ReserveSummary>) {
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
        val reserveItemStatusText: TextView

        init {
            // Define click listener for the ViewHolder's View
            reserveTitleText = view.findViewById(R.id.reserveTitleText)
            reserveDateText = view.findViewById(R.id.reserveDateText)
            reservePeriodText = view.findViewById(R.id.reservePeriodText)
            reserveItemStatusText = view.findViewById(R.id.reserveItemStatusText)
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

        val context = viewHolder.itemView.context

        viewHolder.reserveTitleText.text = reserve.serviceName

        val dateInfo = calculateDate(
            reserve.startDateTime,
            reserve.endDateTime
        )
        viewHolder.reserveDateText.text = dateInfo[1]
        viewHolder.reservePeriodText.text = dateInfo[0]

        val cardView = viewHolder.itemView as com.google.android.material.card.MaterialCardView

        if (reserve.isCanceled) {
            viewHolder.reserveItemStatusText.text = context.getString(R.string.canceled_word)
            viewHolder.reserveItemStatusText.setTextColor(context.getColor(R.color.md_theme_error))

            cardView.alpha = 0.38f
        } else {
            viewHolder.reserveItemStatusText.text =
                TextUtils.formatRemainingText(reserve.startDateTime, reserve.endDateTime, context)

            viewHolder.reserveItemStatusText.setTextColor(context.getColor(R.color.md_theme_primary))

            cardView.alpha = 1f
        }
        cardView.radius = context.resources.displayMetrics.density * 20

        viewHolder.itemView.setOnClickListener {
            onClick(reserve)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    fun calculateDate(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): List<String> {
        val listToReturn: ArrayList<String> = arrayListOf()

        listToReturn.add("${TextUtils.formatTime(startTime.toLocalTime())} - ${TextUtils.formatTime(endTime.toLocalTime())}")
        listToReturn.add(TextUtils.formatDate(startTime.toLocalDate()))
        return listToReturn
    }

}