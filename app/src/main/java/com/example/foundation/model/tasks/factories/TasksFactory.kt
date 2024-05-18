package com.example.foundation.model.tasks.factories

import com.example.foundation.model.tasks.Task
import com.example.foundation.model.tasks.TaskBody

interface TasksFactory {

    fun <T> async(body: TaskBody<T>): Task<T>

}