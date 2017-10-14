package com.tools.security.common;

/**
 * description:常量
 * author: xiaodifu
 * date: 2016/12/12.
 */

public class AppConstants {

    //是否为测试服务器
    public static final boolean IS_TEST_SERVER = true;
    //测试服务器地址
    public static final String TEST_SERVER = "http://test.security.batmobi.net/common?funid=";
    //正式服务器地址
    public static final String FORMAL_SERVER = "http://security.lmobi.net/common?funid=";
    //PKEY
    public static final String PKEY = "batmobi_2016_security";
    //appKey
    public static final String SECURITY_APPKEY = "";
    //channel
    public static final String CHANNEL = "";
    //Trust Look client key
    public static final String TRUST_LOOK_CLIENT_KEY = "d46656d66466e72e49173ab737c8d00d9a19288b15ca4025b67bbca2";
    //GooglePlay  url
    public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=com.zz.ultra.security";//https://play.google.com/store/apps/details?id=" + SecurityApplication.getInstance().getPackageName();


    //广告SDK信息
    public static final String BATMOBI_APPKEY = "L3HILDEC22D99HA4J7FG92SA";
    public static final String FACEBOOK_APPID = "1191288307627495";
    public static final String BATMOBI_TOKEN = "4abab1f82922892e713bd0614594eee4";
    //通知栏广告
    public static final String BATMOBI_NOTIFICATION_PLACEMENT_ID = "11103_93912";
    //普通锁屏广告
    public static final String BATMOBI_SCREEN_LOCK_PLACEMENT_ID = "11103_41340";
    //充电锁屏广告
    public static final String BATMOBI_CHARGE_LOCK_PLACEMENT_ID = "11103_68542";
    //wifi扫描结果页广告
    public static final String BATMOBI_WIFI_RESULT_PLACEMENT_ID = "11103_14787";
    //试试手气广告
    public static final String BATMOBI_TRY_PLACEMENT_ID = "11103_64900";
    //病毒扫描结果页
    public static final String BATMOBI_VIRUS_RESULT_PLACEMENT_ID = "11103_73295";
    //释放带宽结果页
    public static final String BATMOBI_WIFI_RELEASING_PLACEMENT_ID = "11103_02590";
    //文件扫描结果页
    public static final String BATMOBI_SCAN_FILES_PLACEMENT_ID = "11103_75852";
    //应用锁
    public static final String BATMOBI_APP_LOCK_PLACEMENT_ID = "11103_48787";
    /*-------------------------测试-----------------------*/
    //病毒扫描结果页A
    public static final String BATMOBI_VIRUS_FB_PLACEMENT_ID = "1057741331002639_1101227293320709";
    //试试手气
    public static final String BATMOBI_TRY_FB_PLACEMENT_ID = "1057741331002639_1101230666653705";
    //文件扫描结果页A
    public static final String BATMOBI_FILE_FB_PLACEMENT_ID = "1057741331002639_1101231233320315";
    /*-------------------------------------------------*---*/

    //应用配置
    public static final String APP_CONFIG = "_app_config";
    //隐私协议
    public static final String PROVACY_POLICY_URL = "http://cdn.batmobi.net/appstore/richmedia/20161221/ut4pq4zn15uqncc647yg2cye_policy.htm";
    //eula
    public static final String EULA_URL = "http://cdn.batmobi.net/appstore/richmedia/20161221/d2grrsgwvc4wj8prj4l4roox_Eula.htm";
    //Ultra邮箱
    public static final String ULTRA_EMAIL = "UltraMobileTeam@gmail.com";
    //浏览器历史记录数目
    public static final String BROSWER_HISTORY_COUNT = "_broswer_history_count";//BROSWER_HISTORY_COUNT
    //云查杀数量
    public static final String CLOUD_SCAN_COUNT = "_cloud_scan_count";
    //垃圾文件
    public static final String JUNK_FILE_LIST = "_junk_file_list";
    //广播:添加忽略文件
    public static final String ACTION_FILTER_ADD_IGNORE = "_action_filter_add_ignore";
    //广播:清除垃圾文件
    public static final String ACTION_FILTER_CLEAN_JUNK = "_action_filter_clean_junk";
    //广播：清除历史记录
    public static final String ACTION_FILTER_CLEAR_BROSWER_HISTORY = "_action_filter_clear_broswer_history";
    //广播：病毒软件卸载
    public static final String ACTION_FILTER_REMOVED_VIRUS_APP = "_action_filter_removed_virus_app";
    //广播：更改主色
    public static final String ACTION_FILTER_CHANGE_COLOR = "_action_filter_change_color";
    //广播:首页侧滑栏关闭
    public static final String ACTION_FILTER_MAIN_DRAWER_CLOSED="_action_filter_main_drawer_closed";
    //实时监听到的appinfo
    public static final String VIRUS_MONITOR_APP_INFO = "_virus_monitor_app_info";
    //垃圾：log文件
    public static final String FILE_CACHE_GROUP_LOG = "_file_cache_group_log";
    //垃圾：apk文件
    public static final String FILE_CACHE_GROUP_APK = "_file_cache_group_apk";
    //垃圾：system temp文件
    public static final String FILE_CACHE_GROUP_SYS_TEMP = "_file_cache_sys_temp";
    //更新下载保护列表
    public static final String UPDATE_DOWNLOAD_SECURITY_LIST = "_update_download_security_list";

