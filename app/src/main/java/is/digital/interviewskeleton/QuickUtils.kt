package `is`.digital.interviewskeleton

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

internal object QuickUtils {
    const val HOST = "https://api-nodejs-todolist.herokuapp.com"

    //--------- SETTINGS ----------------------
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var sharedPrefsEditor: SharedPreferences.Editor
    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        sharedPrefsEditor = sharedPrefs.edit()
    }

    fun getResponse(activity: Activity, url: String, callback: QuickCallbackInterface) {
        if (BuildConfig.DEBUG) Log.d("DEBUG", "getResponse: $url")
        object : Thread() {
            override fun run() {
                try {
                    val client = OkHttpClient.Builder().build()
                    val request = Request.Builder()
                        .addHeader("Authorization", "Bearer " + BuildConfig.BEARER_TOKEN)
                        .url(url.toHttpUrlOrNull()!!)
                        .build()
                    val response = client.newCall(request).execute()
                    val responseString = response.body!!.string()
                    if (BuildConfig.DEBUG) Log.d("DEBUG", "response: $responseString")
                    activity.runOnUiThread { callback.onSuccess(responseString) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity.runOnUiThread { callback.onError(e) }
                }
            }
        }.start()
    }

    fun getPostResponse(activity: Activity?, url: String, params: JSONObject, callback: QuickCallbackInterface) {
        if (BuildConfig.DEBUG) Log.d("DEBUG", "getPostResponse: $url params: $params")
        object : Thread() {
            override fun run() {
                try {
                    val client = OkHttpClient.Builder()
                        .build()
                    val bodyBuilder = FormBody.Builder()
                    val iter = params.keys()
                    while (iter.hasNext()) {
                        val key = iter.next()
                        bodyBuilder.add(key, params.getString(key))
                    }
                    val body: RequestBody = bodyBuilder.build()
                    val request = Request.Builder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer " + BuildConfig.BEARER_TOKEN)
                        .url(url)
                        .post(body)
                        .build()
                    val response = client.newCall(request).execute()
                    val responseString = response.body!!.string()
                    if (BuildConfig.DEBUG) Log.d("DEBUG", "response: $responseString")
                    if (activity == null) callback.onSuccess(responseString) else {
                        activity.runOnUiThread(Runnable { callback.onSuccess(responseString) })
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (activity == null) callback.onError(e) else {
                        activity.runOnUiThread(Runnable { callback.onError(e) })
                    }
                }
            }
        }.start()
    }

    interface QuickCallbackInterface {
        fun onSuccess(response: String?)
        fun onError(e: Exception)
    }
}
