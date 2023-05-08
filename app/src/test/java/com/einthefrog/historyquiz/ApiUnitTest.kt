package com.einthefrog.historyquiz

import com.einthefrog.historyquiz.api.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ApiUnitTest {
    @Test
    fun testPresidentsListQuery() = runBlocking {
        val api = RetrofitInstance.api
        val response = api.getCategoryMembers(
            title = "Category:Lists of national presidents"
        )
        Assert.assertNotEquals(null, response)
    }
}
