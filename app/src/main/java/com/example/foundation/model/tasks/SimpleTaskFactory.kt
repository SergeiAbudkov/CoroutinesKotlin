package com.example.foundation.model.tasks

import android.os.Handler
import android.os.Looper
import com.example.foundation.model.ErrorResult
import com.example.foundation.model.FinalResult
import com.example.foundation.model.SuccessResult

private val handler = Handler(Looper.getMainLooper())

class SimpleTaskFactory : TasksFactory {

    override fun <T> async(body: TaskBody<T>): Task<T> {
        return SimpleTask(body)
    }

    class SimpleTask<T>(
        val body: TaskBody<T>
    ) : Task<T> {

        private var thread: Thread? = null
        private var cancelled = false

        override fun await(): T = body()

        override fun enqueue(listener: TaskListener<T>) {
            thread = Thread {
                try {
                    val data = body()
                    publishResult(listener, SuccessResult(data))

                } catch (e: Exception) {
                    publishResult(listener, ErrorResult(e))
                }
            }.apply { start() }
        }

        private fun publishResult(listener: TaskListener<T>, result: FinalResult<T>) {
            handler.post {
                if (cancelled) return@post
                listener(result)
            }
        }

        override fun cancel() {
            cancelled = true
            thread?.interrupt()
            thread = null
        }

    }
}