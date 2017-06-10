package de.codewild.batterygauge;

import android.app.Application;

import de.codewild.batterygauge.ioc.ApplicationComponent;
import de.codewild.batterygauge.ioc.DaggerApplicationComponent;

public class BatteryGauge extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Dagger 2 application component
        this.setUpComponent();
    }

    private void setUpComponent() {
        this.component = DaggerApplicationComponent
                .builder()
                .build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
