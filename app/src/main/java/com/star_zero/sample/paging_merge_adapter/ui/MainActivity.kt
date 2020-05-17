package com.star_zero.sample.paging_merge_adapter.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.MergeAdapter
import com.star_zero.sample.paging_merge_adapter.R
import com.star_zero.sample.paging_merge_adapter.data.repository.paging.NetworkState
import com.star_zero.sample.paging_merge_adapter.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val repoAdapter = RepoAdapter()
    private val loadingAdapter = LoadingAdapter()
    private val retryAdapter = RetryAdapter {
        Timber.d("onRefresh")
        viewModel.retry()
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        val mergeAdapter = MergeAdapter(repoAdapter)
        binding.recycler.adapter = mergeAdapter

        viewModel.repos.observe(this) {
            repoAdapter.submitList(it)
        }
        viewModel.networkState.observe(this) {
            mergeAdapter.removeAdapter(loadingAdapter)
            mergeAdapter.removeAdapter(retryAdapter)
            when (it) {
                is NetworkState.Loading -> {
                    Timber.d("Add LoadingAdapter")
                    mergeAdapter.addAdapter(loadingAdapter)
                }
                is NetworkState.ERROR -> {
                    Timber.d("Add RetryAdapter")
                    mergeAdapter.addAdapter(retryAdapter)
                }
            }
            mergeAdapter.notifyDataSetChanged()
        }
        viewModel.error.observe(this) {
            if (it) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onRefresh() {
        Timber.d("onRefresh")
        viewModel.refresh()
    }
}
