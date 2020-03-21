package com.cat.domain.entity

enum class Status {
    RUNNING,
    SUCCESS,
    EMPTY,
    FAILED
}

@Suppress("DataClassPrivateConstructor")
data class State private constructor(
    val status: Status,
    val msg: String? = null) {
    companion object {
        val LOADED = State(Status.SUCCESS)
        val EMPTY = State(Status.EMPTY)
        val LOADING = State(Status.RUNNING)
        fun error(msg: String?) = State(Status.FAILED, msg)
    }
}