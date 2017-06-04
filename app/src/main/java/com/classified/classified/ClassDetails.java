package com.classified.classified;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ScrollView;
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


public class ClassDetails extends ActionBarActivity {

    Context context;

    String courseID;
    String url;

    TextView courseCode;
    TextView PDF;
    TextView className;
    TextView professor;
    TextView description;
    TextView assignment;
    TextView grade;
    TextView req;
    TextView prereq;
    TextView info;

    ListView precept;

    String[] result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_details);

        this.context = this;

        Intent i = getIntent();
        courseID = i.getStringExtra("Course_ID");

        courseCode = (TextView) findViewById(R.id.course_id);
        PDF = (TextView) findViewById(R.id.pdf_id);
        className = (TextView) findViewById(R.id.className_id);
        professor = (TextView) findViewById(R.id.professor_id);

        description = (TextView) findViewById(R.id.descriptionScroll);
        description.setMovementMethod(new ScrollingMovementMethod());

        assignment = (TextView) findViewById(R.id.assignmentScroll);
        assignment.setMovementMethod(new ScrollingMovementMethod());

        grade = (TextView) findViewById(R.id.gradingScroll);
        grade.setMovementMethod(new ScrollingMovementMethod());

        req = (TextView) findViewById(R.id.reqScroll);
        req.setMovementMethod(new ScrollingMovementMethod());

        prereq = (TextView) findViewById(R.id.prereqScroll);
        prereq.setMovementMethod(new ScrollingMovementMethod());

        info = (TextView) findViewById(R.id.informationScroll);
        info.setMovementMethod(new ScrollingMovementMethod());

        precept = (ListView) findViewById(R.id.preceptList);

        result = new String[10];
        // 0 -course code
        // 1 - PDF
        // 2 - className
        // 3- professor
        // 4- description
        // 5 - reading/writing assignment
        // 6 - grading
        // 7 - other requirements

        // 8 - prereq
        // 9 - other info

        new getAsyncTask().execute();
    }

    private class getAsyncTask extends AsyncTask <Void, Void, ArrayList<String[]>> {
        protected ArrayList<String[]> doInBackground(Void... params) {

            ArrayList<String[]> arrayList = new ArrayList<String[]>();
            //0 - section
            //1 - days
            //2 - time

            //3 - enrolled
            //4 - limit
            //5 - status


            url = "https://registrar.princeton.edu/course-offerings/course_details.xml?courseid="+courseID+"&term=1164";
            //url "https://registrar.princeton.edu/course-offerings/course_details.xml?courseid=008025&term=1164";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);

            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line = reader.readLine();

                int counterStrong = 0;
                int counterH2 = 0;
                int counterEM = 0;
                int counterTR = 0;

                while (line != null) {

                    if (line.contains("<strong>") && !line.contains("<p>")) {
                        counterStrong++;
                        if (counterStrong == 2) {
                            StringBuffer sb = new StringBuffer();
                            while (!(line = reader.readLine()).contains("</strong>")) {
                                if (!line.matches(" *"))
                                    sb.append(line);
                            }
                            result[0] = sb.toString();
                        } else if (line.contains("Reading/Writing assignments:")) {
                            StringBuffer sBuffer = new StringBuffer();

                            while (!(line = reader.readLine()).contains("<br>")) {
                                if (!line.matches(" *"))
                                    sBuffer.append(line);
                            }
                            result[5] = sBuffer.toString();
                        } else if (line.contains("Other Requirements:")) {
                            StringBuffer strBuffer = readInfo(reader, "<br>");

                            result[7] = strBuffer.toString();
                        } else if (line.contains("Prerequisites and Restrictions:")) {
                            StringBuffer stBuffer = readInfo(reader, "<br>");

                            result[8] = stBuffer.toString();
                        } else if (line.contains("Other information:")) {
                            StringBuffer sBuffer = readInfo(reader, "<br>");

                            result[9] = sBuffer.toString();
                        }

                    } else if (line.contains("</em><br>")) {
                        counterEM++;
                        if (counterEM == 1) {
                            String[] array = line.split("<");
                            result[1] = array[0].trim();
                        }
                    } else if (line.contains("<h2>")) {
                        counterH2++;
                        String finalString;
                        if (counterH2 == 2) {
                            String[] array = line.split(">");
                            finalString = array[1];
                            if (finalString.contains("&amp;"))
                                finalString = finalString.replaceAll("&amp;", "&");
                            result[2] = finalString;
                        }
                    } else if (line.contains("id=\"descr")) {
                        StringBuffer stringB = new StringBuffer();
                        while (!(line = reader.readLine()).contains("</div")) {
                            if (line.contains("<"))
                                stringB.append(line.split("<")[0]);
                            else
                                stringB.append(line);
                        }

                        result[4] = stringB.toString();

                    } else if (line.contains("<p><strong>")) {
                        reader.readLine();

                        StringBuffer stringBuffer = readNames(reader);
                        StringBuffer moreNames;

                        while (!(line = reader.readLine()).contains("</p")) {
                            moreNames = new StringBuffer();
                            stringBuffer.append("\n");
                            reader.readLine();
                            moreNames= readNames(reader);
                            stringBuffer.append(moreNames.toString());
                        }
                        result[3] = stringBuffer.toString();
                    } else if (line.contains("Requirements/Grading:")) {
                        reader.readLine();

                        StringBuffer strBuf = new StringBuffer();

                        while (!(line = reader.readLine()).matches("<br>")) {
                            if (!line.matches(" *")) {
                                strBuf.append(line.split("<")[0]);
                                strBuf.append("\n");
                            }
                        }
                        result[6] = strBuf.toString();
                    } else if (line.contains("<tr")) {
                        counterTR++;
                        if (counterTR > 1) {
                            String[] array = new String[6];
                            int counterTD = 0;
                            while (!line.contains("</tr>")) {
                                if (line.contains("<td><strong>")){
                                    counterTD++;
                                    if (counterTD == 2)
                                        array[0] = line.replaceAll("[^0-9A-Z]", "");
                                    else if (counterTD == 3) {
                                        if (line.contains("</strong>"))
                                            array[1] = "TBA";
                                        else {
                                            StringBuffer striBuffer = new StringBuffer();
                                            while (!(line = reader.readLine()).contains("</strong>")) {
                                                if (!line.matches(" *"))
                                                    striBuffer.append(line);
                                            }
                                            array[1] = striBuffer.toString();
                                        }
                                    }
                                    else if (line.contains("Enrolled:")) {
                                        array[3] = line.replaceAll("[^0-9]", "");
                                    }
                                }
                                else if (line.contains("<td nowrap>")) {
                                    StringBuffer buffer = new StringBuffer();

                                    if (!line.matches("<td nowrap>"))
                                        buffer.append(line.split(">")[1].trim());
                                    line = reader.readLine();
                                    buffer.append(line.trim());
                                    line = reader.readLine();
                                    if (!line.matches("</td>"))
                                        buffer.append(line.split("<")[0].trim());

                                    array[2] = buffer.toString();
                                }
                                else if (line.contains("Limit:")) {
                                    array[4] = line.replaceAll("[^0-9]", "");
                                }
                                else if (line.contains("Closed") || (line.contains("Canceled"))) {
                                    array[5] = line.trim();
                                }
                                line = reader.readLine();
                            }
                            arrayList.add(array);
                        }
                    }

                    line = reader.readLine();
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
        return arrayList;
        }

        protected void onPostExecute(ArrayList<String[]> arrayList) {
            courseCode.setText(result[0]);
            PDF.setText(result[1]);
            className.setText(result[2]);
            professor.setText(result[3]);
            description.setText(result[4]);
            assignment.setText(result[5]);
            grade.setText(result[6]);
            req.setText(result[7]);
            prereq.setText(result[8]);
            info.setText(result[9]);

            PreceptAdapter adapter = new PreceptAdapter(context, arrayList);
            precept.setAdapter(adapter);
        }
    }

    private StringBuffer readInfo(BufferedReader reader, String regex) {
        String line;
        StringBuffer stringBuffer = new StringBuffer();

        try {
            while (!(line = reader.readLine()).matches(regex)) {
                if (!line.matches(" *")) {
                    if (line.contains(regex)) {
                        stringBuffer.append(line.split("<")[0]);
                        break;
                    }
                    else
                        stringBuffer.append(line);
                }
            }
        }catch (IOException e) {
        }
        return stringBuffer;
    }

    private StringBuffer readNames(BufferedReader reader) {
        String line;
        StringBuffer stringBuffer = new StringBuffer();

        try {
            while (!(line = reader.readLine()).contains("</a></strong><br>")) {
                if (!line.matches(" *")) {
                    stringBuffer.append(line.trim());
                    stringBuffer.append(" ");
                }
            }
        }catch (IOException e) {
        }
        return stringBuffer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_class_details, menu);
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
