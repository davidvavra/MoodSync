package cz.destil.moodsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.destil.moodsync.R;
import cz.destil.moodsync.core.App;
import cz.destil.moodsync.core.Config;
import cz.destil.moodsync.event.LocalColorEvent;
import cz.destil.moodsync.light.LightsController;
import cz.destil.moodsync.light.LocalColorSwitcher;
import cz.destil.moodsync.light.MirroringHelper;
import cz.destil.moodsync.service.LightsService;
import cz.destil.moodsync.util.Toas;

public class MainActivity extends Activity {

    @InjectView(R.id.container)
    LinearLayout vContainer;
    @InjectView(R.id.name)
    TextView vName;

    MirroringHelper mMirroring;
    private LocalColorSwitcher mColorSwitcher;
    private LightsController mLights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        vName.setText(Html.fromHtml(getString(R.string.app_name_r)));
        mMirroring = MirroringHelper.get();
        mColorSwitcher = LocalColorSwitcher.get();
        vContainer.setBackgroundColor(mColorSwitcher.getPreviousColor());
        mLights = LightsController.get();
        mMirroring.init();
        mLights.init();
    }

    @Override
    protected void onStart() {
        App.bus().register(this);
        mColorSwitcher.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mColorSwitcher.stop();
        App.bus().unregister(this);
        super.onStop();
    }

    @OnClick(R.id.control)
    public void controlButtonClicked() {
        if (mMirroring.isRunning()) {
            Intent intent = new Intent(this, LightsService.class);
            intent.setAction("STOP");
            startService(intent);
        } else {
            mMirroring.askForPermission(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != MirroringHelper.PERMISSION_CODE) {
            return;
        }
        if (resultCode != RESULT_OK) {
            Toas.t("You need to give sharing permission");
            return;
        }
        mMirroring.permissionGranted(resultCode, data);
        Intent intent = new Intent(this, LightsService.class);
        intent.setAction("START");
        startService(intent);
    }

    @Subscribe
    public void onNewLocalColor(LocalColorEvent event) {
        ColorDrawable[] colors = {new ColorDrawable(event.previousColor), new ColorDrawable(event.newColor)};
        TransitionDrawable trans = new TransitionDrawable(colors);
        vContainer.setBackground(trans);
        trans.startTransition(Config.DURATION_OF_COLOR_CHANGE);
    }
}
