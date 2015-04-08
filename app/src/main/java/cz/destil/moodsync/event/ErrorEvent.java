package cz.destil.moodsync.event;

/**
 * Otto event for some error.
 *
 * @author David Vávra (david@vavra.me)
 */
public class ErrorEvent {
    public int textRes;

    public ErrorEvent(int textRes) {
        this.textRes = textRes;
    }
}
