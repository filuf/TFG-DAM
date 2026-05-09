package com.raj.slotify.models.api

data class PageResponse<T>(
    var content: List<T>,
    var totalPages: Int,
    var totalElements: Long,
    var size: Int,
    var number: Int // El número de página actual
)
