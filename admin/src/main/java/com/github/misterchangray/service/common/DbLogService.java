package com.github.misterchangray.service.common;

import com.github.misterchangray.common.enums.DBEnum;
import com.github.misterchangray.common.utils.DateUtils;
import com.github.misterchangray.service.po.DbLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 保存需要插入数据库的日志
 * 主要用于保存业务逻辑日志
 */
@Service
public class DbLogService {
    @Autowired
    MongoDbService mongoDbService;
    Logger logger = LoggerFactory.getLogger(DbLogService.class);

    public boolean info(String tpl, Object... args) {
        logger.info(tpl, args);
        DbLog dbLog = null;
        if(null != tpl) {
            for(int i=0; i<args.length; i++) {
                tpl = tpl.replaceFirst("\\{\\}", String.valueOf(args[i]));
            }
        }
        dbLog = new DbLog();
        dbLog.setTime(System.currentTimeMillis());
        dbLog.setDate(DateUtils.now(null));
        dbLog.setLog(tpl);

        mongoDbService.insert(dbLog, DbLog.collectionName);
        return true;
    }

    public boolean info(DBEnum type, String tpl, Object... args) {
        DbLog dbLog = null;
        logger.info(tpl, args);
        if(null != tpl) {
            for(int i=0; i<args.length; i++) {
                tpl = tpl.replaceFirst("\\{\\}", String.valueOf(args[i]));
            }
        }
        dbLog = new DbLog();
        dbLog.setTime(System.currentTimeMillis());
        dbLog.setDate(DateUtils.now(null));
        dbLog.setType(type.getDesc());
        dbLog.setLog(tpl);

        mongoDbService.insert(dbLog, DbLog.collectionName);
        return true;
    }
}
