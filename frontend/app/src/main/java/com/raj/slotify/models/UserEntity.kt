package com.raj.slotify.models

import java.time.LocalDateTime
import java.util.UUID

data class UserEntity (
    var userID: UUID,
    var userName: String,
    var s3ImageKey: String?,
    var phoneNumber: String?,
    var emailAddress: String,
    var createdAt: LocalDateTime,
    var createdByCompany: UUID?
)