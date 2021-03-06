package com.mandiri.movie.ui.discover

import android.os.Bundle
import android.view.*

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mandiri.movie.R
import com.mandiri.movie.model.data.Category
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.android.synthetic.main.fragment_discover.loading_error_text_view
import kotlinx.android.synthetic.main.fragment_discover.progress_bar

class DiscoverFragment : Fragment(), CategoryRecyclerView.AppBarTracking,
    MenuItem.OnMenuItemClickListener {

    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var viewModel: DiscoverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // needed because toolbar is changed
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DiscoverViewModel::class.java)

        categories_recycler_view.layoutManager = LinearLayoutManager(context)
        setupSlider()
        setupToolbar()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let {
                categoryAdapter = CategoryAdapter(it)
                categories_recycler_view.adapter = categoryAdapter
                categories_recycler_view.visibility = View.VISIBLE
                categoryAdapter.setChildClickListener { _, checked, group, childIndex ->
                    viewModel.onSubcategorySelected(checked, group as Category, childIndex)
                }
                loading_error_text_view.visibility = View.GONE
            } ?: run { loading_error_text_view.visibility = View.VISIBLE }
            progress_bar.visibility = View.GONE
            ViewCompat.setNestedScrollingEnabled(categories_recycler_view, false)
        })
    }

    private fun setupSlider() {
        release_year_slider.setOnThumbValueChangeListener { multiSlider, _, thumbIndex, value ->
            if (thumbIndex == 0)
                if (value != multiSlider.min)
                    release_year_slider_min_value.text = value.toString()
                else release_year_slider_min_value.text = "???"
            if (thumbIndex == 1) {
                release_year_slider_max_value.text = value.toString()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_confirm) {
            viewModel.onConfirmSelectionClicked(
                view!!,
                release_year_slider_min_value.text.toString(),
                release_year_slider_max_value.text.toString()
            )
        }
        return true
    }

    // --------- Toolbar functionality ------------//

    private var appBarOffset: Int = 0
    private var isAppBarIdle = false

    private var isExpanded: Boolean = false

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_search).isVisible = false
        val confirmItem = menu.findItem(R.id.action_confirm)
        confirmItem.isVisible = true
        confirmItem.setOnMenuItemClickListener(this)
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        (activity as AppCompatActivity).setSupportActionBar(collapsing_toolbar)
        activity!!.invalidateOptionsMenu()

        app_bar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                appBarOffset = verticalOffset
                setToolbarArrowRotation(verticalOffset, appBarLayout)
                isExpanded = verticalOffset == 0
                // check beyond offset points to be safer
                isAppBarIdle = appBarOffset >= 0 || appBarOffset <= -app_bar.totalScrollRange
                if (isAppBarIdle)
                    setExpandAndCollapseEnabled(isExpanded)
            })
        categories_recycler_view.setAppBarTracking(this)

        expand_collapse_btn.setOnClickListener {
            isExpanded = !isExpanded
            app_bar.setExpanded(isExpanded, true)        }
    }

    private fun setToolbarArrowRotation(verticalOffset: Int, appBarLayout: AppBarLayout) {
        // get percent of progress for scrolling done
        // current offset / positive max offset
        val progress = (-verticalOffset).toFloat() / appBarLayout.totalScrollRange
        arrow_image_view.rotation = 180 + progress * 180
    }

    private fun setExpandAndCollapseEnabled(enabled: Boolean) {
        categories_recycler_view.isNestedScrollingEnabled = enabled
    }

    override fun isAppBarExpanded(): Boolean = appBarOffset == 0
    override fun isAppBarIdle(): Boolean = isAppBarIdle
}