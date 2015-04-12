package cz.destil.moodsync.light;

import cz.destil.moodsync.R;
import cz.destil.moodsync.core.App;
import cz.destil.moodsync.core.Config;
import cz.destil.moodsync.event.LocalColorEvent;
import cz.destil.moodsync.util.SleepTask;

/**
 * Thread which switches material colors.
 *
 * @author David Vávra (david@vavra.me)
 */
public class LocalColorSwitcher {
    private static LocalColorSwitcher sInstance;
    private final int[] COLORS = {R.color.material_indigo, R.color.material_blue, R.color.material_light_blue, R.color.material_cyan,
            R.color.material_teal, R.color.material_green, R.color.material_light_green, R.color.material_lime, R.color.material_yellow,
            R.color.material_amber, R.color.material_orange, R.color.material_deep_orange, R.color.material_red, R.color.material_pink,
            R.color.material_purple, R.color.material_deep_purple};
    private boolean mRunning = false;
    private int mCurrentColor;
    private int mPreviousColor = COLORS.length - 1;
    SleepTask mSleepTask;

    public static LocalColorSwitcher get() {
        if (sInstance == null) {
            sInstance = new LocalColorSwitcher();
        }
        return sInstance;
    }

    public void start() {
        mRunning = true;
        changeColor();
    }

    private void changeColor() {
        if (mRunning) {
            mSleepTask = new SleepTask(Config.DURATION_OF_COLOR_CHANGE, new SleepTask.Listener() {
                @Override
                public void awoken() {
                    if (mRunning) {
                        App.bus().post(new LocalColorEvent(App.get().getResources().getColor(COLORS[mPreviousColor]),
                                App.get().getResources().getColor(COLORS[mCurrentColor])));
                        mPreviousColor = mCurrentColor;
                        mCurrentColor++;
                        if (mCurrentColor >= COLORS.length) {
                            mCurrentColor = 0;
                        }
                        changeColor();
                    }
                }
            });
            mSleepTask.start();
        }
    }

    public void stop() {
        mRunning = false;
        if (mSleepTask != null) {
            mSleepTask.cancel(true);
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    public int getPreviousColor() {
        return App.get().getResources().getColor(COLORS[mPreviousColor]);
    }
}
