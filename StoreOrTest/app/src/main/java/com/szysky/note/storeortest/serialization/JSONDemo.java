package com.szysky.note.storeortest.serialization;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;

/**
 * Author :  suzeyu
 * Time   :  2016-10-02  下午9:16
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 * <p>
 * ClassDescription : 练习android中的JSON
 */

public class JSONDemo {

    /**
     * 一个简单的JSON格式字符串
     */
    public static final String JSON_SAMPLE = "[\n" +
            "    {\n" +
            "        \"name\":\"张三\"\n" +
            "        \"id\"  :\"1\"\n" +
            "        \"sex\" :\"男\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"张三\"\n" +
            "        \"id\"  :\"1\"\n" +
            "        \"sex\" :\"男\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"name\":\"张三\"\n" +
            "        \"id\"  :\"1\"\n" +
            "        \"sex\" :\"男\"\n" +
            "    }\n" +
            "]";



    public static JSONArray readTasksFromInputStream(InputStream stream) {

        InputStreamReader reader = new InputStreamReader(stream);
        JsonReader jsonReader = new JsonReader(reader);

        JSONArray jsonArray = new JSONArray();

        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                JSONObject jsonObject = readSingleTask(jsonReader);
                jsonArray.put(jsonObject);
            }
            jsonReader.endArray();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    /**
     *  读取一个JSONReader实例中的一个json对象的全部属性, 并返回
     */
    private static JSONObject readSingleTask(JsonReader jsonReader) throws IOException, JSONException {

        // 创建一个JSONObject对象准备装载整个JSON字符串中的某一个对象
        JSONObject jsonObject = new JSONObject();
        jsonReader.beginObject();
        JsonToken token;

        do {
            String name = jsonReader.nextName();
            if (name.equals("name")) {
                jsonObject.put("name", jsonReader.nextString());
            } else if (name.equals("id")) {
                jsonObject.put("id", jsonReader.nextString());
            } else if (name.equals("sex")) {
                jsonObject.put("sex", jsonReader.nextString());
            }
            token = jsonReader.peek();
        }while(token != null && !token.equals(JsonToken.END_OBJECT));

        jsonReader.endObject();

        return jsonObject;
    }

    /**
     *  利用JsonWriter类允许往outputStream高效的写入数据
     */
    public void writeJsonToStream(JSONArray array, OutputStream stream) throws IOException, JSONException {

        OutputStreamWriter writer = new OutputStreamWriter(stream);
        JsonWriter jsonWriter = new JsonWriter(writer);

        int length = array.length();
        jsonWriter.beginArray();

        for (int i = 0; i < length; i++) {

            JSONObject jsonObject = array.getJSONObject(i);

            jsonWriter.beginObject();
            jsonWriter.name("name").value(jsonObject.getString("name"));
            jsonWriter.name("id").value(jsonObject.getString("id"));
            jsonWriter.name("sex").value(jsonObject.getString("sex"));

            jsonWriter.endObject();
        }

        jsonWriter.endArray();
        jsonWriter.close();
    }
}
