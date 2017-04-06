package com.iantmeyer.spyfall.fragment.setup;

import android.Manifest;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iantmeyer.spyfall.MainActivity;
import com.iantmeyer.spyfall.R;
import com.iantmeyer.spyfall.fragment.dialog.CustomDialogFragment;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.util.CounterHelper;
import com.iantmeyer.spyfall.util.PermissionUtil;
import com.iantmeyer.spyfall.view.ContactItem;
import com.iantmeyer.spyfall.view.CursorItemAdapter;
import com.iantmeyer.spyfall.view.ItemClickListener;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetupFragment extends Fragment implements SetupMvp.View, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private static final int PERMISSIONS_SEND_SMS = 100;

    @BindView(R.id.timer_time)
    protected TextView mTimerTv;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.button_start)
    protected Button mStartBtn;

    private SetupMvp.Presenter mPresenter;

    private CursorItemAdapter<ContactItem, Contact> mAdapter;

    public static SetupFragment newInstance() {
        return new SetupFragment();
    }

    public SetupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.spy_right_padding);
        actionBar.setTitle(R.string.app_name);

        mAdapter = new CursorItemAdapter<ContactItem, Contact>(new ContactItem(true), mPlayerClickListener);
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new SetupPresenter(this);

        new CounterHelper.Builder()
                .incrementView(view.findViewById(R.id.timer_plus))
                .decrementView(view.findViewById(R.id.timer_minus))
                .delay(400, 50, 50)
                .range(60, 3570)            // 1:00 to 59:30
                .start(mPresenter.getTimeLimit())
                .step(30)
                .listener(mCounterListener)
                .build();

        loadCursor();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_setup, menu);
        menu.findItem(R.id.menu_last_game).setEnabled(mPresenter.lastGameAvailable());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return mPresenter.onBackPressed();
            case R.id.menu_last_game:
                if (!mPresenter.reopenLastGame()) {
                    Toast.makeText(getContext(), "No previous game available", Toast.LENGTH_LONG).show();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadCursor();
        showFab(true);
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        showFab(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    public void loadCursor() {
        if (PermissionUtil.hasPermission(getContext(), Manifest.permission.READ_CONTACTS)) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            mAdapter.updateCursor(null);
        }
    }

    @Override
    public void setHeader(Contact contact) {
        mAdapter.setHeader(contact);
    }

    @Override
    public void reloadCursor() {
        if (PermissionUtil.hasPermission(getContext(), Manifest.permission.READ_CONTACTS)) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void showFab(boolean show) {
        ((MainActivity) getActivity()).showFab(show);
    }

    @Override
    public void setTimeLimit(long seconds) {
        long minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        mTimerTv.setText("" + String.format(Locale.getDefault(), "%02d", minutes) + ":" + String.format(Locale.getDefault(), "%02d", seconds));
    }

    @Override
    public void showErrorMessage(String msg) {
    }

    @Override
    public void startGamePrompt(int numTexts) {
        String msg = getResources().getString(R.string.dialog_start_game);
        msg = msg.replace("#", getNumber(numTexts));

        new CustomDialogFragment.Builder()
                .setMessage(msg)
                .setConfirm(getResources().getString(R.string.dialog_start))
                .setDeny(getResources().getString(R.string.dialog_cancel))
                .build()
                .show(getFragmentManager(), MainActivity.TAG_START_GAME);
    }

    private String getNumber(int number) {
        switch (number) {
            case 2:
                return "Two";
            case 3:
                return "Three";
            case 4:
                return "Four";
            case 5:
                return "Five";
            case 6:
                return "Six";
            case 7:
                return "Seven";
            default:
                return String.valueOf(number);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = mPresenter.getPlayersUri();
        String[] projection = mPresenter.getPlayersProjection();
        String selection = mPresenter.getPlayersSelection();
        String[] selectionArgs = mPresenter.getPlayersSelectionArgs();
        String order = mPresenter.getPlayersOrder();

        return new CursorLoader(
                getContext(),
                uri,
                projection,
                selection,
                selectionArgs,
                order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.updateCursor(cursor);
        mPresenter.setContacts(cursor);
    }

    @Override
    public void enableStartButton(boolean enable) {
        mStartBtn.setEnabled(enable);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.updateCursor(null);
    }

    private CounterHelper.CounterListener mCounterListener = new CounterHelper.CounterListener() {
        @Override
        public void onCounterChange(long value) {
            mPresenter.setTimeLimit(value);
        }
    };

    private ItemClickListener<Contact> mPlayerClickListener = new ItemClickListener<Contact>() {
        @Override
        public void onItemClick(Contact contact, View view) {
            if (view.getId() == R.id.remove) {
                mPresenter.removePlayer(contact);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.contactId));
                intent.setData(uri);
                getActivity().startActivity(intent);
            }
        }
    };

    @OnClick(R.id.button_start)
    protected void onStartButtonPress() {
        if (PermissionUtil.hasPermissionWithRequest(this, Manifest.permission.SEND_SMS, PERMISSIONS_SEND_SMS)) {
            mPresenter.startGame();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.startGame();
            } else {
                Toast.makeText(getContext(), "Requires permission to send SMS messages", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Context getContext() {
        return getActivity().getApplicationContext();
    }
}