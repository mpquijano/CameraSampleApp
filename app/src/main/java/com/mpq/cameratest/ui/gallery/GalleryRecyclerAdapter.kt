package com.mpq.cameratest.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.mpq.cameratest.R
import com.mpq.cameratest.data.model.ImageFile
import com.mpq.cameratest.utils.MediaStoreUtils

class GalleryRecyclerAdapter(private val context: Context,
    private val images: LiveData<List<ImageFile>>
)
    : RecyclerView.Adapter<GalleryRecyclerAdapter.GalleryCardHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryRecyclerAdapter.GalleryCardHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_gallery_item, parent, false)

        return GalleryCardHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryCardHolder, position: Int) {
        val itemsViewModel = images.value?.get(position)
        // sets the text to the textview from our itemHolder class
        if(itemsViewModel?.path != null){
            try {
                holder.view.setImageURI(MediaStoreUtils(context).getUriFromPath(itemsViewModel.path))
            } catch (e: java.lang.Exception){}
        }
    }

    override fun getItemCount(): Int = images.value?.size ?: 0

    inner class GalleryCardHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: AppCompatImageView = v.findViewById(R.id.imageBox)
        private var imageFile: ImageFile? = null
    }
}