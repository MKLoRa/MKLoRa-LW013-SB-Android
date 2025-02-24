package com.moko.lw013sb.activity.device;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.databinding.Lw013ActivityIndicatorSettingsBinding;
import com.moko.lw013sb.utils.ToastUtils;
import com.moko.support.lw013sb.LoRaLW013SBMokoSupport;
import com.moko.support.lw013sb.OrderTaskAssembler;
import com.moko.support.lw013sb.entity.OrderCHAR;
import com.moko.support.lw013sb.entity.ParamsKeyEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndicatorSettingsActivity extends BaseActivity {

    private Lw013ActivityIndicatorSettingsBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    private int mAlarmState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityIndicatorSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());

        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        mBind.cbLowPower.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getIndicatorStatus());
            LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 200)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (!MokoConstants.ACTION_CURRENT_DATA.equals(action))
            EventBus.getDefault().cancelEventDelivery(event);
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
            }
            if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
                dismissSyncProgressDialog();
            }
            if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
                OrderTaskResponse response = event.getResponse();
                OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
                int responseType = response.responseType;
                byte[] value = response.responseValue;
                switch (orderCHAR) {
                    case CHAR_PARAMS:
                        if (value.length >= 5) {
                            int header = value[0] & 0xFF;// 0xED
                            int flag = value[1] & 0xFF;// read or write
                                int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
                            if (header != 0xED)
                                return;
                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                            if (configKeyEnum == null) {
                                return;
                            }
                            int length = value[4] & 0xFF;
                            if (flag == 0x01) {
                                // write
                                int result = value[5] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_INDICATOR_STATUS:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(IndicatorSettingsActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Save Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_INDICATOR_STATUS:
                                        if (length > 0) {
                                            byte[] indicatorBytes = Arrays.copyOfRange(value, 5, 5 + length);
                                            int indicator = MokoUtils.toInt(indicatorBytes);
                                            mBind.cbDeviceState.setChecked((indicator & 1) == 1);
                                            mAlarmState = indicator & 2;
                                            mBind.cbFix.setChecked((indicator & 4) == 4);
                                            mBind.cbFixSuccess.setChecked((indicator & 8) == 8);
                                            mBind.cbFixFail.setChecked((indicator & 16) == 16);
                                            mBind.cbNetworkCheck.setChecked((indicator & 32) == 32);
                                            mBind.cbFullCharge.setChecked((indicator & 64) == 64);
                                            mBind.cbCharging.setChecked((indicator & 128) == 128);
                                            mBind.cbLowPower.setChecked((indicator & 256) == 256);
                                            mBind.cbBleAdvCheck.setChecked((indicator & 512) == 512);
                                        }
                                        break;
                                }
                            }
                        }
                        break;
                }
            }
        });
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            dismissSyncProgressDialog();
                            finish();
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }


    public void onBack(View view) {
        backHome();
    }

    @Override
    public void onBackPressed() {
        backHome();
    }

    private void backHome() {
        setResult(RESULT_OK);
        finish();
    }

    public void onSave(View view) {
        if (isWindowLocked())
            return;
        int indicator = (mBind.cbDeviceState.isChecked() ? 1 : 0)
                | mAlarmState
                | (mBind.cbFix.isChecked() ? 4 : 0)
                | (mBind.cbFixSuccess.isChecked() ? 8 : 0)
                | (mBind.cbFixFail.isChecked() ? 16 : 0)
                | (mBind.cbNetworkCheck.isChecked() ? 32 : 0)
                | (mBind.cbFullCharge.isChecked() ? 64 : 0)
                | (mBind.cbCharging.isChecked() ? 128 : 0)
                | (mBind.cbLowPower.isChecked() ? 256 : 0)
                | (mBind.cbBleAdvCheck.isChecked() ? 512 : 0);
        savedParamsError = false;
        showSyncingProgressDialog();
        LoRaLW013SBMokoSupport.getInstance().sendOrder(OrderTaskAssembler.setIndicatorStatus(indicator));
    }
}
