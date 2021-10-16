package com.arcm.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.arcm.demo.databinding.ActivityMainBinding;
import com.arcm.scalenumberpicker.ScaleNumberPicker;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private ScaleNumberPicker scaleNumberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Demo");

        this.scaleNumberPicker = binding.scaleNumberPicker;
        binding.horizontal.setOnClickListener(this::onHorizontalOrientationSelected);
        binding.verticalLeft.setOnClickListener(this::onVerticalLeftOrientationSelected);
        binding.verticalRight.setOnClickListener(this::onVerticalRightOrientationSelected);
        binding.horizontalCircular.setOnClickListener(this::onHorizontalCircularOrientationSelected);
    }

    private void onHorizontalOrientationSelected(View view) {
        Log.i(TAG, "horizontal orientation button clicked");
        scaleNumberPicker.setOrientation(ScaleNumberPicker.HORIZONTAL);
    }

    private void onHorizontalCircularOrientationSelected(View view) {
        Log.i(TAG, "horizontal circular orientation button clicked");
        scaleNumberPicker.setOrientation(ScaleNumberPicker.HORIZONTAL_CIRCULAR);
    }

    private void onVerticalLeftOrientationSelected(View view) {
        Log.i(TAG, "vertical left orientation button clicked");
        scaleNumberPicker.setOrientation(ScaleNumberPicker.VERTICAL_LEFT);
    }

    private void onVerticalRightOrientationSelected(View view) {
        Log.i(TAG, "vertical right orientation button clicked");
        scaleNumberPicker.setOrientation(ScaleNumberPicker.VERTICAL_RIGHT);
    }
}