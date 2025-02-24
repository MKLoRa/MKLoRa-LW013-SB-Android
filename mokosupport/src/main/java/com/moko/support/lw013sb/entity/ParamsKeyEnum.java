package com.moko.support.lw013sb.entity;


import java.io.Serializable;

public enum ParamsKeyEnum implements Serializable {

    //// 系统相关参数
    KEY_CLOSE(0x0000),
    KEY_REBOOT(0x0001),
    KEY_RESET(0x0002),
    // 厂家信息
    KEY_MANUFACTURER(0x0010),
    // 固件版本号
    KEY_FIRMWARE_VERSION(0x0011),
    // 硬件版本号
    KEY_HARDWARE_VERSION(0x0012),
    // 产品需求版本
    KEY_DEMAND_VERSION(0x0013),
    // 产品型号
    KEY_PRODUCT_MODEL(0x0014),
    // 芯片MAC
    KEY_CHIP_MAC(0x0015),
    // 系统时间
    KEY_TIME_UTC(0x0020),
    // 时区
    KEY_TIME_ZONE(0x0021),
    // 设备心跳间隔
    KEY_HEARTBEAT_INTERVAL(0x0022),
    // 指示灯开关
    KEY_INDICATOR_STATUS(0x0023),
    // 按键开关关机功能
    KEY_OFF_BY_BUTTON(0x0025),
    // 关机信息上报
    KEY_SHUTDOWN_PAYLOAD_ENABLE(0x0026),
    // 蜂鸣器声效选择
    KEY_BUZZER_SOUND_CHOOSE(0x0027),
    // 三轴唤醒条件
    KEY_ACC_WAKEUP_CONDITION(0x0028),
    // 运动检测判断
    KEY_ACC_MOTION_CONDITION(0x0029),
    // 电池电量
    KEY_BATTERY_POWER(0x0040),
    // 产测状态
    KEY_PCBA_STATUS(0x0041),
    // 自检状态
    KEY_SELFTEST_STATUS(0x0042),
    // 温度
    KEY_TEMP_CURRENT(0x0043),
    // 光照
    KEY_LIGHT_CURRENT(0x0044),

    //// 电池相关参数
    // 电池信息清除
    KEY_BATTERY_RESET(0x0100),
    // 电池电量总消耗
    KEY_BATTERY_INFO_ALL(0x0103),
    // 低电百分比
    KEY_LOW_POWER_PERCENT(0x0104),
    // 低电触发心跳开关
    KEY_LOW_POWER_PAYLOAD_ENABLE(0x0106),
    // 低电上报间隔
    KEY_LOW_POWER_REPORT_INTERVAL(0x0107),
    // 充电自动开机
    KEY_AUTO_POWER_ON_ENABLE(0x0108),


    //// 蓝牙相关参数
    // 登录是否需要密码
    KEY_PASSWORD_VERIFY_ENABLE(0x0200),
    KEY_PASSWORD(0x0201),
    KEY_ADV_TIMEOUT(0x0202),
    KEY_BEACON_MODE(0x0203),
    KEY_ADV_INTERVAL(0x0204),
    KEY_ADV_TX_POWER(0x0205),
    KEY_ADV_NAME(0x0206),

