package com.example.todolist.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // Repository bindings will be defined here using @Binds once implementations are created.
    // Example:
    // @Binds
    // @Singleton
    // abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}
