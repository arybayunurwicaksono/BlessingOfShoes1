package com.example.blessingofshoes_1.di

import android.app.Application
import com.example.blessingofshoes_1.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideMealRepository(application: Application) = AppRepository(application)
}