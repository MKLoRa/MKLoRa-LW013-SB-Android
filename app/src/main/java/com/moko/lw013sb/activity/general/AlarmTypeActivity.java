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
import com.moko.lw013sb.AppConstants;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.databinding.Lw013ActivityAlarmTypeSettingBinding;
import com.moko.lw013sb.dialog.BottomDialog;
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

public class AlarmTypeActivity extends BaseActivity {
    private Lw013ActivityAlarmTypeSettingBinding mBind;
    private boolean mReceiverTag;
    private final ArrayList<String> mValues = new ArrayList<>(4);
    private int mSelected;

    private boolean savedParamsError;

    private int mAlarmType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityAlarmTypeSettingBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        mValues.add("No");
        mValues.add("Normal");
        mValues.add("Alarm");
        mAlarmType = getIntent().getIntExtra(AppConstants.EXTRA_KEY_ALARM_TYPE, 0);
        if (mAlarmType == 1)
            mBind.tvTitle.setText("Alarm Type2 Settings");
        if (mAlarmType == 2)
            mBind.tvTitle.setText("Alarm Type3 Settings");
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>(4);
        orderTasks.add(OrderTaskAssembler.getAlarmEnable(mAlarmType));
        orderTasks.add(OrderTaskAssembler.getBuzzerEnable(mAlarmType));
        orderTasks.add(OrderTaskAssembler.getAlarmReportInterval(mAlarmType));
        orderTasks.add(OrderTaskAssembler.getExitAlarmDuration(mAlarmType));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));

        mBind.tvBuzzerEnable.setOnClickListener(v -> {
            if (isWindowLocked()) return;
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mValues, mSelected);
            dialog.setListener(value -> {
                mSelected = value;
                mBind.tvBuzzerEnable.setText(mValues.get(value));
            });
            dialog.show(getSupportFragmentManager());
        });
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
                            int result = value[5] & 0xFF;
                            switch (configKeyEnum) {
                                case KEY_ALARM_REPORT_INTERVAL_1:
                                case KEY_ALARM_REPORT_INTERVAL_2:
                                case KEY_ALARM_REPORT_INTERVAL_3:
                                case KEY_BUZZER_ENABLE_1:
                                case KEY_BUZZER_ENABLE_2:
                                case KEY_BUZZER_ENABLE_3:
                                case KEY_ALARM_ENABLE_1:
                                case KEY_ALARM_ENABLE_2:
                                case KEY_ALARM_ENABLE_3:
                                    if (result != 1)
                                        savedParamsError = true;
                                    break;
                                case KEY_EXIT_ALARM_DURATION_1:
                                case KEY_EXIT_ALARM_DURATION_2:
                                case KEY_EXIT_ALARM_DURATION_3:
                                    if (result != 1) {
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
                                case KEY_BUZZER_ENABLE_1:
                                case KEY_BUZZER_ENABLE_2:
                                case KEY_BUZZER_ENABLE_3:
                                    if (length == 1) {
                                        mSelected = value[5] & 0xff;
                                        mBind.tvBuzzerEnable.setText(mValues.get(mSelected));
                                    }
                                    break;
                                case KEY_ALARM_REPORT_INTERVAL_1:
                                case KEY_ALARM_REPORT_INTERVAL_2:
                                case KEY_ALARM_REPORT_INTERVAL_3:
                                    if (length == 2) {
                                        int interval = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 7));
                                        mBind.etReportInterval.setText(String.valueOf(interval));
                                    }
                                    break;
                                case KEY_ALARM_ENABLE_1:
                                case KEY_ALARM_ENABLE_2:
                                case KEY_ALARM_ENABLE_3:
                                    if (length == 2) {
                                        int enable = value[5];
                                        int exitEnable = value[6];
                                        mBind.cbEnable.setChecked(enable == 1);
                                        mBind.cbExitAlarmEnable.setChecked(exitEnable == 1);
                                    }
                                    break;
                                case KEY_EXIT_ALARM_DURATION_1:
                                case KEY_EXIT_ALARM_DURATION_2:
                                case KEY_EXIT_ALARM_DURATION_3:
                                    if (length == 1) {
                                        int duration = value[5] & 0xff;
                                        mBind.etAlarmType.setText(String.valueOf(duration));
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
        int interval = Integer.parseInt(mBind.etReportInterval.getText().toString());
        int duration = Integer.parseInt(mBind.etAlarmType.getText().toString());
        int exitEnable = mBind.cbExitAlarmEnable.isChecked() ? 1 : 0;
        int enable = mBind.cbEnable.isChecked() ? 1 : 0;
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setBuzzerEnable(mSelected, mAlarmType));
        orderTasks.add(OrderTaskAssembler.setAlarmReportInterval(interval, mAlarmType));
        orderTasks.add(OrderTaskAssembler.setAlarmEnable(enable, exitEnable, mAlarmType));
        orderTasks.add(OrderTaskAssembler.setExitAlarmDuration(duration, mAlarmType));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(mBind.etReportInterval.getText())) return false;
        String intervalStr = mBind.etReportInterval.getText().toString();
        int interval = Integer.parseInt(intervalStr);
        if (interval < 1 || interval > 1440)
            return false;
        if (TextUtils.isEmpty(mBind.etAlarmType.getText())) return false;
        String durationStr = mBind.etAlarmType.getText().toString();
        int duration = Integer.parseInt(durationStr);
        return duration >= 10 && duration <= 15;
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
