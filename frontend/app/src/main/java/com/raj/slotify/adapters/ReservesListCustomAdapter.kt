package com.raj.slotify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.dtos.reserves.ReserveSummary
import com.raj.slotify.tools.FormatUtils
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

        val context = viewHolder.itemView.context

        viewHolder.reserveTitleText.text = reserve.serviceName

        val dateInfo = calculateDate(
            reserve.startDateTime,
            reserve.endDateTime
        )
        viewHolder.reserveDateText.text = dateInfo[1]
        viewHolder.reservePeriodText.text = dateInfo[0]

        viewHolder.remainingOrClientText.text = calculateRemainingText(reserve.startDateTime, context)

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

        listToReturn.add("${FormatUtils.formatTime(startTime.toLocalTime())} - ${FormatUtils.formatTime(endTime.toLocalTime())}")
        listToReturn.add(FormatUtils.formatDate(startTime.toLocalDate()))
        return listToReturn
    }

    fun calculateRemainingText(startTime: LocalDateTime, context: Context): String {
        // SET REMAINING TEXT
        val now: LocalDateTime = LocalDateTime.now()

        val duration = java.time.Duration.between(now, startTime)
        if (duration.isNegative || duration.isZero) {
            return context.getString(R.string.already_started)
        }

        val remainingDays = duration.toDays()
        val remainingHours = duration.toHours()
        val remainingMinutes = duration.toMinutes()

        return when {
            remainingDays > 0 -> " $remainingDays ${
                if (remainingDays > 1) context.getString(
                    R.string.days_word
                ) else context.getString(R.string.day_word)
            } ${context.getString(R.string.left_after)} "

            remainingHours > 0 -> " $remainingHours ${
                if (remainingHours > 1) context.getString(
                    R.string.hours_word
                ) else context.getString(R.string.hour_word)
            } ${context.getString(R.string.left_after)} "

            else -> " $remainingMinutes ${
                if (remainingMinutes > 1) context.getString(R.string.minutes_word) else context.getString(
                    R.string.minute_word
                )
            } ${context.getString(R.string.left_after)} "
        }
    }

}