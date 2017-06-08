package com.classified.classified;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.widget.AdapterView.*;


public class HomePage extends ActionBarActivity {

    private EditText search;
    private Spinner spinner;
    private int spinnerVal;
    private ListView openListView;
    private Context context;
    private String[] searchOptions = {"Course Code", "Distribution Area", "Major", "Professor"};
    private String professor;
    private String disArea;
    private String number;
    private String major;
    private String searchUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        this.context = this;
        setTitle("CLASSified");

        search = (EditText) findViewById(R.id.searchQuery);
        openListView = (ListView) findViewById(R.id.openListView);

        // TODO: this should be useful later on
        //openListView.setOnItemClickListener(onListClick);
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(context, chat.class);
            TextView courseID = (TextView) view.findViewById(R.id.courseid);
            TextView courseName = (TextView) view.findViewById(R.id.courseText);
            intent.putExtra("Course_ID", courseID.getText().toString());
            intent.putExtra("Course_Name", courseName.getText().toString());
            //Intent intent = new Intent(context, ClassDetails.class);

            //TextView courseID = (TextView) view.findViewById(R.id.courseid);
            //TextView courseName = (TextView) view.findViewById(R.id.courseText);

            //intent.putExtra("Course_ID", courseID.getText().toString());

            startActivity(intent);

        }
    };

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
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName())); // this is throwing an error

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
