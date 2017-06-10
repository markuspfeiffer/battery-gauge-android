package de.codewild.batterygauge.ui.activities;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import de.codewild.batterygauge.BatteryGauge;
import de.codewild.batterygauge.ioc.ApplicationComponent;


public abstract class DaggerActivity extends BaseActivity {

    @Override
    public void setContentView(@LayoutRes final int layoutResID) {
        super.setContentView(layoutResID);

        final ApplicationComponent component = this.getComponent();
        this.injectFrom(component);
    }

    protected abstract void injectFrom(@NonNull ApplicationComponent component);

    @NonNull
    @Override
    public BatteryGauge getApplicationContext() {
        return (BatteryGauge) super.getApplicationContext();
    }

    @NonNull
    protected ApplicationComponent getComponent() {
        return this.getApplicationContext().getComponent();
    }
}
