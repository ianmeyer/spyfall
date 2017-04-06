package com.iantmeyer.spyfall.dagger;

import com.iantmeyer.spyfall.MainActivity;
import com.iantmeyer.spyfall.fragment.play.PlayPresenter;
import com.iantmeyer.spyfall.fragment.search.SearchPresenter;
import com.iantmeyer.spyfall.fragment.setup.SetupPresenter;
import com.iantmeyer.spyfall.game.LocationManager;
import com.iantmeyer.spyfall.util.SharedPrefSettings;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(SharedPrefSettings sharedPrefSettings);

    void inject(LocationManager locationManager);

    void inject(SetupPresenter presenter);

    void inject(SearchPresenter presenter);

    void inject(PlayPresenter presenter);
}