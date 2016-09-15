package com.example.ankitsharma.contactbrowser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = ContactsFragment.class.getSimpleName();

    // Marshmallow update
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    ListView contactsList;
    private final int LOADER_ID_CONTACTS = 0;
    private ContactsListAdapter adapter;
    private String searchNameSelection = "";
    private final int FLAGS = 0;

    private final String[] CONTACT_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    static final int CONTACT_ID = 0;
    static final int CONTACT_NAME = 1;
    static final int CONTACT_NUMBER_TYPE = 2;
    static final int CONTACT_NUMBER = 3;

    static final String CONTACT_COLUMN_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchNameSelection = query;
                getLoaderManager().restartLoader(LOADER_ID_CONTACTS, null, ContactsFragment.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchNameSelection = newText;
                getLoaderManager().restartLoader(LOADER_ID_CONTACTS, null, ContactsFragment.this);
                Log.d(LOG_TAG, newText);
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check for permissions for Android 6.0, if granted, then display contacts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                                                              != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                                                 PERMISSIONS_REQUEST_READ_CONTACTS);
        else
            showContacts();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void showContacts() {
        contactsList = (ListView) getActivity().findViewById(R.id.list);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "onClick");
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                String number = cursor.getString(CONTACT_NUMBER);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+number));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        // Start the loader
        getLoaderManager().initLoader(LOADER_ID_CONTACTS, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC";
        String selection = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + searchNameSelection + "%"};

        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                CONTACT_PROJECTION,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.isClosed()) {
            adapter = new ContactsListAdapter(getContext(), data, FLAGS);
            adapter.swapCursor(data);
            contactsList.setAdapter(adapter);
            //contactsList.setFastScrollEnabled(true);
            //contactsList.setScrollingCacheEnabled(true);
            //contactsList.setFastScrollAlwaysVisible(true);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
