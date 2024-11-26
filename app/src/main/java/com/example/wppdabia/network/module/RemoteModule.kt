package com.example.wppdabia.network.module

import com.example.wppdabia.network.Remote
import com.example.wppdabia.network.RemoteImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides
    @Singleton
    fun provideRemote(): Remote {
        return RemoteImpl()
    }
}