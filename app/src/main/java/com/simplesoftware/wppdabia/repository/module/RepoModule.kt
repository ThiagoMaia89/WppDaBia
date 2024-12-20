package com.simplesoftware.wppdabia.repository.module

import com.simplesoftware.wppdabia.repository.Repository
import com.simplesoftware.wppdabia.repository.RepositoryImpl
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