package com.einthefrog.historyquiz.api

import com.einthefrog.historyquiz.model.CategoryMember
import com.einthefrog.historyquiz.model.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApi {
    @GET("api.php?action=query&list=categorymembers&format=json")
    suspend fun getCategoryMembers(
        @Query("cmtitle") title: String,
        @Query("cmcontinue") continueCode: String = ""
    ): Response
}
