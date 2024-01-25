package com.dhilder.photogallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhilder.photogallery.databinding.PhotoItemBinding
import com.dhilder.photogallery.domain.model.PhotoMetadata
import com.squareup.picasso.Picasso

class PhotoAdapter(
    private val metadataList: List<PhotoMetadata>,
    private val onClickListener: OnClickListener
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
            setTags(itemViewModel)

            setOnClickListeners(itemViewModel)
        }

        private fun setTags(itemViewModel: PhotoMetadata) {
            if (itemViewModel.tags.isNotEmpty()) {
                binding.imageTag.text = binding.root.resources.getString(
                    R.string.tags,
                    itemViewModel.tags
                )
                binding.imageTag.visibility = View.VISIBLE
            } else {
                binding.imageTag.visibility = View.GONE
            }
        }

        private fun setOnClickListeners(itemViewModel: PhotoMetadata) {
            binding.imageView.setOnClickListener {
                onClickListener.onImageClick(itemViewModel)
            }

            binding.userLayout.setOnClickListener {
                onClickListener.onUserClick(itemViewModel.owner)
            }
        }
    }

    interface OnClickListener {
        fun onImageClick(metadata: PhotoMetadata)

        fun onUserClick(owner: String)
    }
}
