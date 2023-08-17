package com.sgamerapps.android.data

import com.google.gson.annotations.SerializedName

class Count(
    @SerializedName("used_count")
    var usedCount: Int,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("counts_left")
    var countsLeft: Int
)