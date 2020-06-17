package com.agh.edu.pankracy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.agh.edu.pankracy.R;

public class AboutFragment extends Fragment {

    public AboutFragment() {
    }
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_about,
                container, false);
    }
}
