package com.example.todolist.di

import android.content.Context
import androidx.room.Room
import com.example.todolist.core.common.Constants
import com.example.todolist.data.local.dao.CategoryDao
import com.example.todolist.data.local.dao.CompletionLogDao
import com.example.todolist.data.local.dao.ScoreDao
import com.example.todolist.data.local.dao.TaskDao
import com.example.todolist.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideCompletionLogDao(database: AppDatabase): CompletionLogDao {
        return database.completionLogDao()
    }

    @Provides
    @Singleton
    fun provideScoreDao(database: AppDatabase): ScoreDao {
        return database.scoreDao()
    }
}
