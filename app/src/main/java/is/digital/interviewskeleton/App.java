package is.digital.interviewskeleton;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}
