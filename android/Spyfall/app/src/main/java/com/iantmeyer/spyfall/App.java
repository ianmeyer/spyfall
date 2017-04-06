package com.iantmeyer.spyfall;

import android.app.Application;

import com.iantmeyer.spyfall.dagger.AppComponent;
import com.iantmeyer.spyfall.dagger.AppModule;
import com.iantmeyer.spyfall.dagger.DaggerAppComponent;
import com.squareup.leakcanary.LeakCanary;

public class App extends Application {

    private static AppComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initLeakCanary();
        initDagger();
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this.getApplicationContext())) {
            return;
        }
        LeakCanary.install(this);
    }

    private void initDagger() {
        mComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this.getApplicationContext()))
                .build();
    }

    public static AppComponent getComponent() {
        return mComponent;
    }
}