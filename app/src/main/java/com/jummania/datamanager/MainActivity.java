package com.jummania.datamanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jummania.DataManager; // Importing your DataManager library

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the DataManager library with the current context (this Activity)
        DataManager.initialize(this);

        // Retrieve a list of SimpleData objects from DataManager
        List<SimpleData> simpleData = DataManager.getData(SimpleData.class);

        // Check if the retrieved data is empty
        if (simpleData.isEmpty()) {
            // If no data exists, create and add 10 SimpleData objects to the list
            for (int i = 0; i < 10; i++)
                simpleData.add(new SimpleData(i, "simpleString"));

            // Save the newly populated data back to DataManager
            DataManager.saveData(simpleData);
        }

        // Find the TextView in the layout by its ID
        TextView simpleTextView = findViewById(R.id.simpleTextView);

        // Set the text of the TextView with a formatted string of SimpleData items
        simpleTextView.setText(formatSimpleDataList(simpleData));
    }

    // Format the list of SimpleData objects for display in a TextView
    private String formatSimpleDataList(List<SimpleData> simpleDataList) {
        StringBuilder formattedText = new StringBuilder();

        // Iterate over the list of SimpleData objects
        for (SimpleData data : simpleDataList) {
            // Append each SimpleData's string representation to the formatted text
            formattedText.append(data.toString()).append("\n\n");
        }

        // Return the formatted string for display in the TextView
        return formattedText.toString();
    }
}
