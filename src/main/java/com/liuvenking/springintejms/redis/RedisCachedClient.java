package com.liuvenking.springintejms.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.utils.JsonBinder;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by venking on 15/11/28.
 */
@Component
public class RedisCachedClient {

    @Resource
    private RedisClient<String, String>  redisClient;

    public <T> T get(String key,TypeReference<T> t){
        String value=redisClient.get(key);
        System.out.println("stringValue:"+value);
        if(value==null){
            return null;
        }
        T module=null;
        if(value instanceof String){
            JsonBinder binder = JsonBinder.buildNonDefaultBinder();
            try {
                module = binder.getMapper().readValue((String)value, t);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return module;
    }

    public boolean set(String key, int exp, Object value) {
        boolean result = false;
        if(value==null){
            return result;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            String valueJson= mapper.writeValueAsString(value);
            System.out.println("writevalueJson:"+valueJson);
            result = redisClient.add(key,valueJson,(long)exp);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void set(String key, Object value) {
        if(value==null){
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            String valueJson= mapper.writeValueAsString(value);
            System.out.println("writevalueJson:"+valueJson);
//			JSONObject jsonSrc = JSONObject.fromObject(value);
//			String valueJson = jsonSrc.toString();
            redisClient.set(key,valueJson,null);
        }  catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void hSet(String key,String k ,String v) {
        if(k==null || v==null){
            return;
        }
        redisClient.hSet(key,k,v);
    }

    public void hSetAll(String key, Map<String,String> map) {
        if(map==null){
            return;
        }
        redisClient.hSetAll(key, map ,null);
    }

    public void hSetAll(String key, Map<String,String> map,Long expire) {
        if(map==null){
            return;
        }
        redisClient.hSetAll(key, map ,expire);
    }

    public String hGet(String key,String k){
        if(key==null || k==null ){
            return null;
        }
        return redisClient.hGet(key, k);
    }

    public Map<String,String> hGetAll(String key){
        if(key==null ){
            return null;
        }
        return redisClient.hGetAll(key);
    }

    public void hDelete(String key,String... fields){
        if(key==null ){
            return;
        }
        redisClient.hDelete(key,fields);
    }

    public Boolean hExists(String key,String... fields){
        if(key==null ){
            return null;
        }
        return redisClient.hExists(key,fields);
    }

    public String get(String key) {
        return redisClient.get(key);
    }

    public Boolean exists(String key){
        if(key==null ){
            return null;
        }
        return redisClient.exists(key);
    }

    public void delete(String key) {
        redisClient.delete(key);
    }
}
