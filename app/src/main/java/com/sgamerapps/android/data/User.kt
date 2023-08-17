package com.sgamerapps.android.data

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("email_verified_at") var emailVerifiedAt: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("is_admin") var isAdmin: Int? = null,
    @SerializedName("wallet") var wallet: Int? = null,
    @SerializedName("referral_code") var referralCode: String? = null,
    @SerializedName("referrad_by") var referradBy: Int? = null

)