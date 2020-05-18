package com.example.coronavirusstatistics;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView response;
    private static List<String> data;
    private static String stateInitials;
    private String stateEndpoint = "http://coronavirusapi.com/getTimeSeries/%s";
    private String countryEndpoint = "https://api.covid19api.com/total/country/united%20states";
    private static final String RESPONSE_FORMAT = "Last Updated: %s\n" +
            "Tested: %s\n" +
            "Tested Positive: %s\n" +
            "Number Of Deaths: %s";


    // TODO: 3/10/2020 build apk then - new upload in testfairy - invite users - get 5 users to test your app
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);

        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateInitials = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                stateInitials = null;
            }
        });

        Button getDataButton = findViewById(R.id.getDataButton);
        getDataButton.setOnClickListener(v -> {
//            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();

            String responseString = null;
            String preparedRequest;
            boolean isCountry = stateInitials.equalsIgnoreCase("US");
            if (isCountry) {
                preparedRequest = countryEndpoint;
            } else {
                preparedRequest = String.format(stateEndpoint, stateInitials);
            }

            try {
                URL url = new URL(preparedRequest);
                InputStream in = (InputStream) url.getContent();
                responseString = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
                in.close();
//                  If an invalid input will be provided by the user then 2 possible messages will be returned - both under length of 41.
//                    in that case: skip manipulation and return that message as it is to let the user know what is wrong with their input.
//                  Else a much larger response will be returned which is the correct one we need, in that case - begin manipulation.
                if (responseString.length() <= 40) {
                    throw new InvalidParameterException();
                }

                if (isCountry) {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject finalRecord = (JSONObject) jsonArray.get(jsonArray.length() - 1);
                    responseString = buildResponse(finalRecord);
                } else {
                    CSVParser csvRecords = CSVParser.parse(responseString, CSVFormat.DEFAULT);
                    List<CSVRecord> records = csvRecords.getRecords();
//                  Get the most up to date record (which is the last one)
                    CSVRecord finalRecord = records.get(records.size() - 1);
                    responseString = buildResponse(finalRecord);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (InvalidParameterException e) {
                System.out.println("Wrong input was inserted, check message");
            }
            response = findViewById(R.id.dataTextBox);
            response.setText(responseString);
        });
    }

    private static String buildResponse(CSVRecord finalRecord) {
        String epochString = finalRecord.get(0);
        String numberOfPeopleTested = finalRecord.get(1);
        String testedPositive = finalRecord.get(2);
        String deaths = finalRecord.get(3);

        Timestamp lastUpdated = convertToHumanDate(epochString);
        String timestampString = lastUpdated.toString();
        String dateString = timestampString.substring(0, timestampString.indexOf(" "));
        String response = String.format(RESPONSE_FORMAT, dateString, numberOfPeopleTested,
                testedPositive, deaths);
        data = new ArrayList<>();
        data.add(dateString);
        data.add(numberOfPeopleTested);
        data.add(testedPositive);
        data.add(deaths);
        return response;
    }

    private static String buildResponse(JSONObject finalRecord) {
        String dateString;
        String numberOfPeopleTested = "N/A";
        String testedPositive;
        String deaths;
        String lastUpdated;
        try {
            dateString = (String) finalRecord.get("Date");
            testedPositive = String.valueOf(finalRecord.get("Confirmed"));
            deaths = String.valueOf(finalRecord.get("Deaths"));
            lastUpdated = dateString.substring(0, dateString.indexOf('T'));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        String response = String.format(RESPONSE_FORMAT, lastUpdated, numberOfPeopleTested,
                testedPositive, deaths);
        data = new ArrayList<>();
        data.add(dateString);
        data.add(numberOfPeopleTested);
        data.add(testedPositive);
        data.add(deaths);
        return response;
    }

    private static Timestamp convertToHumanDate(String epochString) {
        long epoch = Long.parseLong(epochString);
//        Timestamp timestamp = new Timestamp(epoch * 1000);
//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//
//        Date today = new Date();
//
//        Date todayWithZeroTime = formatter.parse(formatter.format(today));
        return new Timestamp(epoch * 1000);
    }
}


