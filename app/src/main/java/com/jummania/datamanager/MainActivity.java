package com.jummania.datamanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.jummania.DataManager;
import com.jummania.DataManagerFactory;

import java.util.List;

/**
 * The MainActivity class serves as the main entry point for the application,
 * showcasing the use of the DataManagerFactory library to manage and display SimpleData objects.
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

        // DataManagerFactory initialization
        DataManager dataManager = DataManagerFactory.create(getFilesDir());

        // Retrieve data and display a toast with the time taken in seconds
        double beforeGetData = System.currentTimeMillis();
        List<SimpleData> dataList = dataManager.getList("key", SimpleData.class);
        double afterGetData = (System.currentTimeMillis() - beforeGetData) / 1000; // Convert to seconds
        Toast.makeText(this, "Data retrieved in: " + afterGetData + " seconds\nData Size: " + dataList.size(), Toast.LENGTH_SHORT).show();

        // Set up the RecyclerView with the retrieved data
        Adapter adapter = new Adapter(dataList);
        recyclerView.setAdapter(adapter);

        // Button click listener for adding data
        add.setOnClickListener(v -> {
            // Generate and save new data, displaying the time taken in seconds
            for (int i = 0; i < 99999; i++)
                dataList.add(new SimpleData(i, "simpleString"));

            double beforeSaveData = System.currentTimeMillis();
            dataManager.saveList("key", dataList);
            double afterSaveData = (System.currentTimeMillis() - beforeSaveData) / 1000; // Convert to seconds
            Toast.makeText(MainActivity.this, "Data saved in: " + afterSaveData + " seconds", Toast.LENGTH_SHORT).show();
            adapter.notifyItemInserted(adapter.getItemCount());
        });

        // Button click listener for clearing all data
        clearAll.setOnClickListener(v -> {
            // Clear all data and display the time taken in seconds
            long beforeClearAll = System.currentTimeMillis();
            dataManager.clear();
            long afterClearAll = (System.currentTimeMillis() - beforeClearAll) / 1000; // Convert to seconds
            Toast.makeText(MainActivity.this, "All data cleared in: " + afterClearAll + " seconds", Toast.LENGTH_SHORT).show();
            dataList.clear();
            adapter.notifyDataSetChanged();
        });
    }
}
