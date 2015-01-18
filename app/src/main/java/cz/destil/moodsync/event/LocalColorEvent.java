package cz.destil.moodsync.event;

/**
 * OttoEvent for local color.
 *
 * @author David Vávra (david@vavra.me)
 */
public class LocalColorEvent {

    public int previousColor;
    public int newColor;

    public LocalColorEvent(int previousColor, int currentColor) {
        this.previousColor = previousColor;
        this.newColor = currentColor;
    }
}
