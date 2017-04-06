package com.iantmeyer.spyfall.fragment.search;

import android.Manifest;
import android.app.Activity;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.iantmeyer.spyfall.R;
import com.iantmeyer.spyfall.game.Contact;
import com.iantmeyer.spyfall.util.PermissionUtil;
import com.iantmeyer.spyfall.util.PhoneUtil;
import com.iantmeyer.spyfall.view.ContactItem;
import com.iantmeyer.spyfall.view.CursorItemAdapter;
import com.iantmeyer.spyfall.view.ItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchFragment extends Fragment implements SearchMvp.View, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "SearchFragment";

    private static final int LOADER_ID = 1;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 200;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.create_contact)
    protected Button mCreateContact;

    private CursorItemAdapter mAdapter;

    private SearchMvp.Presenter mPresenter;

    private SearchView mSearchView;

    private String mSearchFor = "";

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
    }

    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setTitle("");

        mAdapter = new CursorItemAdapter<ContactItem, Contact>(new ContactItem(), mContactClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new SearchPresenter(this);

        showContacts();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchViewMenuItem.getActionView();
        mSearchView.onActionViewExpanded();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView magImage = (ImageView) mSearchView.findViewById(searchImgId);
        magImage.setVisibility(View.GONE);
        magImage.setImageDrawable(null);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!mSearchFor.isEmpty()) {
            mSearchView.setQuery(mSearchFor, false);
        }
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchFor = newText;
                mPresenter.search(newText);
                if (newText.isEmpty()) clearSearch();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mPresenter.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearSearch() {
        mSearchFor = "";
    }

    private ItemClickListener<Contact> mContactClickListener = new ItemClickListener<Contact>() {
        @Override
        public void onItemClick(Contact contact, View view) {
            if (mPresenter.addContact(contact)) {
                backPressed();
            } else {
                Toast.makeText(getContext(), "Error adding contact", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int index, Bundle bundle) {
        Uri uri = mPresenter.getSearchUri();
        String[] projection = mPresenter.getSearchProjection();
        String selection = mPresenter.getSearchSelection();
        String[] selectionArgs = mPresenter.getSelectionArgs();
        String order = mPresenter.getSearchOrder();
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
        if (cursor.getCount() == 0 && PhoneUtil.isValidNumber(mSearchFor)) {
            mCreateContact.setVisibility(View.VISIBLE);
        } else {
            mCreateContact.setVisibility(View.GONE);
        }
        mAdapter.updateCursor(cursor);
    }

    @Override
    public void backPressed() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        getActivity().onBackPressed();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.updateCursor(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(getContext(), "Permission required to show contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showContacts() {
        if (PermissionUtil.hasPermissionWithRequest(this, Manifest.permission.READ_CONTACTS, PERMISSIONS_REQUEST_READ_CONTACTS)) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void searchAgain() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void errorDuplicateContact() {
        Toast.makeText(getContext(), getResources().getString(R.string.search_already_added), Toast.LENGTH_LONG).show();
    }

    private static final int CONTACT_ADDED = 1;

    @OnClick(R.id.create_contact)
    protected void addContact() {
        String phone = PhoneUtil.getJustNumber(mSearchFor);
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        getActivity().startActivityForResult(intent, CONTACT_ADDED);
    }
}