    //// 模式相关参数
    // 工作模式选择
    KEY_DEVICE_MODE(0x0300),
    // 待机模式下定位策略选择
    KEY_STANDBY_MODE_POS_STRATEGY(0x0310),
    // 定期模式定位策略
    KEY_PERIODIC_MODE_POS_STRATEGY(0x0320),
    // 定期模式上报间隔
    KEY_PERIODIC_MODE_REPORT_INTERVAL(0x0321),
    // 定时模式定位策略
    KEY_TIME_MODE_POS_STRATEGY(0x0330),
    // 定时模式时间点
    KEY_TIME_MODE_REPORT_TIME_POINT(0x0331),
    // 运动开始事件信息开关
    KEY_MOTION_MODE_START_ENABLE(0x0340),
    // 运动开始定位开关
    KEY_MOTION_MODE_START_FIX_ENABLE(0x0341),
    // 运动开始定位策略
    KEY_MOTION_MODE_START_POS_STRATEGY(0x0342),
    // 运动开始定位上报次数
    KEY_MOTION_MODE_START_NUMBER(0x0343),
    // 运动中事件信息开关
    KEY_MOTION_MODE_TRIP_ENABLE(0x0350),
    // 运动中定位开关
    KEY_MOTION_MODE_TRIP_FIX_ENABLE(0x0351),
    // 运动中定位策略
    KEY_MOTION_MODE_TRIP_POS_STRATEGY(0x0352),
    // 运动中定位上报间隔
    KEY_MOTION_MODE_TRIP_REPORT_INTERVAL(0x0353),
    // 运动结束事件信息开关
    KEY_MOTION_MODE_END_ENABLE(0x0360),
    // 运动结束定位开关
    KEY_MOTION_MODE_END_FIX_ENABLE(0x0361),
    // 运动结束定位策略
    KEY_MOTION_MODE_END_POS_STRATEGY(0x0362),
    // 运动结束定位上报间隔
    KEY_MOTION_MODE_END_REPORT_INTERVAL(0x0363),
    // 运动结束定位次数
    KEY_MOTION_MODE_END_NUMBER(0x0364),
    // 运动结束判断时间
    KEY_MOTION_MODE_END_TIMEOUT(0x0365),
    // 静止定位开关
    KEY_MOTION_MODE_STATIONARY_FIX_ENABLE(0x0370),
    // 静止定位策略
    KEY_MOTION_MODE_STATIONARY_POS_STRATEGY(0x0371),
    // 静止上报间隔
    KEY_MOTION_MODE_STATIONARY_REPORT_INTERVAL(0x0372),
    // 定时定期模式定位策略
    KEY_TIME_PERIODIC_MODE_POS_STRATEGY(0x0380),
    // 定时定期模式时间段
    KEY_TIME_PERIODIC_MODE_REPORT_TIME_POINT(0x0381),

