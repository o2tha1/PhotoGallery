package com.dhilder.photogallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhilder.photogallery.databinding.PhotoItemBinding
import com.dhilder.photogallery.domain.model.PhotoMetadata
import com.squareup.picasso.Picasso

class PhotoAdapter(
    private val metadataList: List<PhotoMetadata>,
    private val onImageClickListener: OnImageClickListener
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.ViewHolder {
        val view = PhotoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoAdapter.ViewHolder, position: Int) {
        holder.bind(metadataList[position])
    }

    override fun getItemCount() = metadataList.size

    inner class ViewHolder(private val binding: PhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemViewModel: PhotoMetadata) {
            Picasso.get().load(itemViewModel.url_l).into(binding.imageView)
            Picasso.get().load(itemViewModel.getBuddyIcons()).into(binding.userIcon)

            binding.userId.text = itemViewModel.owner
            binding.imageView.setOnClickListener {
                onImageClickListener.onImageClick(itemViewModel.url_l)
            }
        }
    }

    interface OnImageClickListener {
        fun onImageClick(url: String)
    }
}
