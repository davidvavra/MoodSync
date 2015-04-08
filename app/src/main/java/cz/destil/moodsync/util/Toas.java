package cz.destil.moodsync.util;

import android.widget.Toast;

import cz.destil.moodsync.core.App;

/**
 * Util class for toasts.
 *
 * @author David Vávra (david@vavra.me)
 */
public class Toas {
    public static void t(String text) {
        Toast.makeText(App.get(), text, Toast.LENGTH_LONG).show();
    }

    public static void t(int resId) {
        t(App.get().getString(resId));
    }
}
