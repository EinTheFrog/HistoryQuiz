package com.einthefrog.historyquiz.model

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("continue")
    val continueCode: ContinueCode,
    val query: Query
)
