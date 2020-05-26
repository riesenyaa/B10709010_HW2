package com.example.android.waitlist;

import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class GuestBag implements Serializable {
    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;

public GuestBag(){

}

    public GuestListAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(GuestListAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public SQLiteDatabase getmDb() {
        return mDb;
    }

    public void setmDb(SQLiteDatabase mDb) {
        this.mDb = mDb;
    }
}