    //// 蓝牙扫描过滤参数
    // RSSI过滤规则
    KEY_FILTER_RSSI(0x0401),
    // 广播内容过滤逻辑
    KEY_FILTER_RELATIONSHIP(0x0402),
    // 过滤设备类型开关
    KEY_FILTER_RAW_DATA(0x0403),
    // 精准过滤MAC开关
    KEY_FILTER_MAC_PRECISE(0x0410),
    // 反向过滤MAC开关
    KEY_FILTER_MAC_REVERSE(0x0411),
    // MAC过滤规则
    KEY_FILTER_MAC_RULES(0x0412),
    // 精准过滤ADV Name开关
    KEY_FILTER_NAME_PRECISE(0x0418),
    // 反向过滤ADV Name开关
    KEY_FILTER_NAME_REVERSE(0x0419),
    // NAME过滤规则
    KEY_FILTER_NAME_RULES(0x041A),
    // iBeacon类型过滤开关
    KEY_FILTER_IBEACON_ENABLE(0x0420),
    // iBeacon类型Major范围
    KEY_FILTER_IBEACON_MAJOR_RANGE(0x0421),
    // iBeacon类型Minor范围
    KEY_FILTER_IBEACON_MINOR_RANGE(0x0422),
    // iBeacon类型UUID
    KEY_FILTER_IBEACON_UUID(0x0423),
    // eddystone-UID类型过滤开关
    KEY_FILTER_EDDYSTONE_UID_ENABLE(0x0428),
    // eddystone-UID类型Namespace
    KEY_FILTER_EDDYSTONE_UID_NAMESPACE(0x0429),
    // eddystone-UID类型Instance
    KEY_FILTER_EDDYSTONE_UID_INSTANCE(0x042A),
    // eddystone-URL类型过滤开关
    KEY_FILTER_EDDYSTONE_URL_ENABLE(0x0430),
    // eddystone-URL类型URL
    KEY_FILTER_EDDYSTONE_URL(0x0431),
    // eddystone-TLM类型过滤开关
    KEY_FILTER_EDDYSTONE_TLM_ENABLE(0x0438),
    // eddystone- TLM类型TLMVersion
    KEY_FILTER_EDDYSTONE_TLM_VERSION(0x0439),
    // BXP-iBeacon类型过滤开关
    KEY_FILTER_BXP_IBEACON_ENABLE(0x0440),
    // BXP-iBeacon类型Major范围
    KEY_FILTER_BXP_IBEACON_MAJOR_RANGE(0x0441),
    // BXP-iBeacon类型Minor范围
    KEY_FILTER_BXP_IBEACON_MINOR_RANGE(0x0442),
    // BXP-iBeacon类型UUID
    KEY_FILTER_BXP_IBEACON_UUID(0x0443),
    // BeaconX Pro-ACC设备过滤开关
    KEY_FILTER_BXP_ACC(0x0450),
    // BeaconX Pro-T&H设备过滤开关
    KEY_FILTER_BXP_TH(0x0458),
    // BXP-Device类型过滤开关
    KEY_FILTER_BXP_DEVICE(0x0460),
    // BXP-Button类型过滤开关
    KEY_FILTER_BXP_BUTTON_ENABLE(0x0468),
    // BXP-Button类型过滤规则
    KEY_FILTER_BXP_BUTTON_RULES(0x0469),
    // BXP-Tag开关类型过滤开关
    KEY_FILTER_BXP_TAG_ENABLE(0x0470),
    // 精准过滤BXP-Tag开关
    KEY_FILTER_BXP_TAG_PRECISE(0x0471),
    // 反向过滤BXP-Tag开关
    KEY_FILTER_BXP_TAG_REVERSE(0x0472),
    // BXP-Tag过滤规则
    KEY_FILTER_BXP_TAG_RULES(0x0473),
    // BXP-TOF
    KEY_FILTER_MK_TOF_ENABLE(0x0478),
    KEY_FILTER_MK_TOF_MFG_CODE(0x0479),
    //MK-PIR 设备过滤开关
    KEY_FILTER_MK_PIR_ENABLE(0x0480),
    //MK-PIR 设备过滤
    //sensor_detection_status
    KEY_FILTER_MK_PIR_DETECTION_STATUS(0x0481),
    //MK-PIR 设备过滤
    //sensor_sensitivity
    KEY_FILTER_MK_PIR_SENSOR_SENSITIVITY(0x0482),
    //MK-PIR 设备过滤
    //door_status
    KEY_FILTER_MK_PIR_DOOR_STATUS(0x0483),
    //MK-PIR 设备过滤
    //delay_response_status
    KEY_FILTER_MK_PIR_DELAY_RES_STATUS(0x0484),
    //MK-PIR 设备
    //Major 过滤范围
    KEY_FILTER_MK_PIR_MAJOR(0x0485),
    //MK-PIR 设备
    //Minor 过滤范围
    KEY_FILTER_MK_PIR_MINOR(0x0486),
    // Unknown设备过滤开关
    KEY_FILTER_OTHER_ENABLE(0x04F8),
    // 3组unknown过滤规则逻辑
    KEY_FILTER_OTHER_RELATIONSHIP(0x04F9),
    // unknown类型过滤规则
    KEY_FILTER_OTHER_RULES(0x04FA),

