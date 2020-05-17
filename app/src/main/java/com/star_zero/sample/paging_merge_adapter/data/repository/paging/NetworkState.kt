package com.star_zero.sample.paging_merge_adapter.data.repository.paging

sealed class NetworkState {
    object Loading: NetworkState()
    object SUCCESS: NetworkState()

    data class ERROR(
        val e: Exception
    ): NetworkState()
}
