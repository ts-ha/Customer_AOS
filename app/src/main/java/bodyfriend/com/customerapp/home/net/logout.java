package bodyfriend.com.customerapp.home.net;

import bodyfriend.com.customerapp.base.BFEnty;

/**
 * Created by 이주영 on 2016-09-27.
 */
public class logout extends BFEnty {
    public logout() {
        setUrl("login/api/logout.json");
    }

    public Data data;

    public static class Data {
        public int resultCode;
    }
}