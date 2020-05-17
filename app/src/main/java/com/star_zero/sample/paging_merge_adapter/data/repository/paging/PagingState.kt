package com.star_zero.sample.paging_merge_adapter.data.repository.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class PagingState<T>(
    val pagedList: LiveData<PagedList<T>>,
    val networkState: LiveData<NetworkState>,
    val refreshState: LiveData<NetworkState>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)
