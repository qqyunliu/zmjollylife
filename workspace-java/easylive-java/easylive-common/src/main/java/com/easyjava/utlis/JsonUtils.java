package com.easyjava.utlis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/*这是一个JSON处理工具类 JsonUtils，它基于阿里巴巴的FastJSON库提供了简单的JSON序列化和反序列化功能*/
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
/*将Java对象转换为JSON字符串*/
    public static String convertObj2Json(Object obj) {
        return JSON.toJSONString(obj);
    }
/*. JSON字符串转对象*/
    public static <T> T convertJson2Obj(String json, Class<T> classz) {
        return JSONObject.parseObject(json, classz);
    }



/*将JSON数组字符串转换为指定类型的Java列表*/
    public static <T> List<T> convertJsonArray2list(String json, Class<T> classz) {
        return JSONArray.parseArray(json, classz);

    }

    public static void main(String[] args) {

    }
}

