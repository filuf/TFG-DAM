package com.raj.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.dtos.company.IntervalSummary
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class IntervalsListAdapter(
    private val dataSet: MutableList<IntervalSummary>,
    private val onClick: (IntervalSummary) -> Unit
) : RecyclerView.Adapter<IntervalsListAdapter.ViewHolder>() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun setItems(newItems: List<IntervalSummary>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val startText: TextView = view.findViewById(R.id.exceptionStartText)
        val endText: TextView = view.findViewById(R.id.exceptionEndText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_exception_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val interval = dataSet[position]
        val context = holder.itemView.context

        val startDayName = interval.startDateTime.dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale.getDefault())
            .replaceFirstChar { it.uppercase() }
        val endDayName = interval.endDateTime.dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale.getDefault())
            .replaceFirstChar { it.uppercase() }

        holder.startText.text = "${context.getString(R.string.start_word)}: $startDayName ${interval.startDateTime.format(dateFormatter)}"
        holder.endText.text = "${context.getString(R.string.end_word)}: $endDayName ${interval.endDateTime.format(dateFormatter)}"

        holder.itemView.setOnClickListener { onClick(interval) }
    }

    override fun getItemCount() = dataSet.size
}
