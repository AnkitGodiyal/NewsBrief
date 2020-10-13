package com.ankit.newsBrief.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ankit.newsBrief.Room.News
import com.ankit.newsBrief.Room.NewsDatabase
import com.ankit.newsBrief.Room.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewsViewModel(var app: Application) : AndroidViewModel(app) {

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope= CoroutineScope(coroutineContext)

    private val newsRepository: NewsRepository
    val newsLiveData:LiveData<List<News>>

    init{
        val newsDao = NewsDatabase.getDatabase(app)!!.newsDao()
        newsRepository=NewsRepository(newsDao)
        newsLiveData=newsRepository.newsLiveData
    }

    fun insert(news:List<News>) = scope.launch(Dispatchers.IO) {
        newsRepository.insert(news)
    }

    fun makeSilentApiHit(){
        newsRepository.fetchDataFromApi();
    }

}
