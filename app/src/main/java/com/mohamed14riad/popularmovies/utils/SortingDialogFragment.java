package com.mohamed14riad.popularmovies.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mohamed14riad.popularmovies.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SortingDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {
    private RadioButton mostPopular = null;
    private RadioButton highestRated = null;
    private RadioButton favorites = null;
    private RadioGroup sortingOptionsGroup = null;

    private static SortingOptionsListener optionsListener = null;
    private static String selectedOption = null;

    private static final String API_KEY = AppConstants.API_KEY;

    public interface SortingOptionsListener {
        void onSortingOptionSelect(String selectedOption);
    }

    public static SortingDialogFragment newInstance(SortingOptionsListener optionsListener, String selectedOption) {
        SortingDialogFragment.optionsListener = optionsListener;
        SortingDialogFragment.selectedOption = selectedOption;
        return new SortingDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.sorting_options, null);

        mostPopular = (RadioButton) dialogView.findViewById(R.id.most_popular);
        highestRated = (RadioButton) dialogView.findViewById(R.id.highest_rated);
        favorites = (RadioButton) dialogView.findViewById(R.id.favorites);
        sortingOptionsGroup = (RadioGroup) dialogView.findViewById(R.id.sorting_group);

        initViews();

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(dialogView);
        dialog.setTitle(R.string.sort_by);
        dialog.show();

        return dialog;
    }

    private void initViews() {
        if (API_KEY.isEmpty()) {
            mostPopular.setEnabled(false);
            highestRated.setEnabled(false);
            favorites.setEnabled(false);
        } else {
            if (selectedOption.equals(getString(R.string.most_popular))) {
                mostPopular.setChecked(true);
            } else if (selectedOption.equals(getString(R.string.highest_rated))) {
                highestRated.setChecked(true);
            } else if (selectedOption.equals(getString(R.string.favorites))) {
                favorites.setChecked(true);
            }

            sortingOptionsGroup.setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.most_popular:
                optionsListener.onSortingOptionSelect(getString(R.string.most_popular));
                dismiss();
                break;
            case R.id.highest_rated:
                optionsListener.onSortingOptionSelect(getString(R.string.highest_rated));
                dismiss();
                break;
            case R.id.favorites:
                optionsListener.onSortingOptionSelect(getString(R.string.favorites));
                dismiss();
                break;
        }
    }
}
