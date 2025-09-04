package com.ots.aipassportphotomaker.di.module

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ots.aipassportphotomaker.di.util.BaseUrl
import com.ots.aipassportphotomaker.data.remote.api.CropImageApi
import com.ots.aipassportphotomaker.data.remote.api.DocumentApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Created by amanullah on 24/07/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BaseUrl.CROP_IMAGE_BASE_URL)
            .build()
    }

    /*@Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)  // Increase timeout for large files
            .writeTimeout(60, TimeUnit.SECONDS) // Increase timeout for large files
            .addInterceptor(loggingInterceptor)
            .build()
    }*/

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0")
                .addHeader("Pragma", "no-cache")
                .addHeader("Expires", "0")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)  // Increase timeout for large files
        .writeTimeout(60, TimeUnit.SECONDS) // Increase timeout for large files

        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    @Singleton
    @Provides
    fun provideCropImageApi(retrofit: Retrofit): CropImageApi {
        return retrofit.create(CropImageApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDocumentApi(retrofit: Retrofit): DocumentApi {
        return retrofit.create(DocumentApi::class.java)
    }
}