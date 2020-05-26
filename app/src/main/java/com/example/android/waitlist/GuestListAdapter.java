package com.example.android.waitlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.waitlist.data.WaitlistContract;


public class GuestListAdapter extends RecyclerView.Adapter<GuestListAdapter.GuestViewHolder>
            implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Holds on to the cursor to display the waitlist
    private Cursor mCursor;
    private Context mContext;
    private Drawable backGroundNewColor;
    private GuestViewHolder mholder;
    private Resources resource ;


    public GuestListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        //sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public GuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.guest_list_item, parent, false);
        return new GuestViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(GuestViewHolder holder, int position) {
        mholder = holder;
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null
        // Update the view holder with the information needed to display
        String name = mCursor.getString(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME));
        int partySize = mCursor.getInt(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE));
        // COMPLETED (6) Retrieve the id from the cursor and
        long id = mCursor.getLong(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry._ID));
        // Display the guest name
        holder.nameTextView.setText(name);
        // Display the party count
        holder.partySizeTextView.setText(String.valueOf(partySize));

   /*     // COMPLETED (7) Set the tag of the itemview in the holder to the id
        GradientDrawable background = (GradientDrawable) partySizetv.getBackground();
        //background.setColor(Color.parseColor(r.getString(R.string.pref_dark_value)));
        //background.setColor(Color.parseColor(sharedPreferences.getString("color","red")));
        background.setColor(Color.parseColor(sharedPreferences.getString(resource.getString(R.string.pref_color_key),resource.getString(R.string.pref_color_red_value))));
        //holder.partySizeTextView.setBackgroundColor(backGroundNewColor); //make it square
        holder.partySizeTextView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.circle)); //change to circle
*/
        holder.itemView.setTag(id);
        setupSharedPreferences();

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        setColor(sharedPreferences.getString("color","red"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setColor(String newColorKey){

        if (newColorKey.equals(mContext.getString(R.string.pref_color_blue_value))) {
            backGroundNewColor = ContextCompat.getDrawable(mContext,R.drawable.circle);
        } else if (newColorKey.equals(mContext.getString(R.string.pref_color_green_value))) {
            backGroundNewColor = ContextCompat.getDrawable(mContext, R.drawable.circle_green);
        } else{
            backGroundNewColor = ContextCompat.getDrawable(mContext, R.drawable.circle_red);
        }
        mholder.partySizeTextView.setBackground(backGroundNewColor);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals("color")){
            setColor(sharedPreferences.getString(s,"red"));
        }
        this.notifyDataSetChanged();
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class GuestViewHolder extends RecyclerView.ViewHolder {

        // Will display the guest name
        TextView nameTextView;
        // Will display the party size number
        TextView partySizeTextView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews
         *
         * @param itemView The View that you inflated in
         *                 {@link GuestListAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public GuestViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            partySizeTextView = (TextView) itemView.findViewById(R.id.party_size_text_view);

        }

    }

}