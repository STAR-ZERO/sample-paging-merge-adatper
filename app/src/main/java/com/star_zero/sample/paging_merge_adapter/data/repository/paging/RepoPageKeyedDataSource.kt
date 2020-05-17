package com.star_zero.sample.paging_merge_adapter.data.repository.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.star_zero.sample.paging_merge_adapter.data.api.GitHubService
import com.star_zero.sample.paging_merge_adapter.data.model.Repo
import timber.log.Timber
import java.util.concurrent.Executors

class RepoPageKeyedDataSource(
    private val gitHubService: GitHubService,
    private val user: String
) : PageKeyedDataSource<Int, Repo>() {

    private var retry: (() -> Unit)? = null

    val refreshState = MutableLiveData<NetworkState>()
    val networkState = MutableLiveData<NetworkState>()

    fun retry() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            Executors.newSingleThreadExecutor().execute {
                it.invoke()
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Repo>
    ) {
        Timber.d("loadInitial")
        fetch(1, params.requestedLoadSize,
            { repos, next ->
                callback.onResult(repos, null, next)
            },
            {
                refreshState.postValue(it)
            },
            {
                loadInitial(params, callback)
            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Repo>) {
        Timber.d("loadAfter")
        fetch(params.key, params.requestedLoadSize,
            { repos, next ->
                callback.onResult(repos, next)
            },
            {
                networkState.postValue(it)
            },
            {
                loadAfter(params, callback)
            }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Repo>) {
        // do nothing
    }

    private fun fetch(
        page: Int,
        pageSize: Int,
        callback: (repos: List<Repo>, next: Int?) -> Unit,
        changeState: (NetworkState) -> Unit,
        retryFetch: () -> Unit
    ) {
        changeState(NetworkState.Loading)

        try {
            val response = gitHubService.repos(user, page, pageSize).execute()
            val repos = response.body()
            if (repos != null) {
                var next: Int? = null
                // Check if there is next page
                response.headers()["Link"]?.let { link ->
                    val regex = Regex("rel=\"next\"")
                    if (regex.containsMatchIn(link)) {
                        next = page + 1
                    }
                }
                callback(repos, next)
            } else {
                callback(emptyList(), null)
            }

            changeState(NetworkState.SUCCESS)
        } catch (e: Exception) {
            Timber.w(e)
            retry = {
                retryFetch()
            }
            changeState(NetworkState.ERROR(e))
        }
    }
}
