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

class ServiceListAdapter(
    private val dataSet: MutableList<GetServicesResponse>,
    private val onClick: (GetServicesResponse) -> Unit
) : RecyclerView.Adapter<ServiceListAdapter.ViewHolder>() {

    fun setItems(newItems: List<GetServicesResponse>) {
        dataSet.clear()
        dataSet.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceImage: ImageView = view.findViewById(R.id.serviceImageView)
        val serviceName: TextView = view.findViewById(R.id.serviceNameText)
        val serviceDuration: TextView = view.findViewById(R.id.serviceDurationText)
        val servicePrice: TextView = view.findViewById(R.id.servicePriceText)
        val serviceDescription: TextView = view.findViewById(R.id.serviceDescriptionText)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_service_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val service = dataSet[position]

        viewHolder.serviceName.text = service.serviceName

        val hours = service.serviceMinutesDuration / 60
        val mins = service.serviceMinutesDuration % 60
        viewHolder.serviceDuration.text = if (hours > 0) "${hours}h ${mins}min" else "${mins} min"

        viewHolder.servicePrice.text = TextUtils.formatPrice(service.servicePriceCent)

        viewHolder.serviceDescription.text = service.description

        if (!service.s3ImageKey.isNullOrEmpty()) {
            viewHolder.serviceImage.load(service.s3ImageKey) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }
        } else {
            viewHolder.serviceImage.setImageResource(R.drawable.ic_launcher_background)
        }

        viewHolder.itemView.setOnClickListener {
            onClick(service)
        }
    }

    override fun getItemCount() = dataSet.size
}
