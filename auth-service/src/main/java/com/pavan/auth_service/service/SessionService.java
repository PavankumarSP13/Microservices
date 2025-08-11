//package com.pavan.auth_service.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.concurrent.TimeUnit;
//
//public class SessionService {
//
//    @Autowired
//    RedisTemplate<String,String> redis;
//
//    public void logout(String token, String user, long expiryMs) {
//        redis.opsForValue().set("blacklist:"+token,"1",expiryMs, TimeUnit.MILLISECONDS);
//        redis.delete("session:"+user);
//    }
//
//    public boolean sessionActive(String user) {
//        return Boolean.TRUE.equals(redis.hasKey("session:"+user));
//    }
//}
