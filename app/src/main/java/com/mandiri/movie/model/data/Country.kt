package com.mandiri.movie.model.data

import com.mandiri.movie.util.KEY_COUNTRY_ISO_CODE
import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName(KEY_COUNTRY_ISO_CODE)
    val code: String,
    val name: String
)