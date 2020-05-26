package com.example.android.waitlist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.waitlist.data.OnSwipeTouchListener;
import com.example.android.waitlist.data.WaitlistContract;
import com.example.android.waitlist.data.WaitlistDbHelper;
import java.util.ArrayList;
import java.util.HashMap;



//public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private String status;
    private TextView mtv;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private static ArrayList<GuestBag> GuestBag = new ArrayList<>();
    RecyclerView waitlistRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Set local attributes to corresponding views
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);

//        waitlistRecyclerView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
//
//            public void onSwipeRight() {
////                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
//            }
//
//        });

        mtv = (TextView) this.findViewById(R.id.text);


        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllGuests();

        // Create an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter(this, cursor);
        //setupSharedPreferences();
        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter(mAdapter);


        // COMPLETED (3) Create a new ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipe directions
        // Create an item touch helper to handle swiping items off the list
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // COMPLETED (4) Override onMove and simply return false inside
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            // COMPLETED (5) Override onSwiped
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)  {
                // COMPLETED (8) Inside, get the viewHolder's itemView's tag and store in a long variable id
                //get the id of the item being swiped

                // COMPLETED (9) call removeGuest and pass through that id
                //remove from DB
                dialogAlert(viewHolder);



                // COMPLETED (10) call swapCursor on mAdapter passing in getAllGuests() as the argument
                //update the list


            }
//
            //COMPLETED (11) attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(waitlistRecyclerView);

        Intent intentFromAdd = getIntent();
        if(intentFromAdd.hasExtra("name")){
         //   mtv.setTextColor(0xffff0000);
//            Bundle bundle = intentFromAdd.getExtras();
            String name = intentFromAdd.getStringExtra("name");
            int size = intentFromAdd.getIntExtra("size",1);
//            mtv.setText(String.valueOf(size));
            addToWaitlist(name,size);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.add:
                    //jump to other window
                    //Add could show the add new people page，ok(add)/Cancel
                    Intent intent_add = new Intent();
                intent_add.setClass(MainActivity.this, Add.class);
                startActivity(intent_add);
//                MainActivity.this.finish();
                return true;

            case R.id.setting:
                Intent intent_setting = new Intent();
                intent_setting.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent_setting);
        //jump to preference
        //open settings，includes ListPreference，main point is that it will show (red、blue、green)，and show Summary.
        //after change settings, it will go back to the first page, and show the aftermath.
            return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void addToWaitlist(String name, int partySize) {

        // Add guest info to mDb
        addNewGuest(name, partySize);

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGuests());

    }

    protected void dialogAlert(final RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to permanently remove this item?");
        builder.setTitle("ATTENTION!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                long id = (long) viewHolder.itemView.getTag();
                removeGuest(id);
                mAdapter.swapCursor(getAllGuests());
                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAdapter.swapCursor(getAllGuests());
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    private long addNewGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }

    private boolean removeGuest(long id) {
        // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id
        return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }

/*
    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadColorFromPreferences(sharedPreferences);
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    private void loadColorFromPreferences(SharedPreferences sharedPreferences) {
        mAdapter.setColor(sharedPreferences.getString(getString(R.string.pref_color_key),
                getString(R.string.pref_color_red_value)));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_color_key))) {
            loadColorFromPreferences(sharedPreferences);
        }
    }
*/


}

