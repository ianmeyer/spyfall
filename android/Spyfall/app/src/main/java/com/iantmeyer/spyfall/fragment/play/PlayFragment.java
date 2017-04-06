package com.iantmeyer.spyfall.fragment.play;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.iantmeyer.spyfall.MainActivity;
import com.iantmeyer.spyfall.R;
import com.iantmeyer.spyfall.fragment.dialog.CustomDialogFragment;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.game.Location;
import com.iantmeyer.spyfall.game.Role;
import com.iantmeyer.spyfall.view.ItemAdapter;
import com.iantmeyer.spyfall.view.ItemClickListener;
import com.iantmeyer.spyfall.view.LocationItem;
import com.iantmeyer.spyfall.view.NoScrollGridLayoutManager;
import com.iantmeyer.spyfall.view.PlayerItem;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnTouch;

public class PlayFragment extends Fragment implements PlayMvp.View {

    @BindView(R.id.timer_time)
    protected TextView mTimerTv;

    @BindView(R.id.role_title)
    protected TextView mRoleTitleTv;

    @BindView(R.id.role_location)
    protected TextView mRoleLocationTv;

    @BindView(R.id.role)
    protected FrameLayout mRoleView;

    @BindView(R.id.role_icon)
    protected ImageView mRoleIv;

    @BindView(R.id.location)
    protected LinearLayout mLocation;

    @BindView(R.id.role_hidden)
    protected View mRoleHiddenView;

    @BindView(R.id.button_reveal)
    protected Button mRevealButton;

    @BindView(R.id.button_pause_play)
    protected ToggleButton mTimerButton;

    @BindView(R.id.button_lock)
    protected ToggleButton mLockButton;

    @BindView(R.id.recycler_view_players)
    protected RecyclerView mRecyclerViewPlayers;

    @BindView(R.id.recycler_view_locations)
    protected RecyclerView mRecyclerViewLocations;

    private ItemAdapter<PlayerItem, Contact> mPlayersAdapter;
    private ItemAdapter<LocationItem, Location> mLocationsAdapter;

    private PlayMvp.Presenter mPresenter;

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    public PlayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_play, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        Context context = inflater.getContext();
        mRecyclerViewPlayers.setLayoutManager(new NoScrollGridLayoutManager(context, 2));
        mPlayersAdapter = new ItemAdapter<>(new PlayerItem(), mPlayerClickListener);
        mRecyclerViewPlayers.setAdapter(mPlayersAdapter);

        mRecyclerViewLocations.setLayoutManager(new NoScrollGridLayoutManager(context, 2));
        mLocationsAdapter = new ItemAdapter<>(new LocationItem(), mLocationClickListener);
        mRecyclerViewLocations.setAdapter(mLocationsAdapter);

        mPresenter = new PlayPresenter(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return mPresenter.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public void endGamePrompt() {
        new CustomDialogFragment.Builder()
                .setMessage(getResources().getString(R.string.dialog_leave_game))
                .setConfirm(getResources().getString(R.string.dialog_leave))
                .setDeny(getResources().getString(R.string.dialog_cancel))
                .build()
                .show(getFragmentManager(), MainActivity.TAG_LEAVE_GAME);
    }

    @Override
    public void revealRole() {
        mRoleHiddenView.setVisibility(View.INVISIBLE);
        mRoleView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRole() {
        mRoleHiddenView.setVisibility(View.VISIBLE);
        mRoleView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void disableRevealButton(boolean disabled) {
        mRevealButton.setEnabled(!disabled);
    }

    @Override
    public void toggleLock(boolean locked) {
        mLockButton.setChecked(locked);
    }

    @Override
    public void toggleTimer(boolean paused) {
        mTimerButton.setChecked(paused);
    }

    @Override
    public void setTimer(long seconds) {
        seconds = seconds < 0 ? 0 : seconds;
        long minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        mTimerTv.setText("" + String.format(Locale.getDefault(), "%02d", minutes) + ":" + String.format(Locale.getDefault(), "%02d", seconds));
    }

    @Override
    public void setRole(Role role) {
        mRoleTitleTv.setText(role.title);
        if (role.isSpy) {
            mLocation.setVisibility(View.GONE);
        } else {
            mRoleLocationTv.setText(role.location);
        }
        mRoleIv.setImageResource(role.image);
    }

    @Override
    public void setContacts(List<Contact> contacts) {
        mPlayersAdapter.updateList(contacts);
    }

    @Override
    public void setLocations(List<Location> locations) {
        mLocationsAdapter.updateList(locations);
    }

    @OnTouch(R.id.button_reveal)
    public boolean onRevealTouch(View view, MotionEvent event) {

        int code = event.getAction() & MotionEvent.ACTION_MASK;

        if (view.getId() == R.id.button_reveal) {
            switch (code) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_BUTTON_PRESS:
                    mPresenter.onRevealPress();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_BUTTON_RELEASE:
                    mPresenter.onRevealUnpress();
                    break;
            }
        }
        return true;
    }

    @OnCheckedChanged(R.id.button_lock)
    public void onLockChanged(boolean locked) {
        mPresenter.onRevealLockChanged(locked);
    }

    @OnCheckedChanged(R.id.button_pause_play)
    public void onPauseChanged(boolean paused) {
        mPresenter.onPauseChanged(paused);
    }

    private ItemClickListener mPlayerClickListener = new ItemClickListener<Contact>() {
        @Override
        public void onItemClick(Contact contact, View view) {
        }
    };

    private ItemClickListener mLocationClickListener = new ItemClickListener<Location>() {
        @Override
        public void onItemClick(Location location, View view) {
        }
    };
}