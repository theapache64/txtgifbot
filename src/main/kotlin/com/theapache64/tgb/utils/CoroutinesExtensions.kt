package com.theapache64.tgb.utils

import kotlinx.coroutines.runBlocking

fun runBlockingUnit(block: suspend () -> Unit) = runBlocking {
    block()
}