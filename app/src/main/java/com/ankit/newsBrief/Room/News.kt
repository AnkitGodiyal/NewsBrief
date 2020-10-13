package com.ankit.newsBrief.Room

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.ankit.newsBrief.Model.Source

@Entity(tableName = "tbl_News")
class News(

    @Ignore
    @SerializedName("source")
    var source: Source?= null,

    var name:String? = source?.name,

    @SerializedName("author")
    var author: String?=null,

    @PrimaryKey @NonNull
    @SerializedName("title")
    var title: String="",

    @SerializedName("description")
    var description: String?=null,

    @SerializedName("url")
    var url: String?=null,

    @SerializedName("urlToImage")
    var urlToImage: String?=null,

    @SerializedName("publishedAt")
    var publishedAt: String?=null
) {
}
