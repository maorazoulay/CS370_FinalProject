package com.example.coronavirusstatistics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.testfairy.TestFairy;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private TextView response;
    private static List<String> data;


    // TODO: 3/10/2020 build apk then - new upload in testfairy - invite users - get 5 users to test your app
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        TestFairy.begin(this, "SDK-s3agrPwy");
        setContentView(R.layout.activity_my_main);

        Button getDataButton = findViewById(R.id.getDataButton);
        getDataButton.setOnClickListener(v -> {
            EditText initialsBox = findViewById(R.id.stateInitialsTextBox);
            initialsBox.onEditorAction(EditorInfo.IME_ACTION_DONE);
            String stateInitials = initialsBox.getText().toString().toUpperCase();

            String responseString = null;
            String endpoint = "http://coronavirusapi.com/getTimeSeries/%s";


            String preparedRequest = String.format(endpoint, stateInitials);

            try {
                URL url = new URL(preparedRequest);
                InputStream in = (InputStream) url.getContent();
                responseString = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
                in.close();
//                  If an invalid input will be provided by the user then 2 possible messages will be returned - both under length of 41.
//                    in that case: skip manipulation and return that message as it is to let the user know what is wrong with their input.
//                  Else a much larger response will be returned which is the correct one we need - begin manipulation.
                if (responseString.length() <= 40) {
                    throw new InvalidParameterException();
                }

                CSVParser csvRecords = CSVParser.parse(responseString, CSVFormat.DEFAULT);
                List<CSVRecord> records = csvRecords.getRecords();
//                  Get the most up to date record (which is the last one)
                CSVRecord finalRecord = records.get(records.size() - 1);

                responseString = buildResponse(finalRecord);

                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidParameterException e) {
                System.out.println("Wrong input was inserted, check message");
            }

            response = findViewById(R.id.dataTextBox);
            response.setText(responseString);
        });

        Button uploadDataButton = findViewById(R.id.uploadDataButton);
        uploadDataButton.setOnClickListener(v -> {
            if (response == null) {
                response = findViewById(R.id.dataTextBox);
                response.setText("Make sure to get the data first");
                response = null;
                return;
            }
            sendPostRequest(response);
//            response.setText(dbResponse);
        });
    }

    private static String buildResponse(CSVRecord finalRecord) {
        String format = "Last Updated: %s\n" +
                "People Tested: %s\n" +
                "Tested Positive: %s\n" +
                "Number Of Deaths: %s";

        String epochString = finalRecord.get(0);
        String numberOfPeopleTested = finalRecord.get(1);
        String testedPositive = finalRecord.get(2);
        String deaths = finalRecord.get(3);

        Timestamp lastUpdated = convertToHumanDate(epochString);
        String response = String.format(format, lastUpdated.toString(), numberOfPeopleTested,
                testedPositive, deaths);
        data = new ArrayList<>();
        data.add(lastUpdated.toString());
        data.add(numberOfPeopleTested);
        data.add(testedPositive);
        data.add(deaths);
        return response;
    }

    private static Timestamp convertToHumanDate(String epochString) {
        long epoch = Long.parseLong(epochString);
        return new Timestamp(epoch * 1000);
    }

    private void sendPostRequest(TextView textView) {
        String format = "%s,%s,%s,%s";
        String dbData = String.format(format,data.get(0), data.get(1), data.get(2), data.get(3));
        String endpoint = "https://9c99e932.ngrok.io/Azoulay_Maor/AndroidServlet";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, dbData);
        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            textView.setText(response.body().string());

        } catch (IOException e) {
            textView.setText(e.getMessage());
        }
    }
}


