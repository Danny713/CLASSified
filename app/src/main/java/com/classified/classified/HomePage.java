package com.classified.classified;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomePage extends ActionBarActivity {

    private String netid;
    private ListView openListView;
    private Context context;
    private CustomAdapter customAdapter;
    private Set<String> bookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        this.context = this;
        setTitle("CLASSified");

        netid = getIntent().getStringExtra("net_id");
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookmarks = new HashSet<>();
        initBookmarks();
    }

    // TODO: this needs courseID, courseNAme
    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(context, chat.class);
            ClassInfo classInfo = (ClassInfo) parent.getItemAtPosition(position);

            intent.putExtra("Course_ID", classInfo.getCourseId());
            intent.putExtra("Course_Name", classInfo.getCourseCode());
            intent.putExtra("NET_ID", netid);

            startActivity(intent);

        }
    };

    private void initBookmarks() {
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(netid);
        databaseRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    bookmarks.add(ds.getValue(Bookmark.class).getCourseId());
                }
                initListView();
            }
        });
    }

    private void initListView() {
        // work to get the new listview
        try {
            String courseJsonString = getJsonString();
            JSONObject courseJsonObject = new JSONObject(courseJsonString);
            JSONArray coursesArray = courseJsonObject.getJSONArray("courses"); // this contains all the courses
            List<ClassInfo> infoList = new ArrayList<>();
            for (int i = 0; i < coursesArray.length(); i++) {
                JSONObject courseObject = coursesArray.getJSONObject(i);
                ClassInfo classInfo = new ClassInfo(courseObject);
                if (bookmarks.size() > 0 && !bookmarks.contains(classInfo.getCourseId())) {
                    continue;
                }
                infoList.add(classInfo);
            }
            openListView = (ListView) findViewById(R.id.openListView);
            customAdapter = new CustomAdapter(this, infoList);
            openListView.setAdapter(customAdapter);
        } catch (JSONException e) {
            Log.d("json", e.toString());
        }
        // TODO: this should be useful later on
        openListView.setOnItemClickListener(onListClick);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search by Class code, name or professor");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SearchResultsActivity", "hello?");
                customAdapter.getFilter().filter(query.replaceAll("\\s+","").toLowerCase());
                customAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText.replaceAll("\\s+","").toLowerCase());
                customAdapter.notifyDataSetChanged();
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("item selected", "" + id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getJsonString() {
        String json = null;
        try {
            Resources res = getResources();
            InputStream is = res.openRawResource(R.raw.courses_1182_nodup); // reading in the json

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.d("d", ex.toString());
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}