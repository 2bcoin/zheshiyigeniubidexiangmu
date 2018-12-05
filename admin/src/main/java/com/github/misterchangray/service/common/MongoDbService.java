package com.github.misterchangray.service.common;

import com.github.misterchangray.common.utils.JSONUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Created by rui.zhang on 2018/7/8.
 * @author rui.zhang
 * @version ver1.0
 * @email misterchangray@hotmail.com
 * @description
 */
@Component
public class MongoDbService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Object object, String collectionName) {
        mongoTemplate.insert(object, collectionName);
    }

    public Object findOne(Map<String, Object> params, String collectionName, Class clazz ) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(params.get("id"))), clazz, collectionName);
    }

    /**
     * 根据条件 返回指定条件的集合
     * @param q
     * @param collectionName
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> find(Query q, String collectionName, Class clazz ) {
        List<T> res = mongoTemplate.find(q, clazz, collectionName);
        return res;
    }


    public List<Object> findAll(String collectionName, Class clazz) {
        return mongoTemplate.findAll(clazz, collectionName);
    }

    public void update(Map<String, Object> params, String collectionName, Class clazz) {
        if(null != params && 0 == params.keySet().size()) return;
        Update update = new Update();
        for(String key : params.keySet()) {
            update.set(key, params.get(key));
        }
        mongoTemplate.upsert(new Query(Criteria.where("id").is(params.get("id"))), update, clazz, collectionName);
    }


    public void update(Criteria criteria, Map<String, Object> params, String collectionName, Class clazz) {
        if(null != params && 0 == params.keySet().size()) return;
        Update update = new Update();
        for(String key : params.keySet()) {
            update.set(key, params.get(key));
        }
        mongoTemplate.updateMulti(new Query(criteria), update, clazz, collectionName);
    }

    public void createCollection(String collectionName) {
        mongoTemplate.createCollection(collectionName);
    }

    public void remove(Map<String, Object> params, String collectionName, Class clazz) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(params.get("id"))), clazz, collectionName);
    }
}
