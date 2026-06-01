package com.example.todolist.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.todolist.data.preferences.UserPreferencesDataStore
import com.example.todolist.data.repository.CategoryRepositoryImpl
import com.example.todolist.data.repository.CompletionLogRepositoryImpl
import com.example.todolist.data.repository.ScoreRepositoryImpl
import com.example.todolist.data.repository.TaskRepositoryImpl
import com.example.todolist.domain.repository.CategoryRepository
import com.example.todolist.domain.repository.CompletionLogRepository
import com.example.todolist.domain.repository.ScoreRepository
import com.example.todolist.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindScoreRepository(
        impl: ScoreRepositoryImpl
    ): ScoreRepository

    @Binds
    @Singleton
    abstract fun bindCompletionLogRepository(
        impl: CompletionLogRepositoryImpl
    ): CompletionLogRepository

    companion object {
        @Provides
        @Singleton
        fun providePreferencesDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> {
            return PreferenceDataStoreFactory.create(
                produceFile = { context.preferencesDataStoreFile("user_prefs") }
            )
        }

        @Provides
        @Singleton
        fun provideUserPreferencesDataStore(
            dataStore: DataStore<Preferences>
        ): UserPreferencesDataStore {
            return UserPreferencesDataStore(dataStore)
        }
    }
}
