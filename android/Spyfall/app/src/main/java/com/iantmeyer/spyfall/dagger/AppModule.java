package com.iantmeyer.spyfall.dagger;

import android.content.Context;
import android.util.Log;

import com.iantmeyer.spyfall.game.Game;
import com.iantmeyer.spyfall.game.GameSettings;
import com.iantmeyer.spyfall.game.LocationManager;
import com.iantmeyer.spyfall.game.SpyfallGame;
import com.iantmeyer.spyfall.sms.SmsHelper;
import com.iantmeyer.spyfall.util.PhoneIds;
import com.iantmeyer.spyfall.util.Settings;
import com.iantmeyer.spyfall.util.SharedPrefSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final String TAG = "AppModule";

    private final Context mAppContext;

    public AppModule(Context context) {
        Log.d(TAG, "Init");
        mAppContext = context.getApplicationContext();
    }

    @Singleton
    @Provides
    Context providesAppContext() {
        return mAppContext;
    }

    @Singleton
    @Provides
    PhoneIds providesPhoneIds(Settings settings) {
        return new PhoneIds(settings);
    }

    @Singleton
    @Provides
    Settings providesSettings() {
        return new SharedPrefSettings();
    }

    @Singleton
    @Provides
    GameSettings providesGameSettings(Settings settings) {
        return new GameSettings(settings);
    }

    @Singleton
    @Provides
    LocationManager providesLocationManager() {
        return new LocationManager();
    }

    @Singleton
    @Provides
    SmsHelper providesSmsHelper(Context context) {
        return new SmsHelper(context);
    }

    @Singleton
    @Provides
    Game providesGame(SmsHelper smsHelper, LocationManager locationManager, GameSettings gameSettings) {
        return new SpyfallGame(smsHelper, locationManager, gameSettings);
    }
}