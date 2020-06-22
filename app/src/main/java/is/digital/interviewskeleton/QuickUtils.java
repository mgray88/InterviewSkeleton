package is.digital.interviewskeleton;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class QuickUtils {
    public static final String HOST = "https://api-nodejs-todolist.herokuapp.com";

    //--------- SETTINGS ----------------------
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor sharedPrefsEditor;

    public static void init(Context context) {
        sharedPrefs = context.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    public interface QuickCallbackInterface {
        void onSuccess(String response);

        void onError(Exception e);
    }

    public static void getResponse(final Activity activity, final String url, final QuickCallbackInterface callback) {
        if (BuildConfig.DEBUG)
            Log.d("DEBUG", "getResponse: " + url);
        new Thread() {
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder().build();
                    Request request = new Request.Builder()
                            .addHeader("Authorization", "Bearer " + BuildConfig.BEARER_TOKEN)
                            .url(HttpUrl.parse(url))
                            .build();
                    Response response = client.newCall(request).execute();

                    final String responseString = response.body().string();

                    if (BuildConfig.DEBUG)
                        Log.d("DEBUG", "response: " + responseString);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(responseString);
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        }.start();
    }

    public static void getPostResponse(final Activity activity, final String url, final JSONObject params, final QuickCallbackInterface callback) {
        if (BuildConfig.DEBUG)
            Log.d("DEBUG", "getPostResponse: " + url + " params: " + params);
        new Thread() {
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .build();
                    FormBody.Builder bodyBuilder = new FormBody.Builder();
                    Iterator<String> iter = params.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        bodyBuilder.add(key, params.getString(key));
                    }
                    RequestBody body = bodyBuilder.build();
                    Request request = new Request.Builder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", "Bearer " + BuildConfig.BEARER_TOKEN)
                            .url(url)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    final String responseString = response.body().string();
                    if (BuildConfig.DEBUG)
                        Log.d("DEBUG", "response: " + responseString);

                    if(activity==null)
                        callback.onSuccess(responseString);
                    else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(responseString);
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    if(activity==null)
                        callback.onError(e);
                    else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                }
            }
        }.start();
    }
}
