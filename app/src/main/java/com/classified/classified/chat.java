package com.classified.classified;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;


public class chat extends ActionBarActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    private String courseID;
    private String netId;
    private Menu menu;
    private boolean isBookmarked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i = getIntent();
        courseID = i.getStringExtra("Course_ID");
        netId = i.getStringExtra("NET_ID");
        setTitle(i.getStringExtra("Course_Name"));

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference().child(courseID)
                        .push()
                        .setValue(new ChatMessage(input.getText().toString()
                        ));

                // Clear the input
                input.setText("");
                displayChatMessages();
            }
        });

        displayChatMessages();
    }

    private void displayChatMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(courseID)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                // Set their text
                messageText.setText(model.getMessageText());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        initBookmark();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);

        return true;
    }

    private void initBookmark() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(netId).child(courseID);
        databaseRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                isBookmarked = dataSnapshot.getValue(Bookmark.class) != null;
                setBookmarkOptions();
            }
        });
    }

    private void setBookmarkOptions() {
        if (menu == null) return;
        if (isBookmarked) {
            menu.findItem(R.id.bookmark).setVisible(false);
            menu.findItem(R.id.unbookmark).setVisible(true);
        } else {
            menu.findItem(R.id.bookmark).setVisible(true);
            menu.findItem(R.id.unbookmark).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.unbookmark:

                // Bookmark current course
                FirebaseDatabase.getInstance()
                        .getReference().child(netId).child(courseID)
                        .setValue(null);
                isBookmarked = false;
                setBookmarkOptions();
                break;
            case R.id.bookmark:
                // Bookmark current course
                FirebaseDatabase.getInstance()
                        .getReference().child(netId).child(courseID)
                        .setValue(new Bookmark(courseID));
                isBookmarked = true;
                setBookmarkOptions();
                break;
        }
        return true;
    }
}
