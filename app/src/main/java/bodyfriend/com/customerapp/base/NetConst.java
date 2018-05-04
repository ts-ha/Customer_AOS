package bodyfriend.com.customerapp.base;

public class NetConst {

    public static final String HOST_REAL = "http://ctest.bodyfriend.co.kr/";
    //    public static final String HOST_TEST = "http://172.30.40.223:8222/";
    // 숙희주임 서버
    public static final String HOST_TEST_SUK = "http://121.138.34.240:8080/";
    // 민주임 서버
    public static final String HOST_TEST_MIN = "http://121.138.34.242:8080/";
//    public static final String HOST_TEST_MIN = "http://172.30.40.22:8090/";
    	public static final String HOST_TEST = "http://121.138.34.240:8181/";
    public static final String UNREACHABLE_URL = "file:///android_asset/unreachable.html";
    public static final String BREGE_URL = "file:///android_asset/androidbridge.html";
    public static String host = HOST_REAL;


//    public static final String URL_HOME_VIEW_HOME1 = "/home/view/home1";
    public static final String URL_PROMOTION_PROMOTION_MAIN = "/promotion/promotion_main";
    public static final String URL_BENEFIT_BENEFIT_MAIN = "/benefit/benefit_main";
    public static final String URL_PROMOTION_PROMOTION_LIST = "/promotion/promotion_list";
    public static final String URL_MYINFO_MAIN = "/myinfo/myinfo_main";

    public static void setHost(String host) {
        NetConst.host = host;
    }

    public static class Code {
        public static final int FAIL = 0;
        public static final int SUCCESS = 1;
        public static final int ERROR = 2;
        public static final int TIMEOUT = 999;
    }
}