package com.raj.slotify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.raj.slotify.R
import com.raj.slotify.dtos.company.GetServicesResponse
import com.raj.slotify.tools.TextUtils

class CompanyServicesAdapter (private val dataSet: MutableList<GetServicesResponse>,
                              private val onClick: (GetServicesResponse) -> Unit) :
    RecyclerView.Adapter<CompanyServicesAdapter.ViewHolder>() {

    fun setItems(newItems: List<GetServicesResponse>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceNameText: TextView
        val servicePriceText: TextView
        val serviceDurationText: TextView
        val serviceImageView: ImageView

        init {
            // Define click listener for the ViewHolder's View
            serviceNameText = view.findViewById(R.id.companyServiceNameText)
            servicePriceText = view.findViewById(R.id.companyServicePriceText)
            serviceDurationText = view.findViewById(R.id.companyServiceDurationText)
            serviceImageView = view.findViewById(R.id.serviceImage)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_company_service_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val serviceData = dataSet[position]

        if (serviceData.s3ImageKey != null) {
            viewHolder.serviceImageView.load(serviceData.s3ImageKey) {
                crossfade(true)
                error(R.drawable._logoslotify_retocado)
            }
        }

        viewHolder.serviceNameText.text = serviceData.serviceName
        viewHolder.servicePriceText.text = TextUtils.formatPrice(serviceData.servicePriceCent)

        viewHolder.itemView.setOnClickListener {
            onClick(serviceData)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}