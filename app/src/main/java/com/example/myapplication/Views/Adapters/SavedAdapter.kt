package com.example.myapplication.Views.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Models.MediaModel
import com.example.myapplication.R
import java.io.File

class SavedAdapter : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {

    private var itemsList: ArrayList<MediaModel>? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(saved: MediaModel) {

            val imageView = itemView.findViewById<ImageView>(R.id.statusImageView)

            Glide.with(itemView.rootView.context).load(saved.path)
                .placeholder(R.drawable.app_dev_icon).into(imageView)

        }
    }

    fun setData(list: ArrayList<MediaModel>) {
        itemsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_save, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = itemsList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val saveItem = itemsList?.get(position)
        if (saveItem != null) {
            holder.bind(saveItem)
        }
    }
}