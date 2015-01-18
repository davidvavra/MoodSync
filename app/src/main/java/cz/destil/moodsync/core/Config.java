package cz.destil.moodsync.core;

/**
 * Global config which controls app behavior.
 *
 * @author David Vávra (david@vavra.me)
 */
public class Config {
    public static final int INITIAL_DELAY = 100; // in ms
    public static final int FINAL_DELAY = 100; // in ms
    public static final int FREQUENCE_OF_SCREENSHOTS = 500; // in ms
    public static final int DURATION_OF_COLOR_CHANGE = 2000; // in ms
    public static final float LIFX_BRIGHTNESS = 0.25f; // 0-1
    public static final int VIRTUAL_DISPLAY_WIDTH = 320; // in px
    public static final int VIRTUAL_DISPLAY_HEIGHT = 180; // in px
}