    //最后垃圾文件清理的时间
    public static final String TIME_LAST_CLEAN_JUNK = "_time_last_clean_junk";
    //上次清理广告垃圾文件的时间
    public static final String TIME_LAST_CLEAN_AD = "_time_last_clean_ad";
    //广告垃圾大小
    public static final String AD_JUNK_SIZE = "_ad_junk_size";
    //卸载所有应用
    public static final String IS_RESOLVE_ALL = "_is_resolve_all";

    public static final String KOCHAVA_APP_GUID = "koultra-security-prb";
    public static final String KOCHAVA_APP_GUID_TEST = "koultra-security-test-ru51evok";
    //渠道
    public static final String REFERRER_PREF = "referrer";
    public static final String MANIFEST_CHANNEL = "channel";

    //结果页操作的item 位置
    public static final String SCAN_RESULT_UPDATE_POSITION = "_scan_result_update_position";

    //Kochava统计
    public static final String CLICK_HOME_SCAN = "click_home_scan"; //点击SCAN
    public static final String CLICK_MENU_SD_CARD = "click_menu_scan_sd_card"; //点击扫描sd卡
    public static final String CLICK_MENU_IGNORE = "click_menu_ignore_list"; //点击白名单
    public static final String CLICK_MENU_ABOUT = "click_menu_about"; //点击About
    public static final String CLICK_MENU_FEEDBACK = "click_menu_feedback"; //点击feedback
    public static final String CLICK_MENU_RATE = "click_menu_rate"; //点击Rate
    public static final String CLICK_HOME_STATE_AREA = "click_home_state_area"; //点击首页状态的文字
    public static final String CLICK_SCAN_RESULT_DEFINITION = "click_scan_result_definition"; //结果页Safe状态下点击item
    public static final String CLICK_SCAN_RESULT_DONE = "click_scan_result_done"; //结果页点击最下面的按钮
    public static final String CLICK_SCAN_RESULT_JUNK = "click_scan_result_junk"; //结果页Suspicious状态下点击Junk
    public static final String CLICK_SCAN_RESULT_JUNK_CLEAN = "click_scan_result_junk_clean"; //结果页Suspicious状态下点击Junk弹出框的clean
    public static final String CLICK_SCAN_RESULT_IGNORE = "click_scan_result_ignore"; //Danger状态下点击病毒应用
    public static final String CLICK_SCAN_RESULT_UNINSTALL = "click_scan_result_uninstall";  //Danger状态下点击病毒应用卸载
    public static final String CLICK_IGNORE_DELETE = "click_ignore_delete"; //白名单删除
    public static final String CLICK_ABOUT_EULA = "click_about_eula"; //关于页EULA
    public static final String CLICK_ABOUT_PRIVACY = "click_about_privacy"; //关于页privacy
    public static final String CLICK_DANGER_UNINSTALL = "click_danger_uninstall"; //危险列表卸载按钮
    public static final String CLICK_DANGER_ITEM = "click_danger_item"; //危险列表item
    public static final String CLICK_DANGER_DIALOG_IGNORE = "click_danger_dialog_ignore"; //危险列表dialog白名单
    public static final String CLICK_DANGER_DIALOG_UNINSTALL = "click_danger_dialog_uninstall"; //危险列表dialog卸载
    public static final String CLICK_RESULT_IGNORELIST = "click_result_ignorelist"; //结果页白名单
    public static final String CLICK_BROWSER_PRIVACY_CLEAN = "click_browser_privacy_clean"; //历史记录清理按钮
    public static final String ON_APP_START = "on_app_start"; //app启动

    //是否已经完成了评分或者渐变
    public static final String FINISHED_RATE_OR_FEEDBACK = "_finished_rate_or_feedback";
    //清理了隐私次数
    public static final String CLEANED_PRIVACY_AND_JUNK_COUNT = "_cleaned_privacy_and_junk_count";
    //清理了隐私次数
    public static final String CLEANED_PRIVACY_COUNT = "_cleaned_privacy_count";
    //清理的垃圾的次数
    public static final String CLEANED_JUNK_COUNT = "_cleaned_junk_count";
    //清理垃圾文件的大小
    public static final String CLEANED_JUNK_FILE_SIZE = "_cleaned_junk_file_size";
    //清理了几次病毒
    public static final String CLEANED_VIRUS_COUNT = "_cleaned_virus_count";

