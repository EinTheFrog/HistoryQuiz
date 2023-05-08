package com.einthefrog.historyquiz.model

import com.google.gson.annotations.SerializedName

data class Query(
    @SerializedName("categorymembers")
    val categoryMembers: List<CategoryMember>
)
