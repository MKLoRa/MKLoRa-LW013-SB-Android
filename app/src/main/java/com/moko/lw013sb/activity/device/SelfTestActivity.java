package com.moko.lw013sb.activity.device;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.databinding.Lw013ActivitySelftestBinding;
import com.moko.lib.loraui.dialog.BottomDialog;
import com.moko.lib.loraui.utils.ToastUtils;
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

public class SelfTestActivity extends BaseActivity {

    private Lw013ActivitySelftestBinding mBind;

    private ArrayList<String> mValues;
    private boolean savedParamsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivitySelftestBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());

        mValues = new ArrayList<>();
        for (int i = 44; i <= 64; i++) {
            mValues.add(MokoUtils.getDecimalFormat("0.##").format(i * 0.05f));
        }
        mBind.tvNonAlarmVoltageThreshold.setTag(0);
        mBind.tvNonAlarmVoltageThreshold.setOnClickListener(v -> {
            showThresholdDialog(v);
        });
        mBind.tvAlarmVoltageThreshold.setTag(0);
        mBind.tvAlarmVoltageThreshold.setOnClickListener(v -> {
            showThresholdDialog(v);
        });


        EventBus.getDefault().register(this);
        showSyncingProgressDialog();
        mBind.tvSelftestStatus.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getSelfTestStatus());
            orderTasks.add(OrderTaskAssembler.getPCBAStatus());
            orderTasks.add(OrderTaskAssembler.getNonAlarmVoltageThreshold());
            orderTasks.add(OrderTaskAssembler.getNonAlarmMinSampleInterval());
            orderTasks.add(OrderTaskAssembler.getNonAlarmSampleTimes());
            orderTasks.add(OrderTaskAssembler.getAlarmVoltageThreshold());
            orderTasks.add(OrderTaskAssembler.getAlarmMinSampleInterval());
            orderTasks.add(OrderTaskAssembler.getAlarmSampleTimes());
            LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    private void showThresholdDialog(View v) {
        if (isWindowLocked()) return;
        int selected = (int) v.getTag();
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, selected);
        dialog.setListener(value -> {
            ((TextView) v).setText(mValues.get(value));
            v.setTag(value);
        });
        dialog.show(getSupportFragmentManager());
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
                            if (length == 0) return;
                            if (flag == 0x01) {
                                // write
                                int result = value[5] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_NON_ALARM_VOLTAGE_THRESHOLD:
                                    case KEY_NON_ALARM_MIN_SAMPLE_INTERVAL:
                                    case KEY_NON_ALARM_SAMPLE_TIMES:
                                    case KEY_ALARM_VOLTAGE_THRESHOLD:
                                    case KEY_ALARM_MIN_SAMPLE_INTERVAL:
                                        if (result != 1)
                                            savedParamsError = true;
                                        break;
                                    case KEY_ALARM_SAMPLE_TIMES:
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
                                    case KEY_SELFTEST_STATUS:
                                        int status = value[5] & 0xFF;
                                        mBind.tvSelftestStatus.setVisibility(status == 0 ? View.VISIBLE : View.GONE);
                                        break;
                                    case KEY_PCBA_STATUS:
                                        mBind.tvPcbaStatus.setText(String.valueOf(value[5] & 0xFF));
                                        break;
                                    case KEY_NON_ALARM_VOLTAGE_THRESHOLD:
                                        int thresholdNon = value[5] & 0xFF;
                                        int selectedNon = thresholdNon - 44;
                                        mBind.tvNonAlarmVoltageThreshold.setText(mValues.get(selectedNon));
                                        mBind.tvNonAlarmVoltageThreshold.setTag(selectedNon);
                                        break;
                                    case KEY_NON_ALARM_MIN_SAMPLE_INTERVAL:
                                        int intervalNon = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 5 + length));
                                        mBind.etNonAlarmMinSampleInterval.setText(String.valueOf(intervalNon));
                                        break;
                                    case KEY_NON_ALARM_SAMPLE_TIMES:
                                        int timesNon = value[5] & 0xFF;
                                        mBind.etNonAlarmSampleTimes.setText(String.valueOf(timesNon));
                                        break;
                                    case KEY_ALARM_VOLTAGE_THRESHOLD:
                                        int threshold = value[5] & 0xFF;
                                        int selected = threshold - 44;
                                        mBind.tvAlarmVoltageThreshold.setText(mValues.get(selected));
                                        mBind.tvAlarmVoltageThreshold.setTag(selected);
                                        break;
                                    case KEY_ALARM_MIN_SAMPLE_INTERVAL:
                                        int interval = MokoUtils.toInt(Arrays.copyOfRange(value, 5, 5 + length));
                                        mBind.etAlarmMinSampleInterval.setText(String.valueOf(interval));
                                        break;
                                    case KEY_ALARM_SAMPLE_TIMES:
                                        int times = value[5] & 0xFF;
                                        mBind.etAlarmSampleTimes.setText(String.valueOf(times));
                                        break;
                                }
                            }
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        finish();
    }

    public void onSave(View view) {
        if (isValid()) {
            showSyncingProgressDialog();
            saveParams();
        } else {
            ToastUtils.showToast(this, "Para error!");
        }
    }

    private boolean isValid() {
        if (TextUtils.isEmpty(mBind.etNonAlarmMinSampleInterval.getText())) return false;
        String intervalNonStr = mBind.etNonAlarmMinSampleInterval.getText().toString();
        int intervalNon = Integer.parseInt(intervalNonStr);
        if (intervalNon < 1 || intervalNon > 1440)
            return false;
        if (TextUtils.isEmpty(mBind.etNonAlarmSampleTimes.getText())) return false;
        String timesNonStr = mBind.etNonAlarmSampleTimes.getText().toString();
        int timesNon = Integer.parseInt(timesNonStr);
        if (timesNon < 1 || timesNon > 100)
            return false;
        if (TextUtils.isEmpty(mBind.etAlarmMinSampleInterval.getText())) return false;
        String intervalStr = mBind.etAlarmMinSampleInterval.getText().toString();
        int interval = Integer.parseInt(intervalStr);
        if (interval < 1 || interval > 1440)
            return false;
        if (TextUtils.isEmpty(mBind.etAlarmSampleTimes.getText())) return false;
        String timesStr = mBind.etAlarmSampleTimes.getText().toString();
        int times = Integer.parseInt(timesStr);
        if (times < 1 || times > 100)
            return false;
        return true;
    }

    private void saveParams() {
        savedParamsError = false;
        int thresholdNon = (int) mBind.tvNonAlarmVoltageThreshold.getTag() + 44;
        int intervalNon = Integer.parseInt(mBind.etNonAlarmMinSampleInterval.getText().toString());
        int timesNon = Integer.parseInt(mBind.etNonAlarmSampleTimes.getText().toString());
        int threshold = (int) mBind.tvAlarmVoltageThreshold.getTag() + 44;
        int interval = Integer.parseInt(mBind.etAlarmMinSampleInterval.getText().toString());
        int times = Integer.parseInt(mBind.etAlarmSampleTimes.getText().toString());

        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setNonAlarmVoltageThreshold(thresholdNon));
        orderTasks.add(OrderTaskAssembler.setNonAlarmMinSampleInterval(intervalNon));
        orderTasks.add(OrderTaskAssembler.setNonAlarmSampleTimes(timesNon));
        orderTasks.add(OrderTaskAssembler.setAlarmVoltageThreshold(threshold));
        orderTasks.add(OrderTaskAssembler.setAlarmMinSampleInterval(interval));
        orderTasks.add(OrderTaskAssembler.setAlarmSampleTimes(times));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }
}
