package com.example.javatests;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyClass {
    private static List<String> data;

    public static void main(String[] args) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//
        String responseString;
        String endpoint = "http://coronavirusapi.com/getTimeSeries/NY";
        String countryEndpoint = "https://api.covid19api.com/total/country/united%20states";


        try {
            URL url = new URL(endpoint);
//            PlainTextInputStream in = (PlainTextInputStream) url.getContent();
            InputStream in = (InputStream) url.getContent();
            responseString = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            in.close();

//            JSONArray jsonArray = new JSONArray(responseString);
//            JSONObject finalRecord = (JSONObject) jsonArray.get(jsonArray.length() - 1);

            CSVParser csvRecords = CSVParser.parse(responseString, CSVFormat.DEFAULT);
            List<CSVRecord> records = csvRecords.getRecords();
//            Get the most up to date record (which is the last one)
            CSVRecord finalRecord = records.get(records.size() - 1);
            responseString = buildResponse(finalRecord);
            System.out.println(responseString);
//            DBOperations dbOperations = new DBOperations();
//            dbOperations.executeInsert(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildResponse(CSVRecord finalRecord) {
        String format = "Last Updated: %s\n" +
                "People Tested: %s\n" +
                "People Tested Positive: %s\n" +
                "Number Of Deaths: %s";

        String epochString = finalRecord.get(0);
        String numberOfPeopleTested = finalRecord.get(1);
        String testedPositive = finalRecord.get(2);
        String deaths = finalRecord.get(3);

        Date lastUpdated = convertToHumanDate(epochString);
        String response = String.format(format, lastUpdated.toString(), numberOfPeopleTested,
                testedPositive, deaths);
        System.out.println();
        data = new ArrayList<>();
        data.add(epochString);
        data.add(numberOfPeopleTested);
        data.add(testedPositive);
        data.add(deaths);
        return response;
    }

    private static String buildResponse(JSONObject finalRecord) {
        String format = "Last Updated: %s\n" +
                "People Tested: %s\n" +
                "People Tested Positive: %s\n" +
                "Number Of Deaths: %s";

        String dateString = (String) finalRecord.get("Date");
        String numberOfPeopleTested = "N/A";
        String testedPositive = String.valueOf(finalRecord.get("Confirmed"));
        String deaths = String.valueOf(finalRecord.get("Deaths"));
        String lastUpdated = dateString.substring(0, dateString.indexOf('T'));

        String response = String.format(format, lastUpdated, numberOfPeopleTested,
                testedPositive, deaths);
        System.out.println();
        data = new ArrayList<>();
        data.add(dateString);
        data.add(numberOfPeopleTested);
        data.add(testedPositive);
        data.add(deaths);
        return response;
    }


    private static Date convertToHumanDate(String epochString) {
        long epoch = Long.parseLong(epochString);
        return new Date(epoch * 1000);
    }


}
