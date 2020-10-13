package com.ankit.newsBrief.View

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ankit.newsBrief.R


class DetailWebView : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_web_view)

        val intent=intent

        val webView = findViewById<WebView>(R.id.webView)
        val loader = findViewById<ProgressBar>(R.id.webViewLoader)
        val cvInternetCnnection=findViewById<CardView>(R.id.cvNoConnectionLayout)

        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.webViewClient = WebViewClient()
        if(isNetworkAvailable()){
        webView.loadUrl(intent.getStringExtra("url"))
        if (webView.isShown) {
            loader.visibility = View.INVISIBLE
        }}
        else
        {
            cvInternetCnnection.visibility=View.VISIBLE
            loader.visibility=View.INVISIBLE
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
