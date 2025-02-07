package com.ifpe_4_ads.camif.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ifpe_4_ads.camif.Constants
import com.ifpe_4_ads.camif.R
import java.io.File

class ImagesCustomAdapter (private val imagesList: List<String>, private val onItemClick: (String) -> Unit): RecyclerView.Adapter<ImagesCustomAdapter.ImagesViewHolder>() {
    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.img_image)
        val imageTitle: TextView = itemView.findViewById(R.id.text_image)

        fun bind(photoName: String, clickListener: (String) -> Unit) {
            itemView.setOnClickListener {
                clickListener(photoName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_display, parent, false)
        return ImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val currentImg = imagesList[position]
        val imgPath = Constants.FILE_PATH+currentImg

        Glide.with(holder.itemView.context)
            .load(File(imgPath))
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Ignorar o cache de disco
            .skipMemoryCache(true)
            .centerCrop()
            .into(holder.image)
        holder.imageTitle.text = currentImg

        holder.bind(currentImg, onItemClick)

    }

}