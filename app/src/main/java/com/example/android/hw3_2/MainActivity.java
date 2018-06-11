package com.example.android.hw3_2;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private GuestListAdapter mAdapter;
    private EditText mNewGuestNameEditText;
    private Spinner mNewPartySizeSpinner;
    private EditText mNewPhoneNumber;
    private Button addButton;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private String partySize = "2人";
    private static final int TASK_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView waitlistRecyclerView;

        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        mNewGuestNameEditText = (EditText)this.findViewById(R.id.person_name_edit_text);
        mNewPhoneNumber = (EditText)this.findViewById(R.id.phonenumber_edit_text);
        addButton = (Button)this.findViewById(R.id.add_to_waitlist_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWaitlist();
            }
        });


        mNewPartySizeSpinner = (Spinner)this.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence>list = ArrayAdapter.createFromResource(
                this,R.array.spn_list,android.R.layout.simple_spinner_item);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNewPartySizeSpinner.setAdapter(list);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                long id = (Long) viewHolder.itemView.getTag();

               removeGuest(id);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                mAdapter.swapCursor(getAllGuests());

            }
        }).attachToRecyclerView(waitlistRecyclerView);





        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Cursor cursor = getAllGuests();
        mAdapter = new GuestListAdapter(this,cursor);
        waitlistRecyclerView.setAdapter(mAdapter);



    }
    public void addToWaitlist(){
        if (mNewGuestNameEditText.getText().length() == 0 ||
                mNewPhoneNumber.getText().length() == 0) {
            return;
        }
        //default party size to 1
        mNewPartySizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                partySize = (parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //mNewPartySizeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           // @Override
           // public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //partySize = Integer.valueOf(parent.getSelectedItem().toString());
            //}
        //});
        //try {
            //mNewPartyCountEditText inputType="number", so this should always work

        //} catch (NumberFormatException ex) {
           // Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
        //}

        // Add guest info to mDb
        addNewGuest(mNewGuestNameEditText.getText().toString(), partySize,mNewPhoneNumber.getText().toString());

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGuests());

        //clear UI text fields

        mNewGuestNameEditText.getText().clear();
        mNewPhoneNumber.getText().clear();
    }

    private Cursor getAllGuests() {
        return getContentResolver().query(
                Uri.parse("content://com.example.android.hw3_1.TaskContentProvider/tasks"),
                null,null,null,null );
    }
    private Uri addNewGuest(String name, String partySize, String phoneNumber) {
        ContentValues cv = new ContentValues();
        cv.put("guestName", name);
        cv.put("partySize", partySize);
        cv.put("phoneNumber",phoneNumber);
        return getContentResolver().insert(Uri.parse("content://com.example.android.hw3_1.TaskContentProvider/tasks"),cv);
    }
    private int removeGuest(long id) {
        return getContentResolver().delete(Uri.parse("content://com.example.android.hw3_1.TaskContentProvider/tasks/"+id),
                null,null);
    }
}
