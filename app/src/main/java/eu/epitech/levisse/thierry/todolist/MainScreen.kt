package eu.epitech.levisse.thierry.todolist

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_list.*
import java.util.*
import eu.epitech.levisse.thierry.todolist.task_management.EditTask
import eu.epitech.levisse.thierry.todolist.task_management.NewTask
import android.view.Menu


class MainScreen : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>

    lateinit var mDbWorkerThread: DbWorkerThread


    companion object {
        private val TAG = MainScreen::class.qualifiedName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)


        custom_app_title()


        updateRecyclerViewViaDB("SELECT")


        new_task.setOnClickListener {
            startActivity(Intent(this, NewTask::class.java))

        }


        initTasksRecyclerView()
    }

    private fun custom_app_title() {
        supportActionBar!!.displayOptions  = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar)

    }

    private fun initTasksRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.main_recyclerview)

        recyclerView.addOnItemTouchListener(RecyclerTouchListener(this,
                recyclerView, object : ClickListener {

            override fun OnClick(view: View, position: Int) {
                Log.d(TAG, "Single Click on position :" + position)
            }

            override fun OnLongClick(view: View, position: Int) {
                Log.d(TAG, "Long press on position :" + position)
                if (TaskList.getInstance()?.get(position)?.frequence ?: -1 != -1) {
                    val intent = Intent(this@MainScreen, EditTask::class.java)
                    intent.putExtra("task", TaskList.getInstance()?.get(position))
                    startActivity(intent)

                }
            }

        }))


        recyclerView.layoutManager = LinearLayoutManager(this)

        val swipeHandler = initSwipe()

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun initSwipe(): ItemTouchHelper.Callback? {
        return object : SwipeToDeleteCallback(baseContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as TaskAdapter
                Log.d(TAG,"${viewHolder.adapterPosition}")

                // if the line is not an header or a in-progress task, remove it
                if (TaskList.getInstance()?.get(viewHolder.adapterPosition)?.frequence ?: -1 != -1 &&
                        TaskList.getInstance()?.get(viewHolder.adapterPosition)?.done ?: 0 != 0) {
                    val pos = viewHolder.adapterPosition
                    updateRecyclerViewViaDB("DELETE", (TaskList.getInstance()?.get(pos)?.id ?: return).toString())
                    adapter.removeAt(pos)

                }
                else if (TaskList.getInstance()?.get(viewHolder.adapterPosition)?.frequence ?: -1 != -1) {
                    var tasks = TaskList.getInstance()
                    var task = TaskList.getInstance()?.get(viewHolder.adapterPosition)
                    task?.done = 1
                    mDbWorkerThread.postTask(Runnable {
                        AppDatabase.getInstance(this@MainScreen)?.taskDao()?.updateTask(task!!)
                    })
                    TaskList.setInstance(tasks!!)
                    adapter.removeAt(viewHolder.adapterPosition)
                    adapter.addItem(task!!)

                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()

/*        startActivity(Intent(this, MainScreen::class.java))
        this.finish()*/

        updateRecyclerViewViaDB("SELECT")

    }

    fun setHeaders() {
        var tasks = TaskList.getInstance()

        tasks?.add(0, Task(getString(R.string.in_progress), "", Date(0), Date(0), "", -1, -1))

        var i = 1
        var size = tasks?.size ?: return
        while (i <= size) {
            if (i == size || (tasks[i - 1].done == 0 && i < size && tasks[i].done == 1)) {
                tasks?.add(i, Task(getString(R.string.done), "", Date(0), Date(0), "", -1, -1))
                break
            }
            ++i
        }

        TaskList.setInstance(tasks)
    }

    fun updateRecyclerViewViaDB(queryType: String, queryWhere: String = "") {
        mDbWorkerThread = DbWorkerThread(UUID.randomUUID().toString())
        mDbWorkerThread.start()

        recyclerView = findViewById(R.id.main_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val task = Runnable {

            if (queryType.equals("DELETE", true) && queryWhere == "")
                AppDatabase.getInstance(this)?.taskDao()?.deleteTasks()
            else if (queryType.equals("DELETE", true))
                AppDatabase.getInstance(this)?.taskDao()?.deleteTasks(queryWhere)
            TaskList.setInstance(AppDatabase.getInstance(this)?.taskDao()?.getAllTasks() ?: return@Runnable)
            setHeaders()


            runOnUiThread {
                adapter = TaskAdapter(TaskList.getInstance()!!)
                recyclerView.adapter = adapter
            }
        }

        mDbWorkerThread.postTask(task)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_delete_all -> {

                updateRecyclerViewViaDB("DELETE")
                val adapter = recyclerView.adapter as TaskAdapter
                adapter.removeAll()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
