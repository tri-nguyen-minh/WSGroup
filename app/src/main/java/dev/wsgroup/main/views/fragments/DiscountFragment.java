package dev.wsgroup.main.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.wsgroup.main.R;

public class DiscountFragment extends Fragment {

    private int identifier;

    public DiscountFragment(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discount, container, false);
    }
}