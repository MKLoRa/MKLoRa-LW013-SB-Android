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
import com.moko.lw013sb.databinding.Lw013TempMonitorSettingsBinding;
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
import java.util.Locale;

public class TempMonitorActivity extends BaseActivity {
    private Lw013TempMonitorSettingsBinding mBind;
    private boolean mReceiverTag;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013TempMonitorSettingsBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>(4);
        orderTasks.add(OrderTaskAssembler.getTempMonitorEnable());
        orderTasks.add(OrderTaskAssembler.getTempSampleRate());
        orderTasks.add(OrderTaskAssembler.getTempCurrent());
        orderTasks.add(OrderTaskAssembler.getTempAlarmThreshold());
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 300)
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
                byte[] value = response.responseValue;
                if (orderCHAR == OrderCHAR.CHAR_PARAMS) {
                    if (null != value && value.length >= 5) {
                        int header = value[0] & 0xFF;// 0xED
                        int flag = value[1] & 0xFF;// read or write
                        int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
                        ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
                        if (header != 0xED || null == configKeyEnum) return;
                        int length = value[4] & 0xFF;
                        if (flag == 0x01) {
                            // write
                            switch (configKeyEnum) {
                                case KEY_TEMP_MONITOR_ENABLE:
                                case KEY_TEMP_SAMPLE_RATE:
                                    if (value[5] != 1)
                                        savedParamsError = true;
                                    break;
                                case KEY_TEMP_ALARM_THRESHOLD:
                                    if (value[5] != 1) {
                                        savedParamsError = true;
                                    }
                                    if (savedParamsError)
                                        ToastUtils.showToast(this, "Opps！Save failed. Please check the input characters and try again.");
                                    else
                                        ToastUtils.showToast(this, "Save Successfully！");
                                    break;
                            }
                        }
                        if (flag == 0x00) {
                            // read
                            switch (configKeyEnum) {
                                case KEY_TEMP_MONITOR_ENABLE:
                                    if (length == 1) {
                                        mBind.cbTempEnable.setChecked((value[5] & 0x01) == 1);
                                        mBind.cbTempAlarmEnable.setChecked((value[5] >> 1 & 0x01) == 1);
                                    }
                                    break;
                                case KEY_TEMP_SAMPLE_RATE:
                                    if (length == 2) {
                                        mBind.etSampleRate.setText(String.valueOf(MokoUtils.toInt(Arrays.copyOfRange(value, 5, 5 + length))));
                                    }
                                    break;
                                case KEY_TEMP_CURRENT:
                                    if (length == 1) {
                                        int temp = value[5];
                                        if (mBind.cbTempEnable.isChecked())
                                            mBind.tvTemp.setText(String.format(Locale.getDefault(), "%d℃", temp));
                                    }
                                    break;
                                case KEY_TEMP_ALARM_THRESHOLD:
                                    if (length == 2) {
                                        mBind.etAlarmMin.setText(String.valueOf(value[5]));
                                        mBind.etAlarmMax.setText(String.valueOf(value[6]));
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    public void onSave(View view) {
        if (isWindowLocked()) return;
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    private void saveParams() {
        savedParamsError = false;
        int rate = Integer.parseInt(mBind.etSampleRate.getText().toString());
        int max = Integer.parseInt(mBind.etAlarmMax.getText().toString());
        int min = Integer.parseInt(mBind.etAlarmMin.getText().toString());
        List<OrderTask> orderTasks = new ArrayList<>(3);
        orderTasks.add(OrderTaskAssembler.setTempMonitorEnable(mBind.cbTempEnable.isChecked() ? 1 : 0, mBind.cbTempAlarmEnable.isChecked() ? 1 : 0));
        orderTasks.add(OrderTaskAssembler.setTempSampleRate(rate));
        orderTasks.add(OrderTaskAssembler.setTempAlarmThreshold(min, max));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(mBind.etSampleRate.getText())
                || TextUtils.isEmpty(mBind.etAlarmMax.getText())
                || TextUtils.isEmpty(mBind.etAlarmMin.getText()))
            return false;
        String rateStr = mBind.etSampleRate.getText().toString();
        int rate = Integer.parseInt(rateStr);
        String maxStr = mBind.etAlarmMax.getText().toString();
        int max = Integer.parseInt(maxStr);
        String minStr = mBind.etAlarmMin.getText().toString();
        int min = Integer.parseInt(minStr);
        return (rate >= 1 && rate <= 3600) && (max >= -20 && max <= 60) && (min >= -20 && min <= 60);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    if (blueState == BluetoothAdapter.STATE_TURNING_OFF) {
                        dismissSyncProgressDialog();
                        finish();
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
        finish();
    }
}
