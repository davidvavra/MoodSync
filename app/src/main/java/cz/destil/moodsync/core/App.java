package cz.destil.moodsync.core;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Main application object.
 *
 * @author David Vávra (david@vavra.me)
 */
public class App extends Application {

    static App sInstance;
    static Bus sBus;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sBus = new Bus();
    }

    public static App get() {
        return sInstance;
    }

    public static Bus bus() {
        return sBus;
    }
}
