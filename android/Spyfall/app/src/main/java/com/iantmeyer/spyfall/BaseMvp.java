package com.iantmeyer.spyfall;

public interface BaseMvp {
    interface View<P extends Presenter> {
    }

    interface Presenter {
        void onResume();

        void onPause();

        boolean onBackPressed();
    }
}