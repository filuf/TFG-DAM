package com.raj.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.raj.slotify.R
import com.raj.slotify.dtos.company.SearchCompaniesResponse

class CompaniesListAdapter(private val dataSet: MutableList<SearchCompaniesResponse>,
                           private val onClick: (SearchCompaniesResponse) -> Unit) :
    RecyclerView.Adapter<CompaniesListAdapter.ViewHolder>() {

    fun setItems(newItems: List<SearchCompaniesResponse>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<SearchCompaniesResponse>) {
        val startPos = dataSet.size
        dataSet.addAll(newItems)
        notifyItemRangeInserted(startPos, newItems.size)
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyName: TextView
        val ratingBar: RatingBar
        val imageCompany: ImageView

        init {
            // Define click listener for the ViewHolder's View
            companyName = view.findViewById(R.id.companyName)
            ratingBar = view.findViewById(R.id.ratingBar)
            imageCompany = view.findViewById(R.id.imageCompany)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_company_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val companyData = dataSet[position]

        viewHolder.companyName.text = companyData.companyName

        if (companyData.rattingAvg == null)
            viewHolder.ratingBar.visibility = View.GONE
        else
            viewHolder.ratingBar.rating = companyData.rattingAvg.toFloat()

        if (companyData.s3ImageKey != null) {
            viewHolder.imageCompany.load(companyData.s3ImageKey) {
                crossfade(true)
                error(R.drawable._logoslotify_retocado)
            }
        }

        viewHolder.itemView.setOnClickListener {
            onClick(companyData)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}