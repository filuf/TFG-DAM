package com.raj.slotify.models

import java.util.UUID

data class ReserveRecyclerItem(
    val id: UUID,
    val title: String,
    val date: String,
    val period: String,
    val remainingOrClient: String
)
