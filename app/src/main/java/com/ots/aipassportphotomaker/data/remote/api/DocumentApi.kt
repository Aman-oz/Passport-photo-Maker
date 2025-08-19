package com.ots.aipassportphotomaker.data.remote.api

import com.ots.aipassportphotomaker.data.model.DocumentData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Created by amanullah on 19/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
interface DocumentApi {

    @GET("/movies?&_sort=category,id")
    suspend fun getDocuments(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int,
    ): List<DocumentData>

    @GET("/movies")
    suspend fun getDocuments(@Query("id") movieIds: List<Int>): List<DocumentData>

    @GET("/movies/{id}")
    suspend fun getDocument(@Path("id") movieId: Int): DocumentData

    @GET("/movies")
    suspend fun search(
        @Query("q") query: String,
        @Query("_page") page: Int,
        @Query("_limit") limit: Int,
    ): List<DocumentData>
}