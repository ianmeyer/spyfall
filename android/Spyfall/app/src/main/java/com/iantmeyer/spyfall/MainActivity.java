package com.iantmeyer.spyfall;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.iantmeyer.spyfall.fragment.dialog.CustomDialogFragment;
import com.iantmeyer.spyfall.fragment.dialog.DialogListener;
import com.iantmeyer.spyfall.fragment.play.PlayFragment;
import com.iantmeyer.spyfall.fragment.search.SearchFragment;
import com.iantmeyer.spyfall.fragment.setup.SetupFragment;
import com.iantmeyer.spyfall.game.Game;
import com.iantmeyer.spyfall.game.GameCallback;
import com.iantmeyer.spyfall.util.Settings;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DialogListener {

    public static final String TAG_START_GAME = "tag_start_game";

    public static final String TAG_LEAVE_GAME = "tag_leave_game";

    public static final String TAG_RULES = "tag_rules";

    public static final String TAG_ABOUT = "tag_about";

    @BindView(R.id.fab)
    protected FloatingActionButton mFab;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @Inject protected Settings mSettings;

    @Inject Game mGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        App.getComponent().inject(this);

        setSupportActionBar(mToolbar);
        initTaskDescription();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSettings.getPhoneIds().size() >= 7) {
                    Toast.makeText(getBaseContext(), getString(R.string.max_8_players), Toast.LENGTH_LONG).show();
                    return;
                }
                loadFragment(SearchFragment.newInstance(), true);
                mFab.setVisibility(View.GONE);
            }
        });
        if (savedInstanceState != null) {
            return;
        }
        mGame.setGameCallback(mGameCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadFragment(final Fragment fragment, final boolean addToBackStack) {
        if (isFinishing()) {
            return;
        }
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void initTaskDescription() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String taskTitle = getString(R.string.app_name);
            Bitmap taskIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_spy);
            int taskColor = getResources().getColor(R.color.colorPrimary);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(taskTitle, taskIcon, taskColor);
            setTaskDescription(taskDescription);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
/*
            case R.id.menu_rules:
                showRulesDialog();
                return true;
*/
            case R.id.menu_about:
                showAboutDialog();
                return true;
        }
        // Must return false by default so fragment onOptionsItemSelected() will be called
        return false;
    }

    public void showFab(boolean show) {
        if (show) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.GONE);
        }
    }

    private GameCallback mGameCallback = new GameCallback() {
        @Override
        public void onGameUpdate(State state) {
            switch (state) {
                case PLAY:
                    loadFragment(PlayFragment.newInstance(), true);
                    break;
                case SETUP:
                    loadFragment(SetupFragment.newInstance(), false);
                    break;
            }
        }

        @Override
        public void onError(String message) {
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onConfirm(String tag) {
        switch (tag) {
            case TAG_START_GAME:
                mGame.startGame();
                break;
            case TAG_LEAVE_GAME:
                mGame.leaveGame();
                loadFragment(SetupFragment.newInstance(), false);
                break;
        }
    }

    @Override
    public void onDeny(String tag) {
        switch (tag) {
            case TAG_START_GAME:
                break;
            case TAG_LEAVE_GAME:
                break;
        }
    }

    /*
        private void showRulesDialog() {
            new CustomDialogFragment.Builder()
                    .setTitle(getResources().getString(R.string.menu_rules))
                    .setMessage(getResources().getString(R.string.dialog_rules))
                    .setClose(getResources().getString(R.string.dialog_close))
                    .build()
                    .show(getFragmentManager(), MainActivity.TAG_RULES);
        }
    */
    private void showAboutDialog() {
        new CustomDialogFragment.Builder()
                .setTitle(getResources().getString(R.string.menu_about))
                .setMessage(getResources().getString(R.string.dialog_about))
                .setClose(getResources().getString(R.string.dialog_close))
                .setTextGravity(Gravity.CENTER_HORIZONTAL)
                .build()
                .show(getFragmentManager(), MainActivity.TAG_ABOUT);
    }
}