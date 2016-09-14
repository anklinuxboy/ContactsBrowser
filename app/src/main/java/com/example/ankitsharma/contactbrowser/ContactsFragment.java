package com.example.ankitsharma.contactbrowser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = ContactsFragment.class.getSimpleName();
    /*
     * Declare global variables like adapter and list view
     */

    // Marshmallow update
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    ListView mContactsList;

    long mContactId;

    String mContactKey;

    Uri mContactUri;

    private final int LOADER_ID = 0;

    private SimpleCursorAdapter mCursorAdapter;

    private final String[] CONTACT_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    private static final int CONTACT_ID = 0;
    private static final int CONTACT_NAME = 1;
    private static final int CONTACT_HAS_NUMBER = 2;

    private final String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    };

    private final static int[] TO_IDS = {
            R.id.name
    };

    public ContactsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check for permissions for Android 6.0, if granted, then display contacts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        else
            showContacts();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void showContacts() {
        mContactsList = (ListView) getActivity().findViewById(R.id.list);

        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);

        mContactsList.setAdapter(mCursorAdapter);

        mContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String number = "4567894512";
                Log.d(LOG_TAG, "onClick");
                //Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+number));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        // Start the loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC";
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?";
        String[] selectionArgs = new String[]{"1"};
        return new CursorLoader(
                getActivity(),
                ContactsContract.Contacts.CONTENT_URI,
                CONTACT_PROJECTION,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        data.moveToFirst();
//        do {
//            Log.d(LOG_TAG, "Cursor name " + data.getString(CONTACT_NAME));
//        } while (data.moveToNext());

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
