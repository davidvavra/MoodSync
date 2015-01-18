package cz.destil.moodsync.light;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import cz.destil.moodsync.core.App;
import cz.destil.moodsync.core.BaseAsyncTask;
import cz.destil.moodsync.core.Config;

/**
 * Helper class which deals with Media Projection.
 *
 * @author David Vávra (david@vavra.me)
 */
public class MirroringHelper {

    static MirroringHelper sInstance;

    public static final int PERMISSION_CODE = 42;
    private MediaProjectionManager mProjectionManager;
    private DisplayMetrics mMetrics;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    boolean mMirroring = false;
    ImageReader mImageReader;

    public static MirroringHelper get() {
        if (sInstance == null) {
            sInstance = new MirroringHelper();
        }
        return sInstance;
    }

    public void init() {
        mProjectionManager = (MediaProjectionManager) App.get().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMetrics = App.get().getResources().getDisplayMetrics();
    }

    public void askForPermission(Activity activity) {
        mMirroring = true;
        activity.startActivityForResult(mProjectionManager.createScreenCaptureIntent(), PERMISSION_CODE);
    }

    public void stop() {
        mMirroring = false;
        mMediaProjection.stop();
        mVirtualDisplay.release();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("LIFX",
                Config.VIRTUAL_DISPLAY_WIDTH, Config.VIRTUAL_DISPLAY_HEIGHT, mMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                null, null /*Callbacks*/, null /*Handler*/);
    }

    public boolean isRunning() {
        return mMirroring;
    }

    public void permissionGranted(int resultCode, Intent data) {
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
    }

    public void getLatestBitmap(final Listener listener) {
        mImageReader = ImageReader.newInstance(Config.VIRTUAL_DISPLAY_WIDTH, Config.VIRTUAL_DISPLAY_HEIGHT, PixelFormat.RGBA_8888, 5);
        mVirtualDisplay = createVirtualDisplay();
        mVirtualDisplay.setSurface(mImageReader.getSurface());
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                mImageReader.setOnImageAvailableListener(null, null);
                new BaseAsyncTask() {

                    Bitmap bitmap;

                    @Override
                    public void inBackground() {
                        try {
                            Image img = mImageReader.acquireLatestImage();
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            final Image.Plane[] planes = img.getPlanes();
                            final ByteBuffer buffer = (ByteBuffer) planes[0].getBuffer().rewind();
                            bitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
                            bitmap.copyPixelsFromBuffer(buffer);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            bos.close();
                            img.close();
                            mImageReader.close();
                            mVirtualDisplay.release();
                        } catch (IOException ignored) {
                        }
                    }

                    @Override
                    public void postExecute() {
                        listener.onBitmapAvailable(bitmap);
                    }
                }.start();
            }
        }, null);
    }

    public interface Listener {
        public void onBitmapAvailable(Bitmap bitmap);
    }
}
