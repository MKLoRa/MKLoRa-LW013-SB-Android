package com.moko.lw013sb.activity.general;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.moko.ble.lib.MokoConstants;
import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.ble.lib.task.OrderTask;
import com.moko.ble.lib.task.OrderTaskResponse;
import com.moko.ble.lib.utils.MokoUtils;
import com.moko.lw013sb.R;
import com.moko.lw013sb.activity.BaseActivity;
import com.moko.lw013sb.adapter.TimeSegmentedPointAdapter;
import com.moko.lw013sb.databinding.Lw013ActivityTimeSegmentedModeBinding;
import com.moko.lw013sb.dialog.BottomDialog;
import com.moko.lw013sb.entity.TimeSegmentedPoint;
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

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TimeSegmentedModeActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {

    private Lw013ActivityTimeSegmentedModeBinding mBind;
    private boolean mReceiverTag = false;
    private boolean savedParamsError;
    private ArrayList<String> mValues;
    private int mSelected;
    private ArrayList<TimeSegmentedPoint> mTimePoints;
    private TimeSegmentedPointAdapter mAdapter;
    private ArrayList<String> mHourValues;
    private ArrayList<String> mMinValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = Lw013ActivityTimeSegmentedModeBinding.inflate(getLayoutInflater());
        setContentView(mBind.getRoot());
        mValues = new ArrayList<>();
        mValues.add("BLE");
        mValues.add("GPS");
        mValues.add("BLE+GPS");
        mValues.add("BLE*GPS");
        mValues.add("BLE&GPS");
        mHourValues = new ArrayList<>();
        for (int i = 0; i <= 24; i++) {
            mHourValues.add(String.format("%02d", i));
        }
        mMinValues = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            mMinValues.add(String.format("%02d", i));
        }
        mTimePoints = new ArrayList<>();
        mAdapter = new TimeSegmentedPointAdapter(mTimePoints);
        // 开启滑动删除
//        mAdapter.enableSwipeItem();
//        mAdapter.setOnItemSwipeListener(onItemSwipeListener);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.openLoadAnimation();
        mBind.rvTimePoint.setLayoutManager(new LinearLayoutManager(this));
        mBind.rvTimePoint.setAdapter(mAdapter);

        SwipeToDeleteCallback callback = new SwipeToDeleteCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mBind.rvTimePoint);

        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        showSyncingProgressDialog();
        mBind.tvTimingPosStrategy.postDelayed(() -> {
            List<OrderTask> orderTasks = new ArrayList<>();
            orderTasks.add(OrderTaskAssembler.getTimePeriodicPosStrategy());
            orderTasks.add(OrderTaskAssembler.getTimePeriodicPosReportPoints());
            LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
        }, 500);
    }


