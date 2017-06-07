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

        // addListenerOnSpinner();

        openListView.setOnItemClickListener(onListClick);
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

    public void onClickSearch(View view) {
        String s = search.getText().toString();
        if (s.equals(""))
            return;

        new ServletGetAsyncTask().execute(s);
    }

    private class ServletGetAsyncTask extends AsyncTask<String, Void, ArrayList<String[]>> {
    //private class ServletGetAsyncTask extends AsyncTask<String, Void, Integer> {
        int arrayLength = 6;

        protected ArrayList<String[]> doInBackground(String... params) {
            String param = params[0]; // param is the input String

            ArrayList<String[]> openList = new ArrayList<String[]>();

            HttpClient httpclient = new DefaultHttpClient();

            professor = "";
            disArea = "";
            number = "";
            major = "";

            if (!param.matches("^[a-zA-Z0-9 ]*$"))
                return openList;

            if (spinnerVal == 0) {
                param = param.trim();

                String[] array   = param.split(" ");
                String userInput = "";

                for (int i = 0; i < array.length; i++)
                    userInput += array[i].trim();

                if (userInput.length() < 6)
                    major = "Unsupported_Major_73192";
                else {
                    major = userInput.substring(0, 3).toUpperCase();
                    number = userInput.substring(3, 6);
                }
            }
            else if (spinnerVal == 1) {
                String[] array = param.split (" ");
                for (int i = 0; i < array.length; i++) {
                    disArea += array[i].trim().toUpperCase();
                }
            }
            else if (spinnerVal == 2) {
                String[] array = param.split(" ");
                for (int i = 0; i < array.length; i++) {
                    major += array[i].trim().toUpperCase();
                }
                int length = major.length();
                if (length > 3) {
                    major = "Unsupported_Major_73192";
                }
                else if (length < 3) {
                    major = "Unsupported_Major_00000";
                }
            }
            else {
                if (param.matches(" *"))
                    professor = "Unsupported_Professor_00000";
                else {
                    String[] array = param.trim().split(" ");
                    professor = array[0];
                    for (int i = 1; i < array.length; i++)
                        professor += "+" + array[i];
                }
            }

            searchUrl = "https://registrar.princeton.edu/course-offerings/search_results.xml?" +
                    "submit=Search&term=1182&instructor=" + professor + "&distr_area=" + disArea +
                    "&cat_number=" + number + "&subject=" + major + "&sort=SYN_PS_PU_ROXEN_SOC_VW.SUBJECT%2C+" +
                    "SYN_PS_PU_ROXEN_SOC_VW.CATALOG_NBR%2CSYN_PS_PU_ROXEN_SOC_VW.CLASS_SECTION%2CSYN_" +
                    "PS_PU_ROXEN_SOC_VW.CLASS_MTG_NBR&submit=Search";
            HttpGet httpget = new HttpGet(searchUrl);
            // String resString = "";

            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent(); // Create an InputStream with the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                String[] array;

                while (line != null) { // Read line by line

                    if (line.contains("<td><strong>")) {
                        array = new String[arrayLength];
                        int counter = 0;
                        boolean open = false;
                        while ((!(line = reader.readLine()).contains("</html>")) && !(line.contains("<td><strong>"))) {
                            if (line.contains("<td")) {
                                ++counter;
                                if (counter == 10) {
                                    if (line.contains("</td>")) open = true;
                                } else if (counter == 1) {
                                    int index = line.indexOf("courseid=");
                                    String courseId = line.substring(index + 9, index + 15);
                                    array[3] = courseId;
                                    array[0] = reader.readLine();
                                } else if (counter == 2) {
                                    array[1] = reader.readLine();
                                } else if (counter == 3) {
                                    array[2] = reader.readLine().trim();

                                } else if (counter == 5) {
                                    String s = "";
                                    while (!(line = reader.readLine()).contains("</td>")) {
                                        if (line.length() != 0) {
                                            if (line.contains("Th")) s += "R";
                                            else s += line;
                                        }
                                    }
                                    if (s.equals("TBA")) s = "---";
                                    array[4] = s;
                                } else if (counter == 6) {
                                    if (line.length() <= 14) {
                                        array[5] = "---";
                                    } else {
                                        String firstMilitary;
                                        String secondMilitary;

                                        String s1 = line.substring(14, 19);
                                        String[] firstTime = s1.split(":");
                                        if (line.contains("pm") && !firstTime[0].equals("12")) {

                                            int x = Integer.parseInt(firstTime[0].trim());
                                            x += 12;
                                            firstMilitary = "" + x + firstTime[1].substring(0, 2);
                                        } else
                                            firstMilitary = firstTime[0].trim() + firstTime[1].substring(0, 2);

                                        reader.readLine();  // read in the hyphen

                                        line = reader.readLine();
                                        String s2 = line.substring(0, 5);
                                        String[] secondTime = s2.split(":");
                                        if (line.contains("pm") && !secondTime[0].equals("12")) {
                                            int x = Integer.parseInt(secondTime[0].trim());
                                            x += 12;
                                            secondMilitary = "" + x + secondTime[1].substring(0, 2);
                                        } else
                                            secondMilitary = secondTime[0].trim() + secondTime[1].substring(0, 2);

                                        array[5] = firstMilitary + "," + secondMilitary;
                                    }
                                } else continue;
                            }
                        }
                        openList.add(array);
                    }
                    if (line != null && !line.contains("<td><strong>")) line = reader.readLine();
                    if (line == null || line.contains("</html>")) break;
                }
                is.close(); // Close the stream
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            // return counter;
            return openList;
        }
        @Override
        protected void onPostExecute(ArrayList<String[]> result) {
            int counter = 0;
            int empty = 0;

            CustomAdapter adapter = new CustomAdapter(context, result);
            openListView.setAdapter(adapter);

            if (spinnerVal == 2 ) {
                if (major.equals("Unsupported_Major_73192")) Toast.makeText(context, "Please use abbreviations (ex. COS)", Toast.LENGTH_LONG).show();
                else if (major.equals("Unsupported_Major_00000")) Toast.makeText(context, "Unrecognizable major format", Toast.LENGTH_LONG).show();
            }
            else if (spinnerVal == 3) {
                if (professor.equals("Unsupported_Professor_00000")) Toast.makeText(context, "Enter a professor", Toast.LENGTH_LONG).show();
                else if (professor.contains("+")) Toast.makeText(context, "You are searching multiple professors", Toast.LENGTH_LONG).show();
            }
            else if (spinnerVal == 1) {
                if (disArea.length() > 2 && (!disArea.equals("STN") && !disArea.equals("STL"))) Toast.makeText(context, "Please use abbreviations (ex. QR)", Toast.LENGTH_LONG).show();
            }
            else {
                if (major.equals("Unsupported_Major_73192")) Toast.makeText(context, "Unrecognizable format", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addListenerOnSpinner() {

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerVal = position;
                if (spinnerVal == 0) {
                    search.setHint("ex. COS 126");
                }
                else if (spinnerVal == 1) {
                    search.setHint("ex. QR");
                }

                else if (spinnerVal == 2) {
                    search.setHint("ex. COS");
                }
                else {
                    search.setHint("ex. Sedgewick");
                }

                if (!search.getText().toString().equals("")) {
                    // re-search with new spinner value
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerVal = 0;
            }
        });
    }

    public class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(Context context, int textViewResourceId,
                         String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getDropDownView(int position, View connvertView, ViewGroup parent){
            LayoutInflater inflater = getLayoutInflater();
            View spinnerItem = inflater.inflate(android.R.layout.simple_spinner_item, null);

            TextView mytext= (TextView)spinnerItem.findViewById(android.R.id.text1);
            mytext.setText(searchOptions[position]);

            int selected = spinner.getSelectedItemPosition();
            if(position == selected){
                spinnerItem.setBackgroundColor(Color.rgb(185, 224, 181));
            }
            return spinnerItem;
        }
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
