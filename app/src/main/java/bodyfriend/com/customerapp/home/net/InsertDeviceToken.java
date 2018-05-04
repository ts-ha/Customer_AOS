package bodyfriend.com.customerapp.home.net;

import bodyfriend.com.customerapp.base.BFEnty;

/**
 * Created by ts.ha on 2017-04-17.
 */

public class InsertDeviceToken extends BFEnty {

    public InsertDeviceToken(String userId, String deviceId) {
        setUrl("/login/api/updateDeviceId.json");

        setParam("userId", userId, "deviceId", deviceId);
    }

    public Data data;

    public static class Data {
        public int resultCode;
    }
}
