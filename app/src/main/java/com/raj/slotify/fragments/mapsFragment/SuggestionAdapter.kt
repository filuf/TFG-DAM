package com.raj.slotify.fragments.mapsFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.dtos.maps.PlaceSuggestion

class SuggestionAdapter(
    private val onClick: (PlaceSuggestion) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    private val items = mutableListOf<PlaceSuggestion>()

    fun submitList(newItems: List<PlaceSuggestion>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val placeText: TextView = view.findViewById(R.id.textPlace)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggestion_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.placeText.text = item.displayName

        holder.view.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = items.size
}