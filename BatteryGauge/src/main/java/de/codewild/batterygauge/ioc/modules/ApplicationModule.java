package de.codewild.batterygauge.ioc.modules;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import de.codewild.batterygauge.ioc.scopes.ApplicationScope;


@Module
public class ApplicationModule {

    @NonNull
    @Provides
    @ApplicationScope
    EventBus getEventBus() {
        return EventBus
                .builder()
                .logNoSubscriberMessages(false)
                .build();
    }
}
