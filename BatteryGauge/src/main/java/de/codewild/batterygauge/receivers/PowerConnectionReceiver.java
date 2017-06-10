package de.codewild.batterygauge.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import de.codewild.batterygauge.BatteryGauge;
import de.codewild.batterygauge.ioc.ApplicationComponent;
import de.codewild.batterygauge.model.BatteryStatus;
import de.codewild.batterygauge.model.BatteryValue;

public class PowerConnectionReceiver extends BroadcastReceiver {

    @Inject
    EventBus eventBus;

    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        ApplicationComponent component = ((BatteryGauge) context.getApplicationContext()).getComponent();
        component.injectInto(this);

        if (hasAction(intent, Intent.ACTION_POWER_DISCONNECTED)) {
            final BatteryStatus status = new BatteryStatus(false);
            eventBus.post(status);
        } else if (hasAction(intent, Intent.ACTION_POWER_CONNECTED)) {
            final BatteryStatus status = new BatteryStatus(true);
            eventBus.post(status);
        } else if (hasAction(intent, Intent.ACTION_BATTERY_CHANGED)) {
            final int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            final int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            final int percent = Math.round(level / (float) scale * 100f);
            final BatteryValue value = new BatteryValue(percent);
            eventBus.post(value);
        }
    }

    private boolean hasAction(@NonNull Intent intent, @NonNull String action) {
        return TextUtils.equals(action, intent.getAction());
    }
}
