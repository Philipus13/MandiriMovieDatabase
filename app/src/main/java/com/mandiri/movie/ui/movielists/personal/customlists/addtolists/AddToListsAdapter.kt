package com.mandiri.movie.ui.movielists.personal.customlists.addtolists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mandiri.movie.R
import com.mandiri.movie.databinding.ItemCustomListToAddBinding
import com.mandiri.movie.model.data.CustomMovieList
import kotlinx.android.synthetic.main.item_custom_list_to_add.view.*

class AddToListsAdapter(
    private val listener: ListCheckedListener,
    private val movieLists: List<CustomMovieList>
) : RecyclerView.Adapter<AddToListsAdapter.ViewHolder>() {

    interface ListCheckedListener {
        fun onListChecked(movieList: CustomMovieList, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: ItemCustomListToAddBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_custom_list_to_add, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listener, movieLists[position])
    }

    override fun getItemCount(): Int {
        return movieLists.size
    }

    inner class ViewHolder(itemView: ItemCustomListToAddBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private val view = itemView

        fun onBind(listener: ListCheckedListener, movieList: CustomMovieList) {
            view.movieList = movieList

            view.root.information_layout.setOnClickListener {
                view.root.list_checkbox.isChecked = !view.root.list_checkbox.isChecked
                listener.onListChecked(movieList, view.root.list_checkbox.isChecked)
            }

            view.root.list_checkbox.setOnCheckedChangeListener { _, isChecked ->
                listener.onListChecked(
                    movieList,
                    isChecked
                )
            }
        }
    }
}