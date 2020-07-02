package `is`.digital.interviewskeleton

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private val tasks = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        supportActionBar?.title = "TODO!"
    }

    override fun onResume() {
        super.onResume()
        listView = findViewById(R.id.todo_list)
        listView.adapter = ArrayAdapter(this, R.layout.todo_list_item, tasks)
        loadTodos()
    }

    private fun loadTodos() {
        val url = QuickUtils.HOST + "/task"
        QuickUtils.getResponse(this, url, object : QuickUtils.QuickCallbackInterface {
            override fun onSuccess(response: String?) {
                try {
                    val root = JSONObject(response)
                    if (root.getInt("count") > 0) {
                        val data = root.getJSONArray("data")
                        for (i in 0 until data.length()) {
                            val task = data.getJSONObject(i)
                            tasks.add(task.getString("description"))
                        }
                    }
                    (listView.adapter as BaseAdapter).notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onError(e: Exception) {
                Log.d("Network", e.localizedMessage)
            }
        })
    }
}
