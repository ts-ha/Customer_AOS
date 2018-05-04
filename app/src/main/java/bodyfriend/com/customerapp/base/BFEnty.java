package bodyfriend.com.customerapp.base;

import android.data.Pojo;
import android.miscellaneous.Log;
import android.net.NetEnty;
import android.util.SDF;

import com.android.volley.Request.Method;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import bodyfriend.com.customerapp.base.util.OH;
import bodyfriend.com.customerapp.base.util.PP;

public class BFEnty extends NetEnty {

    String err;
    String err_msg;
    String session_msg;

    private boolean isShowProgress = true;

    protected void setEnableProgress(boolean isEnable) {
        isShowProgress = isEnable;
    }

    public String getSessionMsg() {
        return session_msg;
    }

    private Gson gson;

    public BFEnty() {
        super(Method.POST);

        try {
            getHeaders().put("Cookie", String.format("JSESSIONID=%s", PP.SID.get()));
        } catch (Exception e) {
        }

        gson = new GsonBuilder()//
                .registerTypeAdapter(Date.class, deserializer_date)//
                .registerTypeAdapter(boolean.class, deserializer_boolean)//
                .registerTypeAdapter(ArrayList.class, deserializer_stringarraylist)//
                .create();
    }

    protected void setParam(Object... key_value) {
        if (key_value.length % 2 == 1)
            throw new IllegalArgumentException("!!key value must pair");

        int N = (key_value.length) / 2;
        for (int i = 0; i < N; i++) {
            final String key = (String) key_value[i * 2];
            final Object value = key_value[i * 2 + 1];
            // Log.l(key, value);
            if (value == null) {
                params.put(key, null);
            } else if (value.getClass().isArray()) {
                final Object[] values = (Object[]) value;
                for (int j = 0; j < values.length; j++)
                    params.put(key + "[" + j + "]", values[j]);
            } else if (value.getClass().isAssignableFrom(ArrayList.class)) {
                final ArrayList<?> values = (ArrayList<?>) value;
                for (int j = 0; j < values.size(); j++)
                    params.put(key + "[" + j + "]", values.get(j));
            } else if (value instanceof Boolean) {
                params.put(key, (((Boolean) value) ? "1" : "0"));
            } else {
                params.put(key, value);
            }
        }
    }

    @Override
    protected void parse(String json) {
        super.parse(json);

        try {
            // Log.l(Arrays.toString(getClass().getClasses()));
            final Class<?> cls = Class.forName(getClass().getName() + "$Data");
            final Field field = getClass().getField("data");
            field.set(this, gson.fromJson(json, cls));
        } catch (Exception e) {
            Log.l(this, e.getMessage());
            new Pojo(getClass(), json).gen().toLog();
        }

        try {
            JSONObject jo = new JSONObject(json);
            err = jo.isNull("err") ? "N" : jo.getString("err");
            err_msg = jo.isNull("err_msg") ? "성공" : jo.getString("err_msg");
            success = !err.equals("Y");
            errorMessage = err_msg;

            session_msg = jo.isNull("session_msg") ? "" : jo.getString("session_msg");

        } catch (JSONException e) {
            e.printStackTrace();
            error(e);
        }
        // Log.l(getClass().getSimpleName() + "<" + success + ">", errorMessage);
    }

    @Override
    protected void error(Exception error) {
        super.error(error);
        success = false;
    }

    private JsonDeserializer<Date> deserializer_date = new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull() || json.getAsString().length() <= 0)
                return null;

            // Log.l(json.getAsString());
            try {
                return SDF.yyyymmddhhmmss_1.parseDate(json.getAsString());
            } catch (Exception e) {
                return null;
            }
        }
    };
    private JsonDeserializer<Boolean> deserializer_boolean = new JsonDeserializer<Boolean>() {
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return !json.isJsonNull() && json.getAsString().equals("Y");
        }

    };
    private JsonDeserializer<ArrayList<String>> deserializer_stringarraylist = new JsonDeserializer<ArrayList<String>>() {
        public ArrayList<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull())
                return null;

            ArrayList<String> list = Lists.newArrayList();
            JsonArray ja = json.getAsJsonArray();
            for (JsonElement je : ja) {
                String spf_file = je.getAsJsonObject().get("spf_file").getAsString();
                // Log.l(spf_file);
                list.add(spf_file);
            }
            return list;
        }

    };

    public void setUrl(String url) {
        this.url = NetConst.host + url;
    }
}
