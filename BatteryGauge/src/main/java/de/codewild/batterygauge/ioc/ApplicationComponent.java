package de.codewild.batterygauge.ioc;


import android.support.annotation.NonNull;

import dagger.Component;
import de.codewild.batterygauge.ioc.modules.ApplicationModule;
import de.codewild.batterygauge.ioc.scopes.ApplicationScope;
import de.codewild.batterygauge.receivers.PowerConnectionReceiver;
import de.codewild.batterygauge.ui.activities.BatteryGaugeActivity;


@ApplicationScope
@Component(modules = {
        ApplicationModule.class
})
public interface ApplicationComponent {

    void injectInto(@NonNull BatteryGaugeActivity activity);

    void injectInto(@NonNull PowerConnectionReceiver receiver);
}
