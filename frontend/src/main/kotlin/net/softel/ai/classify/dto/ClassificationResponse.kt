package net.softel.ai.classify.dto

import com.google.gson.annotations.SerializedName

data class ClassificationResponse(
    @SerializedName("className"   ) var className   : String? = "",
    @SerializedName("probability" ) var probability : Double? = 0.00

)