    //// LoRaWAN参数
    // LoRaWAN网络状态
    KEY_LORA_NETWORK_STATUS(0x0500),
    // 频段
    KEY_LORA_REGION(0x0501),
    // 入网类型
    KEY_LORA_MODE(0x0502),
    KEY_LORA_DEV_EUI(0x0503),
    KEY_LORA_APP_EUI(0x0504),
    KEY_LORA_APP_KEY(0x0505),
    KEY_LORA_DEV_ADDR(0x0506),
    KEY_LORA_APP_SKEY(0x0507),
    KEY_LORA_NWK_SKEY(0x0508),
    KEY_LORA_ADR_ACK_LIMIT(0x050A),
    KEY_LORA_ADR_ACK_DELAY(0x050B),
    KEY_LORA_DEV_NONCE(0x050C),
    // CH
    KEY_LORA_CH(0x0520),
    // 入网DR
    KEY_LORA_DR(0x0521),
    // 数据发送策略
    KEY_LORA_UPLINK_STRATEGY(0x0522),
    // DUTYCYCLE
    KEY_LORA_DUTYCYCLE(0x0523),
    // 同步间隔
    KEY_LORA_TIME_SYNC_INTERVAL(0x0540),
    // 网络检查间隔
    KEY_LORA_NETWORK_CHECK_INTERVAL(0x0541),

    // 设备信息包上行配置
    KEY_DEVICE_INFO_PAYLOAD(0x0550),
    //心跳数据包上行配置
    KEY_HEARTBEAT_PAYLOAD(0x0551),
    //低电状态数据包上行配置
    KEY_LOW_POWER_PAYLOAD(0x0552),
    //事件信息包上行配置
    KEY_EVENT_PAYLOAD(0x0554),
    //定位数据包上行配置
    KEY_POSITIONING_PAYLOAD(0x0555),
    //电池消耗包上行配置
    KEY_BATTERY_PAYLOAD(0x0556),
    //震动检测包上行配置
    KEY_SHOCK_PAYLOAD(0x0557),
    //GPS极限定位数据包上行配置
    KEY_GPS_LIMIT_PAYLOAD(0x055B),
    //产品配置包上行配置
    KEY_CONFIG_PAYLOAD(0x055D),

    //// 辅助功能参数
    // 下行请求定位策略
    KEY_DOWN_LINK_POS_STRATEGY(0x0600),
    // 震动检测使能
    KEY_SHOCK_DETECTION_ENABLE(0x0610),
    // 震动检测阈值
    KEY_SHOCK_THRESHOLD(0x0611),
    // 震动上发间隔
    KEY_SHOCK_REPORT_INTERVAL(0x0612),
    // 震动次数判断间隔
    KEY_SHOCK_TIMEOUT(0x0613),
    //光照监测开关
    KEY_LIGHT_MONITOR_ENABLE(0x0640),
    //光照数据采样间隔
    KEY_LIGHT_SAMPLE_RATE(0x0641),
    //光照阈值
    KEY_LIGHT_ALARM_THRESHOLD(0x0642),
    //温度监测开关
    KEY_TEMP_MONITOR_ENABLE(0x0650),
    //温度数据采样间隔
    KEY_TEMP_SAMPLE_RATE(0x0651),
    //温度上限阈值
    KEY_TEMP_ALARM_THRESHOLD(0x0652),
    // 报警触发方式
    KEY_ALARM_TYPE(0x0660),
    //退出报警按键时间
    KEY_ALARM_EXIT_TIME(0x0661),
    //Alert 报警触发按键模式
    KEY_ALARM_ALERT_TRIGGER_TYPE(0x0662),
    //Alert 报警定位策略
    KEY_ALARM_ALERT_POS_STRATEGY(0x0663),
    //Alert 报警事件通知
    KEY_ALARM_ALERT_NOTIFY_ENABLE(0x0664),
    //SOS 报警触发按键
    KEY_ALARM_SOS_TRIGGER_TYPE(0x0665),
    //SOS 报警定位策略
    KEY_ALARM_SOS_POS_STRATEGY(0x0666),
    //SOS 定位数据上报间隔
    KEY_ALARM_SOS_REPORT_INTERVAL(0x0667),
    //SOS 报警事件通知
    KEY_ALARM_SOS_NOTIFY_ENABLE(0x0668),
    // 闲置功能使能
    KEY_MAN_DOWN_DETECTION_ENABLE(0x0670),
    // 闲置超时时间
    KEY_MAN_DOWN_DETECTION_TIMEOUT(0x0671),
    // 闲置定位策略
    KEY_MAN_DOWN_DETECTION_POS_STRATEGY(0x0672),
    // 闲置定位上报间隔
    KEY_MAN_DOWN_DETECTION_REPORT_INTERVAL(0x0673),
    //    // 活动记录使能
//    KEY_ACTIVE_STATE_COUNT_ENABLE(0xBA),
//    // 活动判定间隔
//    KEY_ACTIVE_STATE_TIMEOUT(0xBB),


