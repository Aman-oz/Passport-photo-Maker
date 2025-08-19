package com.ots.aipassportphotomaker.data.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by Aman Ullah on 19/08/2025
 */
class DiskExecutor : Executor {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    override fun execute(runnable: Runnable) {
        executor.execute(runnable)
    }
}
