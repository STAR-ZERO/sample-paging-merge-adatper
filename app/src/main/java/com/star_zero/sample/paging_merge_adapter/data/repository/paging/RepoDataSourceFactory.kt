package com.star_zero.sample.paging_merge_adapter.data.repository.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.star_zero.sample.paging_merge_adapter.data.api.GitHubService
import com.star_zero.sample.paging_merge_adapter.data.model.Repo

class RepoDataSourceFactory(
    private val gitHubService: GitHubService,
    private val user: String
): DataSource.Factory<Int, Repo>() {

    val sourceLiveData = MutableLiveData<RepoPageKeyedDataSource>()

    override fun create(): DataSource<Int, Repo> {
        val source = RepoPageKeyedDataSource(gitHubService, user)
        sourceLiveData.postValue(source)
        return source
    }
}
