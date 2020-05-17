package com.star_zero.sample.paging_merge_adapter.data.api

import com.star_zero.sample.paging_merge_adapter.data.model.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("/users/{user}/repos")
    fun repos(
        @Path("user") user: String,
        @Query("page") page: Int,
        @Query("per_page") prePage: Int
    ): Call<List<Repo>>
}
