package com.jummania.datamanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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

        // DataManagerFactory initialization
        DataManager dataManager = DataManagerFactory.create(getFilesDir());

// Register a listener to display a toast when data changes
        dataManager.registerOnDataChangeListener(key -> Toast.makeText(MainActivity.this, "Data changed: " + key, Toast.LENGTH_SHORT).show());

// Save various types of data
        dataManager.saveString("string", "string");
        dataManager.saveInt("int", 10);
        dataManager.saveLong("long", 1000000L);
        dataManager.saveFloat("float", 10.0f);
        dataManager.saveBoolean("boolean", true);
        dataManager.saveObject("object", new SimpleData(1, "object"));
        dataManager.saveList("key", List.of(new SimpleData(1, "list")));
        dataManager.saveList("key", List.of(new SimpleData(1, "list")), 100);

// Measure time taken to retrieve data
        double beforeGetData = System.currentTimeMillis();

        String string = dataManager.getString("string");
        int i = dataManager.getInt("int");
        long l = dataManager.getLong("long");
        float f = dataManager.getFloat("float");
        boolean b = dataManager.getBoolean("boolean");
        Object o = dataManager.getObject("object", SimpleData.class);
        List<SimpleData> list = dataManager.getList("key", SimpleData.class);
        boolean isContains = dataManager.contains("key");
        List<SimpleData> parameterized = dataManager.getParameterized("key", List.class, SimpleData.class);
        String json = dataManager.toJson(new SimpleData(1, "object"));

// Calculate elapsed time
        double afterGetData = (System.currentTimeMillis() - beforeGetData) / 1000; // Convert to seconds

// Display retrieval stats in a toast
        Toast.makeText(this, "Data retrieved in: " + afterGetData + " seconds\nData Size: " + list.size(), Toast.LENGTH_SHORT).show();

// Display retrieved data in TextView
        TextView textView = findViewById(R.id.textView);


        StringBuilder dataDisplay = new StringBuilder();
        dataDisplay.append("String: ").append(string).append("\n").append("Int: ").append(i).append("\n").append("Long: ").append(l).append("\n").append("Float: ").append(f).append("\n").append("Boolean: ").append(b).append("\n").append("Object: ").append(o).append("\n").append("List: ").append(list).append("\n").append("Contains Key: ").append(isContains).append("\n").append("Parameterized Data: ").append(parameterized).append("\n").append("JSON: ").append(json);
        textView.setText(dataDisplay);

        // Remove and clear data
        dataManager.remove("key");
        dataManager.clear();
        dataManager.unregisterOnDataChangeListener();
    }
}
