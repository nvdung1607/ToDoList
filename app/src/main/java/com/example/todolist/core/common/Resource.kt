package com.example.todolist.core.common

/**
 * A generic class that holds a value with its loading status.
 *
 * @param T The type of data being loaded.
 */
sealed class Resource<T> {
    /**
     * Represents a successful state containing the loaded data.
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Represents an error state with an error message and optional cause.
     */
    data class Error<T>(val message: String, val cause: Throwable? = null) : Resource<T>()

    /**
     * Represents a loading state.
     */
    class Loading<T> : Resource<T>()
}

/**
 * Returns true if the resource is of type [Resource.Success].
 */
val <T> Resource<T>.isSuccess: Boolean
    get() = this is Resource.Success

/**
 * Returns true if the resource is of type [Resource.Error].
 */
val <T> Resource<T>.isError: Boolean
    get() = this is Resource.Error

/**
 * Returns true if the resource is of type [Resource.Loading].
 */
val <T> Resource<T>.isLoading: Boolean
    get() = this is Resource.Loading

/**
 * Returns the data if [Resource.Success], otherwise null.
 */
fun <T> Resource<T>.getOrNull(): T? {
    return (this as? Resource.Success)?.data
}

/**
 * Returns the error message if [Resource.Error], otherwise null.
 */
fun <T> Resource<T>.getErrorOrNull(): String? {
    return (this as? Resource.Error)?.message
}
