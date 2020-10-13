package com.ankit.newsBrief.Room

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.ankit.newsBrief.Retrofit.ApiClient
import com.ankit.newsBrief.Model.Headlines
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NewsRepository(private val newsDao: NewsDao) {

    val apiKey = "89cc9094d2e84413937c985b54c3b06b"
    val newsLiveData: LiveData<List<News>> = newsDao.getAllNews()
    val country = getMyCountry();

    @WorkerThread
    suspend fun insert(news: List<News>) {
        newsDao?.insert(news)
    }

    fun fetchDataFromApi() {

        var call = ApiClient.getInstance().api.getHeadlines(country, apiKey)

        call.enqueue(object : Callback<Headlines> {

            override fun onFailure(call: Call<Headlines>, t: Throwable?) {
            }

            override fun onResponse(call: Call<Headlines>?, response: Response<Headlines>?) {

                if(response!!.isSuccessful && response.body()!!.articles!=null){
                    var articles=response!!.body()!!.articles
                    articles.reverse()
                    CoroutineScope(Dispatchers.IO).launch{
                        insert(articles)
                    }
                }
            }
        })
    }


    fun getMyCountry(): String {
        var locale = Locale.getDefault()
        var country = locale.country
        return country.toLowerCase()
    }
}