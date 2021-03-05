package com.mandiri.movie.ui.details.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mandiri.movie.R
import com.mandiri.movie.databinding.FragmentMovieOverviewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mandiri.movie.model.data.Image
import com.mandiri.movie.model.data.Review
import com.mandiri.movie.ui.details.media.ImageAdapter
import kotlinx.android.synthetic.main.fragment_movie_media.*
import kotlinx.android.synthetic.main.fragment_movie_overview.*
import kotlinx.android.synthetic.main.fragment_movie_overview.bottom_navigation
import kotlinx.android.synthetic.main.fragment_movie_overview.information_layout
import kotlinx.android.synthetic.main.fragment_movie_overview.loading_error_text_view
import kotlinx.android.synthetic.main.fragment_movie_overview.progress_bar
import kotlinx.android.synthetic.main.fragment_movies_grid.*

class OverviewFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentView: FragmentMovieOverviewBinding
    private lateinit var viewModel: OverviewViewModel
    private val reviewAdapter = ReviewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie_overview, container, false)
        return fragmentView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            OverviewViewModelFactory(activity!!.application, arguments)
        ).get(OverviewViewModel::class.java)
        setupRecyclerViews()
        bottom_navigation.setOnNavigationItemSelectedListener(this)

        observeViewModel()

    }

    private fun setupRecyclerViews() {

        review_recycler_view.layoutManager = LinearLayoutManager(context)
        review_recycler_view.itemAnimator = DefaultItemAnimator()
        review_recycler_view.adapter = reviewAdapter
    }

    private fun observeViewModel() {
        viewModel.currentMovie.observe(viewLifecycleOwner, Observer { movie ->
            movie?.let {
                fragmentView.movie = movie
                // due to some movies not having production countries written
                if (!movie.productionCountryString.isNullOrBlank())
                    production_countries_card_view.visibility = View.VISIBLE
                information_layout.visibility = View.VISIBLE
                loading_error_text_view.visibility = View.GONE
            } ?: run {
                loading_error_text_view.visibility = View.VISIBLE
            }
            progress_bar.visibility = View.GONE
        })

        viewModel.currentReview.observe(viewLifecycleOwner, Observer { review ->
            review?.let {
                fragmentView.review = review
                updateReview(it.results)
                loading_error_text_view.visibility = View.GONE

                // due to some movies not having production countries written

            } ?: run {
                loading_error_text_view.visibility = View.VISIBLE
            }
            progress_bar.visibility = View.GONE
        })

    }


    private fun updateReview(reviewList: List<Review>?) {
        if (!reviewList.isNullOrEmpty()) {
            reviewAdapter.updateReviewList(reviewList)
            review_layout.visibility = View.VISIBLE
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        return viewModel.onNavigationItemSelected(bottom_navigation, menuItem)
    }
}