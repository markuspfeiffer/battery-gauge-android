package de.codewild.batterygauge.ui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import de.codewild.batterygauge.R;
import de.codewild.batterygauge.ioc.ApplicationComponent;
import de.codewild.batterygauge.model.BatteryStatus;
import de.codewild.batterygauge.model.BatteryValue;
import de.codewild.batterygauge.receivers.PowerConnectionReceiver;
import de.codewild.batterygauge.ui.view.Gauge;
import de.codewild.batterygauge.ui.view.ProgressRing;


public class BatteryGaugeActivity extends DaggerActivity implements ProgressRing.ProgressRingListener, Runnable {

    @Inject
    EventBus eventBus;

    @BindView(R.id.gauge_progress_ring)
    ProgressRing progressRing;

    @BindView(R.id.gauge)
    Gauge gauge;

    private Handler handler;

    private PowerConnectionReceiver receiver = new PowerConnectionReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_gauge);

        handler = new Handler();
    }

    @Override
    protected void injectFrom(@NonNull ApplicationComponent component) {
        component.injectInto(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        receiver = new PowerConnectionReceiver();

        initialize();
        eventBus.register(this);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        eventBus.unregister(this);

        super.onPause();
    }

    private void initialize() {
        int batteryPercent = getBatteryPercent();
        progressRing.setProgress(batteryPercent);
        gauge.setProgress(batteryPercent);
    }

    private int getBatteryPercent() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        final Intent batteryStatus = this.registerReceiver(null, filter);

        if (batteryStatus != null) {
            final int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            final int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            return Math.round(level / (float) scale * 100f);
        }
        return -1;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final BatteryStatus status) {
        int direction = status.isCharging() ? 15 : -15;
        final int percent = getBatteryPercent() + direction;

        progressRing.setDuration(100);
        progressRing.addProgressListener(this);
        progressRing.setProgress(percent, true);
        progressRing.setStarted(true);

        gauge.setDuration(100);
        gauge.addProgressListener(this);
        gauge.setProgress(percent, true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final BatteryValue value) {
        progressRing.setProgress(value.getPercent(), true);
        gauge.setProgress(value.getPercent(), true);
    }

    @Override
    public void onVisualProgressChanged(float visualProgress, int actualProgress) {
        if (visualProgress == actualProgress) {
            handler.post(this);
        }
    }

    @Override
    public void run() {
        progressRing.setDuration(500);
        progressRing.addProgressListener(null);

        gauge.setDuration(500);
        gauge.addProgressListener(null);

        int batteryPercent = getBatteryPercent();
        progressRing.setProgress(batteryPercent, true);
        gauge.setProgress(batteryPercent, true);
    }
}
