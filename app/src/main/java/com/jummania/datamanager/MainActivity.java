package com.jummania.datamanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jummania.DataManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.initialize(this);
    }
}