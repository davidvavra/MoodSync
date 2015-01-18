package cz.destil.moodsync.light;

import android.graphics.Color;

import cz.destil.moodsync.core.App;
import cz.destil.moodsync.core.Config;
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

    private static LightsController sInstance;
    private LFXNetworkContext mNetworkContext;

    public static LightsController get() {
        if (sInstance == null) {
            sInstance = new LightsController();
        }
        return sInstance;
    }

    public void init() {
        mNetworkContext = LFXClient.getSharedInstance(App.get()).getLocalNetworkContext();
        mNetworkContext.addNetworkContextListener(new LFXNetworkContext.LFXNetworkContextListener() {
            @Override
            public void networkContextDidConnect(LFXNetworkContext networkContext) {
            }

            @Override
            public void networkContextDidDisconnect(LFXNetworkContext networkContext) {

            }

            @Override
            public void networkContextDidAddTaggedLightCollection(LFXNetworkContext networkContext, LFXTaggedLightCollection collection) {
                mNetworkContext.getAllLightsCollection().setPowerState(LFXTypes.LFXPowerState.ON);
            }

            @Override
            public void networkContextDidRemoveTaggedLightCollection(LFXNetworkContext networkContext, LFXTaggedLightCollection collection) {

            }
        });
    }

    public void changeColor(int color) {
        mNetworkContext.getAllLightsCollection().setColorOverDuration(convertColor(color), Config.DURATION_OF_COLOR_CHANGE);
    }

    public void start() {
        mNetworkContext.connect();
    }

    public void stop() {
        mNetworkContext.disconnect();
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
}
