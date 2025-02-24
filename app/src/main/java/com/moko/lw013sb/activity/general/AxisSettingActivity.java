package com.moko.lw013sb.activity.general;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.databinding.Lw013ActivityAxisSettingBinding;
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

public class AxisSettingActivity extends BaseActivity {

    private Lw013ActivityAxisSettingBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityAxisSettingBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());

        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        mBind.etWakeupDuration.postDelayed(() -> {
            ArrayList<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getAccWakeupCondition());
            orderTasks.add(OrderTaskAssembler.getAccMotionCondition());
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
                                    case KEY_ACC_WAKEUP_CONDITION:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_ACC_MOTION_CONDITION:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(AxisSettingActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Save Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read
                                switch (configKeyEnum) {
                                    case KEY_ACC_WAKEUP_CONDITION:
                                        if (length == 2) {
                                            int threshold = value[5] & 0xFF;
                                            int duration = value[6] & 0xFF;
                                            mBind.etWakeupThreshold.setText(String.valueOf(threshold));
                                            mBind.etWakeupDuration.setText(String.valueOf(duration));

                                        }
                                        break;
                                    case KEY_ACC_MOTION_CONDITION:
                                        if (length == 2) {
                                            int threshold = value[5] & 0xFF;
                                            int duration = value[6] & 0xFF;
                                            mBind.etMotionThreshold.setText(String.valueOf(threshold));
                                            mBind.etMotionDuration.setText(String.valueOf(duration));
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
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    private boolean isValid() {
        final String wakeUpThresholdStr = mBind.etWakeupThreshold.getText().toString();
        if (TextUtils.isEmpty(wakeUpThresholdStr))
            return false;
        final int wakeUpThreshold = Integer.parseInt(wakeUpThresholdStr);
        if (wakeUpThreshold < 1 || wakeUpThreshold > 20)
            return false;
        final String wakeUpDurationStr = mBind.etWakeupDuration.getText().toString();
        if (TextUtils.isEmpty(wakeUpDurationStr))
            return false;
        final int wakeUpDuration = Integer.parseInt(wakeUpDurationStr);
        if (wakeUpDuration < 1 || wakeUpDuration > 10)
            return false;
        final String motionThresholdStr = mBind.etMotionThreshold.getText().toString();
        if (TextUtils.isEmpty(motionThresholdStr))
            return false;
        final int motionThreshold = Integer.parseInt(motionThresholdStr);
        if (motionThreshold < 10 || motionThreshold > 250)
            return false;
        final String motionDurationStr = mBind.etMotionDuration.getText().toString();
        if (TextUtils.isEmpty(motionDurationStr))
            return false;
        final int motionDuration = Integer.parseInt(motionDurationStr);
        if (motionDuration < 1 || motionDuration > 50)
            return false;
        return true;

    }

    private void saveParams() {
        final String wakeUpThresholdStr = mBind.etWakeupThreshold.getText().toString();
        final int wakeUpThreshold = Integer.parseInt(wakeUpThresholdStr);
        final String wakeUpDurationStr = mBind.etWakeupDuration.getText().toString();
        final int wakeUpDuration = Integer.parseInt(wakeUpDurationStr);
        final String motionThresholdStr = mBind.etMotionThreshold.getText().toString();
        final int motionThreshold = Integer.parseInt(motionThresholdStr);
        final String motionDurationStr = mBind.etMotionDuration.getText().toString();
        final int motionDuration = Integer.parseInt(motionDurationStr);
        savedParamsError = false;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setAccWakeupCondition(wakeUpThreshold, wakeUpDuration));
        orderTasks.add(OrderTaskAssembler.setAccMotionCondition(motionThreshold, motionDuration));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
