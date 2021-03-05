package com.mandiri.movie.model.data

import com.mandiri.movie.util.KEY_RESULT_LIST
import com.mandiri.movie.util.KEY_VIDEO_SITE
import com.mandiri.movie.util.KEY_VIDEO_TYPE
import com.google.gson.annotations.SerializedName

class VideoResults(
        @SerializedName(KEY_RESULT_LIST)
        val videoList: List<Video>
)

data class Video(
        val key: String,
        val name: String,

        @SerializedName(KEY_VIDEO_SITE)
        val siteType: String,

        @SerializedName(KEY_VIDEO_TYPE)
        val videoType: String
)