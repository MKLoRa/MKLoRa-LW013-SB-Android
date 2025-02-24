package com.moko.lw013sb.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moko.ble.lib.task.OrderTask;
import com.moko.lw013sb.R;
import com.moko.lw013sb.activity.DeviceInfoActivity;
import com.moko.lw013sb.databinding.Lw013FragmentPosBinding;
import com.moko.support.lw013sb.LoRaLW013SBMokoSupport;
import com.moko.support.lw013sb.OrderTaskAssembler;

import java.util.ArrayList;

public class PositionFragment extends Fragment {
    private static final String TAG = PositionFragment.class.getSimpleName();
    private Lw013FragmentPosBinding mBind;
    private DeviceInfoActivity activity;
    private boolean mExtremeModeEnable;
    private boolean mVoltageReportEnable;

    public PositionFragment() {
    }


    public static PositionFragment newInstance() {
        PositionFragment fragment = new PositionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mBind = Lw013FragmentPosBinding.inflate(inflater, container, false);
        activity = (DeviceInfoActivity) getActivity();
        return mBind.getRoot();
    }

    public void setExtremeModeEnable(int enable) {
        mExtremeModeEnable = enable == 1;
        mBind.ivGPSExtremeMode.setImageResource(mExtremeModeEnable ? R.drawable.lw013_ic_checked : R.drawable.lw013_ic_unchecked);
    }

    public void setVoltageReportEnable(int enable) {
        mVoltageReportEnable = enable == 1;
        mBind.ivVoltageReport.setImageResource(mVoltageReportEnable ? R.drawable.lw013_ic_checked : R.drawable.lw013_ic_unchecked);
    }



    public void changeExtremeMode() {
        mExtremeModeEnable = !mExtremeModeEnable;
        activity.showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setGPSExtremeModeL76C(mExtremeModeEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getGPSExtremeModeL76());
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
    public void changeVoltageReport() {
        mVoltageReportEnable = !mVoltageReportEnable;
        activity.showSyncingProgressDialog();
        ArrayList<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setVoltageReportEnable(mVoltageReportEnable ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.getVoltageReportEnable());
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
