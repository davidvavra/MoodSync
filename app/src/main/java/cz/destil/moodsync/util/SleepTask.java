package cz.destil.moodsync.util;

import cz.destil.moodsync.core.BaseAsyncTask;

/**
 * AsyncTack which only sleeps
 *
 * @author David Vávra (david@vavra.me)
 */
public class SleepTask extends BaseAsyncTask {

    private int delay;
    private Listener listener;

    public SleepTask(int delay, Listener listener) {
        this.delay = delay;
        this.listener = listener;
    }

    @Override
    public void inBackground() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void postExecute() {
        listener.awoken();
    }

    public interface Listener {
        public void awoken();
    }
}
