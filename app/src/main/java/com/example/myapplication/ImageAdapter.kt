package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.ArrayList

private val onImageSelected: ((String) -> Unit)? = null

class ImageAdapter(private var context: Context,private val imageList: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val selectedImages = mutableSetOf<String>()
    private var selectedImageList: ArrayList<String> = ArrayList()

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val isSelectIcon: ImageView = itemView.findViewById(R.id.is_Select_Btn)

        fun bind(imagePath: String) {
            //itemView.isSelected = selectedImages.contains(imagePath)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = imageList[position]
        holder.bind(imagePath)

        Glide.with(holder.itemView.context)
            .load(imagePath)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            if (selectedImageList.contains(imagePath)) {
                selectedImageList.remove(imagePath)
                holder.isSelectIcon.visibility = View.INVISIBLE
                Toast.makeText(context, "images removed $imagePath", Toast.LENGTH_SHORT).show()
            } else {
                selectedImageList.add(imagePath)
                holder.isSelectIcon.visibility = View.VISIBLE
                Toast.makeText(context, "images Added $imagePath", Toast.LENGTH_SHORT).show()
            }
            notifyItemChanged(position)
            onImageSelected?.invoke(imagePath)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
