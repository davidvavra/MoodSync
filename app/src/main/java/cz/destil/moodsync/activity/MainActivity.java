package cz.destil.moodsync.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cz.destil.moodsync.R;
import cz.destil.moodsync.core.App;
import cz.destil.moodsync.core.Config;
import cz.destil.moodsync.event.ErrorEvent;
import cz.destil.moodsync.event.LocalColorEvent;
import cz.destil.moodsync.event.SuccessEvent;
import cz.destil.moodsync.light.LocalColorSwitcher;
import cz.destil.moodsync.light.MirroringHelper;
import cz.destil.moodsync.service.LightsService;

public class MainActivity extends Activity {

    @InjectView(R.id.container)
    LinearLayout vContainer;
    @InjectView(R.id.name)
    TextView vName;
    @InjectView(R.id.progress_layout)
    LinearLayout vProgressLayout;
    @InjectView(R.id.progress_bar)
    ProgressBar vProgressBar;
    @InjectView(R.id.progress_text)
    TextView vProgressText;
    @InjectView(R.id.control)
    ToggleButton vButton;

    MirroringHelper mMirroring;
    private LocalColorSwitcher mColorSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        hideProgress();
        vName.setText(Html.fromHtml(getString(R.string.app_name_r)));
        mMirroring = MirroringHelper.get();
        mColorSwitcher = LocalColorSwitcher.get();
        vContainer.setBackgroundColor(mColorSwitcher.getPreviousColor());
        vButton.setChecked(mMirroring.isRunning());
        App.bus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mColorSwitcher.stop();
    }

    @Override
    protected void onDestroy() {
        App.bus().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mColorSwitcher.start();
    }

    @OnClick(R.id.control)
    public void controlButtonClicked() {
        if (mMirroring.isRunning()) {
            stop();
        } else {
            showProgress(R.string.connecting);
            mMirroring.askForPermission(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != MirroringHelper.PERMISSION_CODE) {
            return;
        }
        if (resultCode != RESULT_OK) {
            showError(R.string.give_permission);
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

    @Subscribe
    public void onError(ErrorEvent event) {
        showError(event.textRes);
    }

    @Subscribe
    public void onSuccess(SuccessEvent event) {
        hideProgress();
    }

    private void stop() {
        Intent intent = new Intent(this, LightsService.class);
        intent.setAction("STOP");
        startService(intent);
    }

    private void showProgress(int textResId) {
        vProgressLayout.setVisibility(View.VISIBLE);
        vProgressBar.setVisibility(View.VISIBLE);
        vProgressText.setText(textResId);
        vButton.setVisibility(View.GONE);
    }

    private void hideProgress() {
        vProgressLayout.setVisibility(View.GONE);
        vButton.setVisibility(View.VISIBLE);
    }

    private void showError(int textResId) {
        vProgressLayout.setVisibility(View.VISIBLE);
        vProgressBar.setVisibility(View.GONE);
        vProgressText.setText(textResId);
        vButton.setVisibility(View.GONE);
        stop();
    }
}
