package com.jummania.datamanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.jummania.DataManager;

import java.util.List;

/**
 * The MainActivity class serves as the main entry point for the application,
 * showcasing the use of the DataManager library to manage and display SimpleData objects.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI elements
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button add = findViewById(R.id.add);
        Button clearAll = findViewById(R.id.clearAll);

        // DataManager initialization
        DataManager dataManager = new DataManager(getFilesDir());

        // Retrieve data and display a toast with the time taken in seconds
        long beforeGetData = System.currentTimeMillis();
        List<SimpleData> dataList = dataManager.getData(SimpleData.class);
        long afterGetData = (System.currentTimeMillis() - beforeGetData) / 1000; // Convert to seconds
        Toast.makeText(this, "Data retrieved in: " + afterGetData + " seconds", Toast.LENGTH_SHORT).show();

        // Set up the RecyclerView with the retrieved data
        recyclerView.setAdapter(new Adapter(dataList));

        // Button click listener for adding data
        add.setOnClickListener(v -> {
            // Generate and save new data, displaying the time taken in seconds
            for (int i = 0; i < 99999; i++)
                dataList.add(new SimpleData(i, "simpleString"));

            long beforeSaveData = System.currentTimeMillis();
            dataManager.saveData(dataList, SimpleData.class);
            long afterSaveData = (System.currentTimeMillis() - beforeSaveData) / 1000; // Convert to seconds
            Toast.makeText(MainActivity.this, "Data saved in: " + afterSaveData + " seconds", Toast.LENGTH_SHORT).show();
        });

        // Button click listener for clearing all data
        clearAll.setOnClickListener(v -> {
            // Clear all data and display the time taken in seconds
            long beforeClearAll = System.currentTimeMillis();
            dataManager.clearAll();
            long afterClearAll = (System.currentTimeMillis() - beforeClearAll) / 1000; // Convert to seconds
            Toast.makeText(MainActivity.this, "All data cleared in: " + afterClearAll + " seconds", Toast.LENGTH_SHORT).show();
        });
    }
}
