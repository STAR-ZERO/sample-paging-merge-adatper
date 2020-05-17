package com.star_zero.sample.paging_merge_adapter.data.repository

import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.star_zero.sample.paging_merge_adapter.data.api.GitHubService
import com.star_zero.sample.paging_merge_adapter.data.model.Repo
import com.star_zero.sample.paging_merge_adapter.data.repository.paging.PagingState
import com.star_zero.sample.paging_merge_adapter.data.repository.paging.RepoDataSourceFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RepoRepository {

    private val gitHubService: GitHubService

    init {
        val okhttp = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            })
            .build()

        val retrofit = Retrofit.Builder()
            .client(okhttp)
            .baseUrl("https://api.github.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        gitHubService = retrofit.create(GitHubService::class.java)
    }

    fun getRepos(user: String, pageSize: Int): PagingState<Repo> {
        val sourceFactory = RepoDataSourceFactory(gitHubService, user)
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(pageSize)
            .setPageSize(pageSize)
            .build()

        return PagingState(
            pagedList = sourceFactory.toLiveData(config),
            networkState = sourceFactory.sourceLiveData.switchMap {
                it.networkState
            },
            refreshState = sourceFactory.sourceLiveData.switchMap {
                it.refreshState
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retry()
            }
        )
    }
}
