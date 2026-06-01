package com.example.todolist.domain.repository

import com.example.todolist.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing categories.
 */
interface CategoryRepository {
    /**
     * Observes all categories, ordered alphabetically by name.
     */
    fun observeAllCategories(): Flow<List<Category>>

    /**
     * Retrieves a category by its ID. Returns null if not found.
     */
    suspend fun getCategoryById(id: String): Category?

    /**
     * Checks if a category with the specified name already exists.
     */
    suspend fun isNameExists(name: String): Boolean

    /**
     * Creates a new category in the database.
     */
    suspend fun createCategory(category: Category)

    /**
     * Updates an existing category in the database.
     */
    suspend fun updateCategory(category: Category)

    /**
     * Deletes a category from the database.
     */
    suspend fun deleteCategory(category: Category)
}
