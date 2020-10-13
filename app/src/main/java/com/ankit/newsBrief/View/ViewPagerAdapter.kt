package com.ankit.newsBrief.View

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ankit.newsBrief.R
import com.ankit.newsBrief.Room.News
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ViewPagerAdapter(var context: Context) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    var list = emptyList<News>()

    inner class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.ivImage)
        var tvTitle: TextView = itemView.findViewById(R.id.tvHeading)
        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var tvReadMore: TextView = itemView.findViewById(R.id.tvReadMore)

        fun bindData(news: News) {
            tvTitle.text = news.title
            tvDescription.text = news.description
            CoroutineScope(Dispatchers.Default).launch {
                val result = async { Picasso.with(context).load(news.urlToImage) }.await()
                withContext(Dispatchers.Main) {
                    result.fit().into(imageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)
        return PagerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindData(list.get(position))
        holder.tvReadMore.setOnClickListener {
            openNewsDetail(position)
        }
    }

    public fun openNewsDetail(position: Int) {
        list.get(position).apply {
            val intent = Intent(context, DetailWebView::class.java)
            intent.putExtra("title", title)
            intent.putExtra("source", name)
            intent.putExtra("time", dateTime(publishedAt))
            intent.putExtra("desc", description)
            intent.putExtra("imageUrl", urlToImage)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    fun getCountry(): String? {
        val locale = Locale.getDefault()
        val country = locale.country
        return country.toLowerCase()
    }

    fun dateTime(t: String?): String? {
        val prettyTime = PrettyTime(Locale(getCountry()))
        var time: String? = null
        try {
            val simpleDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:", Locale.ENGLISH)
            val date = simpleDateFormat.parse(t)
            time = prettyTime.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return time
    }

    fun updateList(list: List<News>) {
        this.list = list
        (this.list as MutableList).reverse()
        notifyDataSetChanged()
    }
}