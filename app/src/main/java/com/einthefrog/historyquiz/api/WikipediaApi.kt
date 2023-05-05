package com.einthefrog.historyquiz.api

import com.einthefrog.historyquiz.model.CategoryMember
import retrofit2.http.GET

interface WikipediaApi {
    @GET
    suspend fun getCategoryMembers(): List<CategoryMember>
}
