package com.moko.lw013sb.activity.filter;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.lw013sb.R;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.databinding.Lw013ActivityFilterBxpSensorInfoBinding;
import com.moko.lw013sb.utils.ToastUtils;
import com.moko.support.lw013sb.entity.OrderCHAR;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class FilterBXPSensorInfoActivity extends BaseActivity {

    private Lw013ActivityFilterBxpSensorInfoBinding mBind;

    private boolean savedParamsError;

    private ArrayList<String> filterTagId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityFilterBxpSensorInfoBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        EventBus.getDefault().register(this);
        filterTagId = new ArrayList<>();
        showSyncingProgressDialog();
        mBind.cbPreciseMatch.postDelayed(() -> {
//            List<OrderTask> orderTasks = new ArrayList<>();
//            orderTasks.add(OrderTaskAssembler.getFilterBXPSensorInfoEnable());
//            orderTasks.add(OrderTaskAssembler.getFilterBXPSensorInfoPrecise());
//            orderTasks.add(OrderTaskAssembler.getFilterBXPSensorInfoReverse());
//            orderTasks.add(OrderTaskAssembler.getFilterBXPSensorInfoRules());
//            LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 400)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        final String action = event.getAction();
        runOnUiThread(() -> {
            if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 400)
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
//                switch (orderCHAR) {
//                    case CHAR_PARAMS:
//                        if (value.length >= 5) {
//                            int header = value[0] & 0xFF;// 0xED
//                            int flag = value[1] & 0xFF;// read or write
//                                int cmd = MokoUtils.toInt(Arrays.copyOfRange(value, 2, 4));
//                            if (header != 0xED)
//                                return;
//                            ParamsKeyEnum configKeyEnum = ParamsKeyEnum.fromParamKey(cmd);
//                            if (configKeyEnum == null) {
//                                return;
//                            }
//                            int length = value[4] & 0xFF;
//                            if (flag == 0x01) {
//                                // write
//                                int result = value[5] & 0xFF;
//                                switch (configKeyEnum) {
//                                    case KEY_FILTER_BXP_SENSOR_INFO_ENABLE:
//                                    case KEY_FILTER_BXP_SENSOR_INFO_PRECISE:
//                                    case KEY_FILTER_BXP_SENSOR_INFO_REVERSE:
//                                        if (result != 1) {
//                                            savedParamsError = true;
//                                        }
//                                        break;
//                                    case KEY_FILTER_BXP_SENSOR_INFO_RULES:
//                                        if (result != 1) {
//                                            savedParamsError = true;
//                                        }
//                                        if (savedParamsError) {
//                                            ToastUtils.showToast(FilterBXPSensorInfoActivity.this, "Opps！Save failed. Please check the input characters and try again.");
//                                        } else {
//                                            ToastUtils.showToast(this, "Save Successfully！");
//                                        }
//                                        break;
//                                }
//                            }
//                            if (flag == 0x00) {
//                                // read
//                                switch (configKeyEnum) {
//                                    case KEY_FILTER_BXP_SENSOR_INFO_ENABLE:
//                                        if (length > 0) {
//                                            int enable = value[4] & 0xFF;
//                                            mBind.cbEnable.setChecked(enable == 1);
//                                        }
//                                        break;
//                                    case KEY_FILTER_BXP_SENSOR_INFO_PRECISE:
//                                        if (length > 0) {
//                                            int enable = value[4] & 0xFF;
//                                            mBind.cbPreciseMatch.setChecked(enable == 1);
//                                        }
//                                        break;
//                                    case KEY_FILTER_BXP_SENSOR_INFO_REVERSE:
//                                        if (length > 0) {
//                                            int enable = value[4] & 0xFF;
//                                            mBind.cbReverseFilter.setChecked(enable == 1);
//                                        }
//                                        break;
//                                    case KEY_FILTER_BXP_SENSOR_INFO_RULES:
//                                        if (length > 0) {
//                                            filterTagId.clear();
//                                            byte[] tagIdBytes = Arrays.copyOfRange(value, 5, 5 + length);
//                                            for (int i = 0, l = tagIdBytes.length; i < l; ) {
//                                                int idLength = tagIdBytes[i] & 0xFF;
//                                                i++;
//                                                filterTagId.add(MokoUtils.bytesToHexString(Arrays.copyOfRange(tagIdBytes, i, i + idLength)));
//                                                i += idLength;
//                                            }
//                                            for (int i = 0, l = filterTagId.size(); i < l; i++) {
//                                                String macAddress = filterTagId.get(i);
//                                                View v = LayoutInflater.from(FilterBXPSensorInfoActivity.this).inflate(R.layout.lw013_item_tag_id_filter, mBind.llTagId, false);
//                                                TextView title = v.findViewById(R.id.tv_tag_id_title);
//                                                EditText etMacAddress = v.findViewById(R.id.et_tag_id);
//                                                title.setText(String.format("Tag ID %d", i + 1));
//                                                etMacAddress.setText(macAddress);
//                                                mBind.llTagId.addView(v);
//                                            }
//                                        }
//                                        break;
//                                }
//                            }
//                        }
//                        break;
//                }
            }
        });
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

    public void onAdd(View view) {
        if (isWindowLocked())
            return;
        int count = mBind.llTagId.getChildCount();
        if (count > 9) {
            ToastUtils.showToast(this, "You can set up to 10 filters!");
            return;
        }
        View v = LayoutInflater.from(this).inflate(R.layout.lw013_item_tag_id_filter, mBind.llTagId, false);
        TextView title = v.findViewById(R.id.tv_tag_id_title);
        title.setText(String.format("Tag ID %d", count + 1));
        mBind.llTagId.addView(v);
    }

    public void onDel(View view) {
        if (isWindowLocked())
            return;
        final int c = mBind.llTagId.getChildCount();
        if (c == 0) {
            ToastUtils.showToast(this, "There are currently no filters to delete");
            return;
        }
        int count = mBind.llTagId.getChildCount();
        if (count > 0) {
            mBind.llTagId.removeViewAt(count - 1);
        }
    }


    private void saveParams() {
        savedParamsError = false;
//        List<OrderTask> orderTasks = new ArrayList<>();
//        orderTasks.add(OrderTaskAssembler.setFilterBXPSensorInfoEnable(mBind.cbEnable.isChecked() ? 1 : 0));
//        orderTasks.add(OrderTaskAssembler.setFilterBXPSensorInfoPrecise(mBind.cbPreciseMatch.isChecked() ? 1 : 0));
//        orderTasks.add(OrderTaskAssembler.setFilterBXPSensorInfoReverse(mBind.cbReverseFilter.isChecked() ? 1 : 0));
//        orderTasks.add(OrderTaskAssembler.setFilterBXPSensorInfoRules(filterTagId));
//        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private boolean isValid() {
        final int c = mBind.llTagId.getChildCount();
        filterTagId.clear();
        if (c > 0) {
            for (int i = 0; i < c; i++) {
                View v = mBind.llTagId.getChildAt(i);
                EditText etTagId = v.findViewById(R.id.et_tag_id);
                final String macAddress = etTagId.getText().toString();
                if (TextUtils.isEmpty(macAddress)) {
                    return false;
                }
                int length = macAddress.length();
                if (length % 2 != 0 || length > 12) {
                    return false;
                }
                filterTagId.add(macAddress);
            }
        }
        return true;
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
        setResult(RESULT_OK);
        finish();
    }
}
