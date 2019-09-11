package com.practice.solarcalculator.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.practice.solarcalculator.R;
import com.practice.solarcalculator.db.model.RecentLocation;
import com.practice.solarcalculator.utils.DateUtils;
import com.practice.solarcalculator.utils.Logger;
import com.practice.solarcalculator.utils.SunriseCalculation;

import java.util.Date;

public class CalculationBottomDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {

    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy";
    private static final String EXTRAS_PARCEL_DATA = "extra_parcel_data";
    // this object used for all calculation and formatting.
    // default set current date.
    private Date mCurrentDate = new Date();
    private RecentLocation location;
    private TextView mSunriseTextView;
    private TextView mSunsetTextView;
    private TextView mDateTextView;

    public static CalculationBottomDialogFragment getInstance(RecentLocation location) {
        CalculationBottomDialogFragment fragment = new CalculationBottomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRAS_PARCEL_DATA, location);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_dialog_calculation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            location = getArguments().getParcelable(EXTRAS_PARCEL_DATA);
        }

        Logger.info("Calculation object %s", location);

        view.findViewById(R.id.imageButton_rewind).setOnClickListener(this);
        view.findViewById(R.id.imageButton_play).setOnClickListener(this);
        view.findViewById(R.id.imageButton_forward).setOnClickListener(this);

        mSunriseTextView = view.findViewById(R.id.textView_sunrise);
        mSunsetTextView = view.findViewById(R.id.textView_sunset);
        mDateTextView = view.findViewById(R.id.textView_date);

        invalidateTimes();
        invalidateDate();
    }

    /**
     * This will update the dates and respective time for sunrise/sunset.
     * and set the new time for rise and set.
     */
    private void invalidateTimes() {
        SunriseCalculation calculation = new SunriseCalculation(
                mCurrentDate.getDate(),
                mCurrentDate.getMonth(),
                mCurrentDate.getYear(),
                location.getLongitude(),
                location.getLatitude()
        );

        calculation.calculateOfficialSunriseSunset();
        String sunrise = DateUtils.convertDateFormat(calculation.getOfficialSunrise(), "hh:mm a");
        String sunset = DateUtils.convertDateFormat(calculation.getOfficialSunset(), "hh:mm a");

        mSunriseTextView.setText(sunrise);
        mSunsetTextView.setText(sunset);
    }

    /**
     * This will update the dates and respective time for sunrise/sunset.
     * According values in respective objects.
     */
    private void invalidateDate() {
        mDateTextView.setText(DateUtils.convertDateFormat(mCurrentDate, DATE_FORMAT));
    }

    /**
     * Update the {@link #mCurrentDate} date according to passing argument.
     *
     * @param by is number that want to be add in date.
     */
    private void updateDateBy(int by) {
        mCurrentDate.setDate(mCurrentDate.getDate() + by);
        invalidateDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_rewind:
                updateDateBy(-1);
                break;
            case R.id.imageButton_play:
                mCurrentDate = new Date();
                invalidateDate();
                break;
            case R.id.imageButton_forward:
                updateDateBy(1);
                break;
        }
    }
}
