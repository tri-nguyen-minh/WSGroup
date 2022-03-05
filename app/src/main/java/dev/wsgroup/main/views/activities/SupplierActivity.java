package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dev.wsgroup.main.R;
import dev.wsgroup.main.views.fragments.tabs.product.DetailTab;

public class SupplierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        this.getSupportActionBar().hide();

    }
}