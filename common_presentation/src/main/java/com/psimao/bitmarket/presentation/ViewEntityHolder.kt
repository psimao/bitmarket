package com.psimao.bitmarket.presentation

data class ViewEntityHolder<T : ViewEntity>(
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val error: ErrorViewEntity? = null,
    val entity: T? = null
) {

    companion object {

        fun <T : ViewEntity> loading(): ViewEntityHolder<T> =
            ViewEntityHolder(isLoading = true)

        fun <T : ViewEntity> withError(errorEntity: ErrorViewEntity): ViewEntityHolder<T> =
            ViewEntityHolder(hasError = true, error = errorEntity)

        fun <T : ViewEntity> create(entity: T): ViewEntityHolder<T> =
            ViewEntityHolder(entity = entity)
    }
}