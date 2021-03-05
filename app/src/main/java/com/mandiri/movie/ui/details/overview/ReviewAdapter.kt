package com.mandiri.movie.ui.details.overview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mandiri.movie.R
import com.mandiri.movie.databinding.ItemReviewBinding
import com.mandiri.movie.model.data.Review
import java.util.*

class ReviewAdapter: RecyclerView.Adapter<ReviewAdapter.ViewHolder>(){

    private val reviewList: MutableList<Review> = ArrayList()

    fun updateReviewList(list: List<Review>) {

        reviewList.clear()
        reviewList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: ItemReviewBinding = DataBindingUtil.inflate(inflater, R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(reviewList[position])
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ViewHolder(itemView: ItemReviewBinding): RecyclerView.ViewHolder(itemView.root){
        private val view = itemView

        fun onBind(review: Review){
            view.review = review
        }
    }
}