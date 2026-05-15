package com.raj.slotify.models.client

import java.util.UUID

data class ClientData (

    var clientId: UUID,
    var userName: String,
    var s3ImageKey:String,
    var phoneNumber: String,
    var email: String

)