package com.raj.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.raj.slotify.R
import com.raj.slotify.dtos.company.GetCompanyResponse

class CompaniesListAdapter(private val dataSet: MutableList<GetCompanyResponse>,
                           private val onClick: (GetCompanyResponse) -> Unit) :
    RecyclerView.Adapter<CompaniesListAdapter.ViewHolder>() {

    fun setItems(newItems: List<GetCompanyResponse>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
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
        viewHolder.ratingBar.rating = companyData.rattingAvg.toFloat()
        viewHolder.imageCompany.setImageURI(companyData.s3ImageKey.toUri())

        viewHolder.itemView.setOnClickListener {
            onClick(companyData)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}