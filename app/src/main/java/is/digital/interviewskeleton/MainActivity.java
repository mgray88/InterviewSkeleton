package is.digital.interviewskeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().setTitle("TODO!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        listView = findViewById(R.id.todo_list);
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.todo_list_item, tasks));
        loadTodos();
    }

    private void loadTodos() {
        String url = QuickUtils.HOST + "/task";
        QuickUtils.getResponse(this, url, new QuickUtils.QuickCallbackInterface() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    if (root.getInt("count") > 0) {
                        JSONArray data = root.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject task = data.getJSONObject(i);
                            tasks.add(task.getString("description"));
                        }
                    }
                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("Network", e.getLocalizedMessage());
            }
        });
    }
}
