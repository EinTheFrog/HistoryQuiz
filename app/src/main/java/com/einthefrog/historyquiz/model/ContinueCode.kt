package com.einthefrog.historyquiz.model

import com.google.gson.annotations.SerializedName

data class ContinueCode(
    @SerializedName("cmcontinue")
    val code: String
)
