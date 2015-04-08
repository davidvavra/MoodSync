package cz.destil.moodsync.light;

import android.graphics.Color;

import cz.destil.moodsync.R;
import cz.destil.moodsync.core.App;
import cz.destil.moodsync.core.BaseAsyncTask;
import cz.destil.moodsync.core.Config;
import cz.destil.moodsync.event.ErrorEvent;
import cz.destil.moodsync.event.SuccessEvent;
import cz.destil.moodsync.util.Toas;
import lifx.java.android.client.LFXClient;
import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.entities.LFXTypes;
import lifx.java.android.light.LFXTaggedLightCollection;
import lifx.java.android.network_context.LFXNetworkContext;

/**
 * Controller which controls LIFX lights.
 *
 * @author David Vávra (david@vavra.me)
 */
public class LightsController {

    private static final int TIMEOUT = 5000;
    private static LightsController sInstance;
    private LFXNetworkContext mNetworkContext;
    private boolean mWorkingFine;
    private boolean mDisconnected;

    public static LightsController get() {
        if (sInstance == null) {
            sInstance = new LightsController();
        }
        return sInstance;
    }

    public void changeColor(int color) {
        if (mWorkingFine) {
            mNetworkContext.getAllLightsCollection().setColorOverDuration(convertColor(color), Config.DURATION_OF_COLOR_CHANGE);
        }
    }

    public void init() {
        mWorkingFine = false;
        mDisconnected = false;
        mNetworkContext = LFXClient.getSharedInstance(App.get()).getLocalNetworkContext();
        mNetworkContext.addNetworkContextListener(new LFXNetworkContext.LFXNetworkContextListener() {
            @Override
            public void networkContextDidConnect(LFXNetworkContext networkContext) {
                mDisconnected = false;
            }

            @Override
            public void networkContextDidDisconnect(LFXNetworkContext networkContext) {
                if (!mDisconnected) {
                    mWorkingFine = false;
                    Toas.t(R.string.lifx_disconnected);
                    App.bus().post(new ErrorEvent(R.string.lifx_disconnected));
                }
            }

            @Override
            public void networkContextDidAddTaggedLightCollection(LFXNetworkContext networkContext, LFXTaggedLightCollection collection) {
                App.bus().post(new SuccessEvent());
                mWorkingFine = true;
                mNetworkContext.getAllLightsCollection().setPowerState(LFXTypes.LFXPowerState.ON);
            }

            @Override
            public void networkContextDidRemoveTaggedLightCollection(LFXNetworkContext networkContext, LFXTaggedLightCollection collection) {
            }
        });
    }

    public void start() {
        mNetworkContext.connect();
        if (!mWorkingFine) {
            new TimeoutTask().start();
        } else {
            App.bus().post(new SuccessEvent());
        }
    }

    public void stop() {
        mDisconnected = true;
        if (mNetworkContext != null && mWorkingFine) {
            mNetworkContext.disconnect();
        }
    }

    private LFXHSBKColor convertColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return LFXHSBKColor.getColor(hsv[0], hsv[1], Config.LIFX_BRIGHTNESS, 3500);
    }

    public void signalStop() {
        int color = App.get().getResources().getColor(android.R.color.white);
        mNetworkContext.getAllLightsCollection().setColorOverDuration(convertColor(color), 100);
    }

    class TimeoutTask extends BaseAsyncTask {

        @Override
        public void inBackground() {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException ignored) {
            }
        }

        @Override
        public void postExecute() {
            int numLights = mNetworkContext.getAllLightsCollection().getLights().size();
            if (numLights == 0) {
                App.bus().post(new ErrorEvent(R.string.no_lights_found));
            }
        }
    }
}
