package cz.destil.moodsync.core;

import android.os.AsyncTask;

/**
 * AsyncTask running on thread pool.
 *
 * @author David Vávra (david@vavra.me)
 */
public abstract class BaseAsyncTask extends AsyncTask<Void, Void, Void> {

    public void start() {
        this.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Void doInBackground(Void... params) {
        inBackground();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        postExecute();
    }

    public abstract void inBackground();

    public abstract void postExecute();
}
