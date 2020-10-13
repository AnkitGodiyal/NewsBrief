package com.ankit.newsBrief.View

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.ankit.newsBrief.R
import com.ankit.newsBrief.ViewModel.NewsViewModel


class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager2
    lateinit var newsViewModel: NewsViewModel
    lateinit var cvNoInternet: CardView
    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var clMain: ConstraintLayout
    var viewPagerAdapter = ViewPagerAdapter(this)
    lateinit var onSwipeTouchListener: OnSwipeListner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setViewPager()
        apiSilentHit()
        swipeRefresh.setOnRefreshListener {
            apiSilentHit()
        }

        viewPager.setOnTouchListener(onSwipeTouchListener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }


    private fun apiSilentHit() {
        newsViewModel.makeSilentApiHit()
    }

    private fun setViewPager() {
        viewPager.adapter = viewPagerAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        // for left swipe change translationY to translationX and remove above line
        val transformer = ViewPager2.PageTransformer { page, position ->
            page.apply {
                val pageWidth = width
                val MIN_SCALE = 0.75f
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 0 -> { // [-1,0]
                        // Use the default slide transition when moving to the left page
                        alpha = 1f
                        translationY = 0f
                        translationZ = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                    position <= 1 -> { // (0,1]
                        // Fade the page out.
                        alpha = 1 - position

                        // Counteract the default slide transition
                        translationY = pageWidth * -position
                        // Move it behind the left page
                        translationZ = -1f

                        // Scale the page down (between MIN_SCALE and 1)
                        val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)))
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }

        viewPager.setPageTransformer(transformer)
        viewPager.overScrollMode = View.OVER_SCROLL_NEVER

        newsViewModel.newsLiveData.observe(this, Observer { newsList ->

            newsList?.let {
                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = false
                    if (newsList?.size == viewPagerAdapter.itemCount) {
                        Toast.makeText(this, "Feed up to date", Toast.LENGTH_SHORT).show()
                    }
                }
                if (newsList.isEmpty() && !isNetworkAvailable()) {
                    cvNoInternet.visibility = View.VISIBLE
                } else {
                    viewPagerAdapter.updateList(newsList)
                }
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun init() {
        viewPager = findViewById(R.id.viewPager)
        cvNoInternet = findViewById(R.id.cvNoInternetLayout)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        clMain = findViewById(R.id.clMain)
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        onSwipeTouchListener = object : OnSwipeListner(this) {
            override fun onSwipeLeft() {
                viewPagerAdapter.openNewsDetail(viewPager.currentItem)
            }
        }
    }
}
