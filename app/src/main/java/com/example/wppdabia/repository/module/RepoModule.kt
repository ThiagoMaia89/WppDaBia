package com.example.wppdabia.repository.module

import com.example.wppdabia.repository.Repository
import com.example.wppdabia.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    @Singleton
    fun provideRepo(): Repository {
        return RepositoryImpl()
    }
}