    //应用锁相关
    public static final String LOCK_IS_FIRST_LOCK = "is_lock"; //是否加过锁
    public static final String LOCK_TYPE = "lock_type"; //锁的类型 0 是图形 1 是数字
    public static final String LOCK_PACKAGE_NAME = "lock_package_name"; //点开的锁屏应用的包名
    public static final String LOCK_FROM = "lock_from"; //解锁后转跳的action
    public static final String LOCK_FROM_FINISH = "lock_from_finish"; //解锁后转跳的action是finish
    public static final String LOCK_FROM_SETTING = "lock_from_setting"; //解锁后转跳的action是setting
    public static final String LOCK_FROM_UNLOCK = "lock_from_unlock"; //解锁后转跳的action
    public static final String LOCK_FROM_LOCK_MAIN_ACITVITY = "lock_from_lock_main_activity"; //解锁后转跳的action是LockMainActivity
    public static final String LOCK_STATE = "app_lock_state"; //应用锁开关(状态，true开，false关)
    public static final String LOCK_PWD = "lock_pwd";//应用锁密码
    public static final String LOCK_EMAIL = "lock_email";//忘记密码的邮箱
    public static final String LOCK_IS_INIT_FAVITER = "lock_is_init_faviter"; //是否初始化了faviter数据表
    public static final String LOCK_IS_INIT_DB = "lock_is_init_db"; //是否初始化了数据库表
    public static final String APP_PACKAGE_NAME = "com.zz.ultra.security"; //包名
    public static final String LOCK_FAVITER_NUM = "lock_faviter_num"; //推荐加锁应用个数
    public static final String LOCK_SYS_APP_NUM = "lock_sys_app_num"; //系统应用个数
    public static final String LOCK_USER_APP_NUM = "lock_sys_app_num"; //非系统应用个数
    public static final String LOCK_IS_HIDE_LINE = "lock_is_hide_line"; //是否隐藏路径
    public static final String LOCK_IS_SELECT_ALL_APP = "lock_is_select_all_APP";
    public static final String LOCK_IS_SELECT_ALL_SYS = "lock_is_select_all_SYS";
    public static final int LOCK_FUID = 3;
    //应用锁设置相关
    public static final String LOCK_AUTO_SCREEN = "lock_auto_screen"; //是否在手机屏幕关闭后再次锁定
    public static final String LOCK_AUTO_SCREEN_TIME = "lock_auto_screen_time"; //是否在手机屏幕关闭后一段时间再次锁定
    public static final String LOCK_APP_QUIT = "lock_app_quit"; //是否在应用在退出后立即锁定
    public static final String LOCK_APP_ONCE_PWD = "lock_app_once_pwd"; //是否输入一次密码解锁全部应用
    public static final String LOCK_CURR_MILLISENCONS = "lock_curr_milliseconds"; //记录当前的时间（毫秒）
    public static final String LOCK_APART_MILLISENCONS = "lock_apart_milliseconds"; //记录相隔的时间（毫秒）
    public static final String LOCK_APART_TITLE = "lock_apart_title"; ///记录相隔的时间对应的标题
    public static final String LOCK_LAST_LOAD_PKG_NAME = "last_load_package_name";


    //上次检测病毒库更新时间
    public static final String LAST_CHECK_VIRUS_LIB_VERSION = "_last_check_virus_lib_version";
    //上次wifi扫描的时间
    public static final String WIFI_SCAN_LAST_TIME_MILLIS = "_wifi_scan_last_time_mills";

    //    设置界面相关偏好设置Key
    public static final String DATABASE_UPDATE = "database_update"; //数据库更新
    public static final String REAl_TIME_PROTECTION = "real_time_protection"; //实时保护
    public static final String DOWNLOAD_PRODUCTION = "download_protection"; //下载保护
    public static final String WEBSITE_PROTECTION = "website_protection"; //网站保护
    public static final String STRANGE_WIFI_ALERT = "strange_wifi_alert"; //模式wifi提醒
    public static final String RISK_WIFI_ALERT = "risky_wifi_alert"; //搞风险wifi提醒

    //上次释放带宽的时间
    public static final String LAST_RELEASE_BANDWIDTH_TIME = "_last_release_bundwidth_time";
    public static final String SD_CARD_FILE_COUNT = "_sd_card_file_count";

    //辅助功能已开启AccessibilityService
    public static final String ACCESSIBILITY_SERVICE_CONNECTED = "_accessibility_service_connected";

    //引擎初始化
    public static final String AVL_INIT_RESULT_SUCCESS = "_avl_init_result_success";

    // 当前时间
    public static final String SYSTEME_CURRENT_DATE = "systeme_current_date";

    //是否用过wifi检测
    public static final String USED_WIFI_SECURITY = "_used_wifi_security";
    //上次更新病毒库的时间
    public static final String LAST_UPDATE_VIRUS_LIB_TIME = "_last_update_virus_lib_time";

    //安全级别
    public static final String APP_SAFE_LEVEL = "app_safe_level";
    //上次从主页退出应用的时间
    public static final String LAST_MAIN_BACK_TIME="_last_main_back_time";
}