package com.sgamerapps.android.data

import com.google.gson.annotations.SerializedName

 class RedeemPlan(
    @SerializedName("id")
    var id: Int,
    @SerializedName("coins")
    var coins: Int,
    @SerializedName("value")
    var value: Int,
    @SerializedName("active")
    var active: Boolean,

)