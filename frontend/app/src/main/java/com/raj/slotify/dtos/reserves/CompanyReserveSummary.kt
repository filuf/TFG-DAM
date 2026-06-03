package com.raj.slotify.dtos.reserves

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class CompanyReserveSummary(

    @SerializedName("reserveId")
    override val reserveId: UUID,

    @SerializedName("serviceId")
    override val serviceId: UUID,

    @SerializedName("startDateTime")
    override val startDateTime: LocalDateTime,

    @SerializedName("endDateTime")
    override val endDateTime: LocalDateTime,

    @SerializedName("minutesDuration")
    override val minutesDuration: Int,

    @SerializedName("serviceName")
    override val serviceName: String,

    @SerializedName("servicePriceCent")
    override val servicePriceCent: Int,

    @SerializedName("isCanceled")
    override val isCanceled: Boolean,

    @SerializedName("userId")
    val userId: UUID,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("userImageUrl")
    val userImageUrl: String?,

): ReserveSummary (), Parcelable