//    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
//        @Override
//        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
//        }
//
//        @Override
//        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
//        }
//
//        @Override
//        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
//            mTimePoints.remove(pos);
//            int size = mTimePoints.size();
//            for (int i = 1; i <= size; i++) {
//                TimeSegmentedPoint point = mTimePoints.get(i - 1);
//                point.name = String.format("Time Period %d", i);
//            }
//            mAdapter.replaceData(mTimePoints);
//        }
//
//        @Override
//        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {
//        }
//    };

    public class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 不支持拖动
            int dragFlags = 0;
            // 支持从右向左滑动
            int swipeFlags = ItemTouchHelper.START;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mTimePoints.remove(position);
            int size = mTimePoints.size();
            for (int i = 1; i <= size; i++) {
                TimeSegmentedPoint point = mTimePoints.get(i - 1);
                point.name = String.format("Time Period %d", i);
                point.intervalStr = String.format("Report Interval %d", i);
            }
            mAdapter.replaceData(mTimePoints);
        }
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
                            if (flag == 0x01) {
                                // write
                                int result = value[5] & 0xFF;
                                switch (configKeyEnum) {
                                    case KEY_TIME_PERIODIC_MODE_POS_STRATEGY:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        break;
                                    case KEY_TIME_PERIODIC_MODE_REPORT_TIME_POINT:
                                        if (result != 1) {
                                            savedParamsError = true;
                                        }
                                        if (savedParamsError) {
                                            ToastUtils.showToast(TimeSegmentedModeActivity.this, "Opps！Save failed. Please check the input characters and try again.");
                                        } else {
                                            ToastUtils.showToast(this, "Save Successfully！");
                                        }
                                        break;
                                }
                            }
                            if (flag == 0x00) {
                                // read

                                switch (configKeyEnum) {
                                    case KEY_TIME_PERIODIC_MODE_POS_STRATEGY:
                                        if (length > 0) {
                                            int strategy = value[5] & 0xFF;
                                            mSelected = strategy;
                                            mBind.tvTimingPosStrategy.setText(mValues.get(mSelected));
                                        }
                                        break;
                                    case KEY_TIME_PERIODIC_MODE_REPORT_TIME_POINT:
                                        if (length > 0) {
                                            for (int i = 0; i < length; i += 8) {
                                                int start = MokoUtils.toInt(Arrays.copyOfRange(value, 5 + i, 7 + i));
                                                int end = MokoUtils.toInt(Arrays.copyOfRange(value, 7 + i, 9 + i));
                                                int interval = MokoUtils.toInt(Arrays.copyOfRange(value, 9 + i, 13 + i));
                                                TimeSegmentedPoint timePoint = new TimeSegmentedPoint();
                                                timePoint.name = String.format("Time Period %d", i / 8 + 1);
                                                timePoint.intervalStr = String.format("Report Interval %d", i / 8 + 1);
                                                timePoint.start = start;
                                                int startHour = start / 60;
                                                int startMin = start % 60;
                                                timePoint.startHour = mHourValues.get(startHour);
                                                timePoint.startMin = mMinValues.get(startMin);
                                                timePoint.end = end;
                                                int endHour = end / 60;
                                                int endMin = end % 60;
                                                timePoint.endHour = mHourValues.get(endHour);
                                                timePoint.endMin = mMinValues.get(endMin);
                                                timePoint.interval = interval;
                                                mTimePoints.add(timePoint);
                                                mAdapter.replaceData(mTimePoints);
                                            }
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

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (isWindowLocked())
            return;
        TimeSegmentedPoint timePoint = (TimeSegmentedPoint) adapter.getItem(position);
        EditText etInterval = (EditText) adapter.getViewByPosition(mBind.rvTimePoint, position, R.id.et_report_interval);
        String intervalStr = etInterval.getText().toString();
        if (!TextUtils.isEmpty(intervalStr)) {
            int interval = Integer.parseInt(intervalStr);
            timePoint.interval = interval;
        }
        if (view.getId() == R.id.tv_point_start_hour) {
            int select = Integer.parseInt(timePoint.startHour);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mHourValues, select);
            dialog.setListener(value -> {
                timePoint.start = value * 60 + Integer.parseInt(timePoint.startMin);
                timePoint.startHour = mHourValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
        if (view.getId() == R.id.tv_point_start_min) {
            int select = Integer.parseInt(timePoint.startMin);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mMinValues, select);
            dialog.setListener(value -> {
                timePoint.start = Integer.parseInt(timePoint.startHour) * 60 + value;
                timePoint.startMin = mMinValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
        if (view.getId() == R.id.tv_point_end_hour) {
            int select = Integer.parseInt(timePoint.endHour);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mHourValues, select);
            dialog.setListener(value -> {
                timePoint.end = value * 60 + Integer.parseInt(timePoint.endMin);
                timePoint.endHour = mHourValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
        if (view.getId() == R.id.tv_point_end_min) {
            int select = Integer.parseInt(timePoint.endMin);
            BottomDialog dialog = new BottomDialog();
            dialog.setDatas(mMinValues, select);
            dialog.setListener(value -> {
                timePoint.end = Integer.parseInt(timePoint.endHour) * 60 + value;
                timePoint.endMin = mMinValues.get(value);
                adapter.notifyItemChanged(position);
            });
            dialog.show(getSupportFragmentManager());
        }
    }

    public void selectPosStrategy(View view) {
        if (isWindowLocked())
            return;
        BottomDialog dialog = new BottomDialog();
        dialog.setDatas(mValues, mSelected);
        dialog.setListener(value -> {
            mSelected = value;
            mBind.tvTimingPosStrategy.setText(mValues.get(value));
        });
        dialog.show(getSupportFragmentManager());
    }

    public void onTimePointAdd(View view) {
        if (isWindowLocked())
            return;
        int size = mTimePoints.size();
        if (size >= 3) {
            ToastUtils.showToast(this, "You can set up to 3 time points!");
            return;
        }
        TimeSegmentedPoint timePoint = new TimeSegmentedPoint();
        timePoint.name = String.format("Time Period %d", size + 1);
        timePoint.intervalStr = String.format("Report Interval %d", size + 1);
        timePoint.start = 0;
        timePoint.startHour = "0";
        timePoint.startMin = "0";
        timePoint.end = 0;
        timePoint.endHour = "0";
        timePoint.endMin = "0";
        timePoint.interval = 600;
        mTimePoints.add(timePoint);
        mAdapter.replaceData(mTimePoints);
    }

    public void onSave(View view) {
        int failedCode = isValid();
        if (failedCode == 1) {
            ToastUtils.showToast(this, "Para error!");
            return;
        }
        if (failedCode == 2) {
            ToastUtils.showToast(this, "The start time must be earlier than the end time!");
            return;
        }
        if (failedCode == 3) {
            ToastUtils.showToast(this, "Time ranges cannot overlap!");
            return;
        }
        savedParamsError = false;
        showSyncingProgressDialog();
        List<OrderTask> orderTasks = new ArrayList<>();
        orderTasks.add(OrderTaskAssembler.setTimePeriodicPosStrategy(mSelected));
        ArrayList<Integer> points = new ArrayList<>();
        for (TimeSegmentedPoint point : mTimePoints) {
            points.add(point.start);
            points.add(point.end);
            points.add(point.interval);
        }
        orderTasks.add(OrderTaskAssembler.setTimePeriodicPosReportPoints(points));
        LoRaLW013SBMokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    }

    private int isValid() {
        int count = mAdapter.getItemCount();
        for (int i = 0; i < count; i++) {
            EditText etInterval = (EditText) mAdapter.getViewByPosition(mBind.rvTimePoint, i, R.id.et_report_interval);
            String intervalStr = etInterval.getText().toString();
            if (TextUtils.isEmpty(intervalStr))
                return 1;
            int interval = Integer.parseInt(intervalStr);
            if (interval < 30 || interval > 86400) return 1;
            mTimePoints.get(i).interval = Integer.parseInt(intervalStr);
        }
        for (int i = 0; i < count; i++) {
            int start = mTimePoints.get(i).start;
            int end = mTimePoints.get(i).end;
            if (start > 1440 || end > 1440)
                return 1;
        }
        if (count > 0) {
            if (mTimePoints.get(0).start >= mTimePoints.get(0).end)
                return 2;
        }
        if (count > 1) {
            if (mTimePoints.get(1).start >= mTimePoints.get(1).end)
                return 2;
            if (mTimePoints.get(0).start < mTimePoints.get(1).end && mTimePoints.get(1).start < mTimePoints.get(0).end)
                return 3;
        }
        if (count > 2) {
            if (mTimePoints.get(2).start >= mTimePoints.get(2).end)
                return 2;
            if (mTimePoints.get(0).start < mTimePoints.get(2).end && mTimePoints.get(2).start < mTimePoints.get(0).end)
                return 3;
            if (mTimePoints.get(1).start < mTimePoints.get(2).end && mTimePoints.get(2).start < mTimePoints.get(1).end)
                return 3;
        }
        return 0;
    }
}
