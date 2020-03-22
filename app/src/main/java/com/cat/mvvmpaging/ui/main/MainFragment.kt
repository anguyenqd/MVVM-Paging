package com.cat.mvvmpaging.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cat.domain.entity.State
import com.cat.mvvmpaging.R
import com.cat.mvvmpaging.ui.main.adapters.PostFeedAdapter
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainFragment : Fragment() {

    private val listViewModel: MainViewModel by viewModel()

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rl_post_list.adapter = PostFeedAdapter()

        val dividerItemDecoration = DividerItemDecoration(
            this.requireContext(),
            (rl_post_list.layoutManager as LinearLayoutManager).orientation
        )
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.list_devider
            )!!
        )

        rl_post_list.addItemDecoration(dividerItemDecoration)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.listViewModel.postsLiveData.observe(viewLifecycleOwner, Observer {
            (rl_post_list.adapter as PostFeedAdapter).submitList(it)
        })

        this.listViewModel.networkState.observe(viewLifecycleOwner, Observer {
            // You can switch UI between loading/empty/error and finish state
            Timber.d("Network state $it")
        })

        this.listViewModel.onLoad()
    }

}
