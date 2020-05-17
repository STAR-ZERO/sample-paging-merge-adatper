package com.star_zero.sample.paging_merge_adapter.ui

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.star_zero.sample.paging_merge_adapter.data.repository.RepoRepository
import com.star_zero.sample.paging_merge_adapter.data.repository.paging.NetworkState

class MainViewModel : ViewModel() {

    private val repository = RepoRepository()

    private val repoPaging = repository.getRepos("android", PAGE_SIZE)

    val repos = repoPaging.pagedList
    val networkState = repoPaging.networkState

    val refreshing = repoPaging.refreshState.map {
        it == NetworkState.Loading
    }

    val error = MediatorLiveData<Boolean>().apply {
        fun call(state: NetworkState) {
            this.value = state is NetworkState.ERROR
            this.value = false // Clear state
        }
        addSource(repoPaging.networkState) { call(it) }
        addSource(repoPaging.refreshState) { call(it) }
    }

    fun refresh() {
        repoPaging.refresh()
    }

    fun retry() {
        repoPaging.retry()
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
