package eu.epitech.levisse.thierry.todolist

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import java.io.File

/**
 * Created by thierry on 31/01/18.
 */

class DbWorkerThread(threadName: String): HandlerThread(threadName) {

    private lateinit var mWorkerHandler: Handler

    override fun onLooperPrepared() {
        super.onLooperPrepared()

        mWorkerHandler = Handler(looper)
    }

    fun postTask(task: Runnable) {
        if (!::mWorkerHandler.isInitialized)
            mWorkerHandler = Handler(looper)
        mWorkerHandler.post(task)
    }

    companion object {
        private var thread: DbWorkerThread? = null

        fun getThreads(threadName: String): DbWorkerThread? {
            if (thread == null) {
                synchronized(AppDatabase::class) {
                    thread = DbWorkerThread(threadName)
                }
                thread?.start()
            }
            return thread
        }
    }
}