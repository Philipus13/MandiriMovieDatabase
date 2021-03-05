package com.mandiri.movie.model.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mandiri.movie.util.KEY_RESULT_LIST


class ReviewList(
    val id: Int,
    val page: Int,
    @SerializedName(KEY_RESULT_LIST)
    val results: List<Review>,
    val total_pages: Int,
    val total_results: Int
)

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val url: String
)