package com.liuvenking.springintejms.redis;

import org.apache.shiro.dao.DataAccessException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by venking on 15/11/28.
 */
@Component
public class RedisClient<K extends Serializable, V extends Serializable> {

    @Resource(name="redisTemplate")
    protected RedisTemplate<K, V> redisTemplate;

//    @Resource
//    private StringRedisTemplate redisTemplate;

    public RedisTemplate<K, V> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean add(final String key, final String value,final Long exp) {
        boolean resultBoolean = false;
        if(redisTemplate != null) {
            resultBoolean = redisTemplate.execute(new RedisCallback<Boolean>() {
                public Boolean doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys  = serializer.serialize(key);
                    byte[] values = serializer.serialize(value);
                    boolean res=connection.setNX(keys, values);
                    if(exp!=null){
                        connection.expire(keys, exp);
                    }
                    return res;
                }
            });
        }else{
            System.out.println(redisTemplate == null);
        }
        return resultBoolean;
    }

    public void set(final String key, final String value,final Long exp) {
        if(redisTemplate != null) {
            redisTemplate.execute(new RedisCallback<Void>() {
                public Void doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys  = serializer.serialize(key);
                    byte[] values = serializer.serialize(value);
                    connection.set(keys, values);
                    if(exp!=null){
                        connection.expire(keys, exp);
                    }
                    return null;
                }
            });
        }else{
            System.out.println("redisTemplate == null");
        }
    }
    public String get(final String key) {
        String resultStr = null;
        if(redisTemplate != null) {
            resultStr = redisTemplate.execute(new RedisCallback<String>() {
                public String doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys = serializer.serialize(key);
                    byte[] values = connection.get(keys);
                    if (values == null) {
                        return null;
                    }
                    String value = serializer.deserialize(values);
                    return value;
                }
            });
        }
        return resultStr;
    }

    public void hSet(final String mapKey, final String k,final String v) {
        if(redisTemplate != null) {
            redisTemplate.execute(new RedisCallback<Void>() {
                public Void doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys  = serializer.serialize(mapKey);
                    connection.hSet(keys,  serializer.serialize(k),serializer.serialize(v));
                    return null;
                }
            });
        }else{
            System.out.println("redisTemplate == null");
        }
    }

    public void hSetAll(final String key, final Map<String,String> map,final Long exp) {
        if(redisTemplate != null) {
            redisTemplate.execute(new RedisCallback<Void>() {
                public Void doInRedis(RedisConnection connection)throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys  = serializer.serialize(key);
                    for(String k:map.keySet()){
                        if(map.get(k)!=null)
                            connection.hSet(keys, serializer.serialize(k),serializer.serialize(map.get(k)));
                    }
                    if(exp!=null){
                        connection.expire(keys, exp);
                    }
                    return null;
                }
            });
        }else{
            System.out.println("redisTemplate == null");
        }
    }
    public Map<String,String> hGetAll(final String key) {
        Map<String, String> resultMap = null;
        if(redisTemplate != null) {
            resultMap = redisTemplate.execute(new RedisCallback<Map<String, String>>() {
                public Map<String, String> doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    Map<String,String> map = new HashMap<String,String>();
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys = serializer.serialize(key);
                    Map<byte[], byte[]> valuesMap = connection.hGetAll(keys);
                    if (valuesMap == null) {
                        return null;
                    }else{
                        for(byte[] k:valuesMap.keySet()){
                            map.put(serializer.deserialize(k),serializer.deserialize(valuesMap.get(k)));
                        }
                    }
                    return map;
                }
            });
        }
        return resultMap;
    }

    public String hGet(final String key,final String k) {
        String result = null;
        if(redisTemplate != null) {
            result = redisTemplate.execute(new RedisCallback<String>() {
                public String doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys = serializer.serialize(key);
                    byte[] values = connection.hGet(keys,serializer.serialize(k));
                    if (values == null) {
                        return null;
                    }
                    return serializer.deserialize(values);
                }
            });
        }
        return result;
    }

    public void hDelete(final String key,final String...fields) {
        if(redisTemplate != null) {
            redisTemplate.execute(new RedisCallback<Void>() {
                public Void doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys = serializer.serialize(key);
                    if(fields==null||fields.length==0)
                        System.out.println(connection.del(keys));
                    else{
                        for(String field:fields)
                            System.out.println(connection.hDel(keys,serializer.serialize(field)));
                    }
                    return null;
                }
            });
        }
    }

    public void delete(final String key) {
        if(redisTemplate != null) {
            redisTemplate.execute(new RedisCallback<Void>() {
                public Void doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys = serializer.serialize(key);
                    connection.del(keys);
                    return null;
                }
            });
        }
    }

    public Boolean exists(final String key){
        if(redisTemplate != null) {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                public Boolean doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys = serializer.serialize(key);
                    return connection.exists(keys);
                }
            });
        }
        return null;
    }

    public Boolean hExists(final String key,final String... fields){
        if(redisTemplate != null) {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                public Boolean doInRedis(RedisConnection connection)
                        throws DataAccessException {
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] keys = serializer.serialize(key);
                    if(fields==null||fields.length==0)
                        return connection.exists(keys);
                    else{
                        boolean f=true;
                        for(String field:fields){
                            f=f&&connection.hExists(keys,serializer.serialize(field));
                        }
                        return f;
                    }
                }
            });
        }
        return null;
    }
    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("/WebContent/WEB-INF/spring-base.xml");
        RedisTemplate<String, String> redisTemplate=(RedisTemplate<String, String>) ctx.getBean("redisTemplate");
        RedisClient<String,String> redisClient=new RedisClient<String, String>();
        redisClient.setRedisTemplate(redisTemplate);
        Map<String,String> userMap=new HashMap<String,String>();
        userMap.put("uid","123" );
        userMap.put("device_token","12312312dsadas1323ds");
        userMap.put("phone_token","dad;asmkldmalksmdlsak");
        userMap.put("phone","按时间段就阿森纳");
        redisClient.hSetAll("phone sasda21jk3n1kjnsjkn",userMap,100L);
    }
}