    // POS Params
    // GPS极限上传模式（L76版本）
    KEY_GPS_EXTREME_MODE_L76C(0x0801),
    // 室外蓝牙定位上报间隔
    KEY_OUTDOOR_BLE_REPORT_INTERVAL(0x0808),
    // 室外GPS定位上报间隔
    KEY_OUTDOOR_GPS_REPORT_INTERVAL(0x0809),
    // 蓝牙定位机制
    KEY_BLE_POS_MECHANISM(0x0820),
    // 蓝牙定位超时时间
    KEY_BLE_POS_TIMEOUT(0x0821),
    // 蓝牙定位成功MAC数量
    KEY_BLE_POS_MAC_NUMBER(0x0822),
    // 上报电压开关
    KEY_VOLTAGE_REPORT_ENABLE(0x0823),
    // GPS定位超时时间（L76版本）
    KEY_GPS_POS_TIMEOUT_L76C(0x0830),
    // GPS位置精度因子PDOP（L76版本）
    KEY_GPS_PDOP_LIMIT_L76C(0x0831),



    //// 定位参数
    // WIFI定位数据格式
//    KEY_WIFI_POS_DATA_TYPE(0x50),
//    // WIFI定位超时时间
//    KEY_WIFI_POS_TIMEOUT(0x51),
//    // WIFI定位成功BSSID数量
//    KEY_WIFI_POS_BSSID_NUMBER(0x52),
    // 蓝牙定位超时时间

//    // GPS定位超时时间（LR1110版本）
//    KEY_GPS_POS_TIMEOUT(0x7A),
//    // GPS搜星数量（LR1110版本）
//    KEY_GPS_POS_SATELLITE_THRESHOLD(0x7B),
//    // GPS定位数据格式（LR1110版本）
//    KEY_GPS_POS_DATA_TYPE(0x7C),
//    // GPS定位星座（LR1110版本）
//    KEY_GPS_POS_SYSTEM(0x7D),
//    // 定位方式选择（LR1110版本）
//    KEY_GPS_POS_AUTONMOUS_AIDING_ENABLE(0x7E),
//    // 辅助定位经纬度（LR1110版本）
//    KEY_GPS_POS_AUXILIARY_LAT_LON(0x7F),
//    // 星历开始更新事件开关
//    KEY_GPS_POS_EPHEMERIS_START_NOTIFY_ENABLE(0x80),
//    // 星历更新结束事件开关
//    KEY_GPS_POS_EPHEMERIS_END_NOTIFY_ENABLE(0x81),
    // 蓝牙定位机制选择
//    KEY_BLE_POS_MECHANISM(0x85),

    //// 存储协议
    // 读取存储的数据
    KEY_READ_STORAGE_DATA(0x0900),
    KEY_CLEAR_STORAGE_DATA(0x0901),
    KEY_SYNC_ENABLE(0x0902),
    ;

    private int paramsKey;

    ParamsKeyEnum(int paramsKey) {
        this.paramsKey = paramsKey;
    }


    public int getParamsKey() {
        return paramsKey;
    }

    public static ParamsKeyEnum fromParamKey(int paramsKey) {
        for (ParamsKeyEnum paramsKeyEnum : ParamsKeyEnum.values()) {
            if (paramsKeyEnum.getParamsKey() == paramsKey) {
                return paramsKeyEnum;
            }
        }
        return null;
    }
}
