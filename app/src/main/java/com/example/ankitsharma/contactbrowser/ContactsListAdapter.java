package com.example.ankitsharma.contactbrowser;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;


/**
 * Created by ankitsharma on 9/14/16.
 */
public class ContactsListAdapter extends CursorAdapter implements SectionIndexer {

    private static final String LOG_TAG = ContactsListAdapter.class.getSimpleName();

    AlphabetIndexer indexer;
    private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static class ViewHolder {
        TextView nameView;
        TextView numberView;
    }

    public ContactsListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);



    }

    @Override
    public Cursor swapCursor(Cursor cursor) {
        if (!cursor.isClosed()) {
            indexer = new AlphabetIndexer(cursor,
                    cursor.getColumnIndex(ContactsFragment.CONTACT_COLUMN_NAME),
                    alphabet);
        }
        indexer.setCursor(cursor);
        return super.swapCursor(cursor);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return indexer.getPositionForSection(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return indexer.getSectionForPosition(position);
    }

    @Override
    public Object[] getSections() {
        return indexer.getSections();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (!cursor.isClosed()) {
            //Log.d(LOG_TAG, cursor.getString(ContactsFragment.CONTACT_NAME));
            ViewHolder holder = (ViewHolder) view.getTag();
            //Log.d(LOG_TAG, cursor.getString(ContactsFragment.CONTACT_NUMBER_TYPE));
            holder.nameView.setText(cursor.getString(ContactsFragment.CONTACT_NAME));
            holder.numberView.setText(cursor.getString(ContactsFragment.CONTACT_NUMBER));
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contacts_list_item, parent, false);

        // View Holder for optimization of findViewById
        ViewHolder holder = new ViewHolder();
        holder.nameView = (TextView) view.findViewById(R.id.name);
        holder.numberView = (TextView) view.findViewById(R.id.number);
        view.setTag(holder);

        return view;
    }
}