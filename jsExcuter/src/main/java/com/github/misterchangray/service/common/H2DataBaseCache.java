package com.github.misterchangray.service.common;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


@Component
public class H2DataBaseCache {
    private static Connection conn = null;
    private static Map<String, Boolean> tables = new HashMap();

    public static void main(String[] args) throws Exception {
        H2DataBaseCache h2DataBaseCache = new H2DataBaseCache();
//        System.out.println(h2DataBaseCache.set("a","12312321"));;
//        System.out.println(h2DataBaseCache.get("a"));;
//        System.out.println(h2DataBaseCache.set("a","asdwqweqeqwe"));;
//        System.out.println(h2DataBaseCache.get("a"));;
//        System.out.println(h2DataBaseCache.remove("a"));;
//        System.out.println(h2DataBaseCache.get("a"));;
        h2DataBaseCache.log("asd", "asdfsf6");
        System.out.println(h2DataBaseCache.log("asd", null,null));
    }

    private static void init() {
        try {
            if(null != conn && false == conn.isClosed()) return;
            //初始H2的数据库驱动
            Class.forName("org.h2.Driver");
            //获取数据库连接
            //jdbc:h2:mem:test, mem表示采用内存模式访问数据库
            Connection conn = DriverManager.getConnection("jdbc:h2:file:~/.h2/DBData;AUTO_SERVER=TRUE");
            //
            ////创建表并插入初始数据
            //
            String sql = "create table if not exists cacheMap (key varchar(255) primary key, value text);" +
                    "create table if not exists logs (id int auto_increment, key varchar(255), value text);" +
                    "create index if not exists keyMap on logs(key);";
            conn.createStatement().execute(sql);
            H2DataBaseCache.conn = conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int log(String key, String value) {
        init();
        PreparedStatement ppstmt = null;
        String tableName = "logs_" + key;
        try {
            if(null == tables.get(tableName)) {
                String sql = MessageFormat.format("create table if not exists {0} (id int auto_increment, value text);", tableName);
                conn.createStatement().execute(sql);
                tables.put(tableName, true);
            }

            ppstmt =  conn.prepareStatement(MessageFormat.format("insert into {0} values(null, ?)", tableName));
            ppstmt.setString(1, value);

            return  ppstmt.executeUpdate();
        } catch (SQLException e) {
            try {
                if(null != ppstmt && ppstmt.isClosed()) {
                    ppstmt.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return -1;
    }


    public boolean tableExist(String tableName) {
        boolean flag = false;
        try {
            ResultSet rs = conn.createStatement().executeQuery("select * from " + tableName);
            flag = rs.next();
        } catch (SQLException e) {}
        return flag;
    }

    public String log(String key, Integer limit, Integer page) {
        String tableName = "logs_" + key;

        init();
        if(false == tableExist(tableName)) return null;
        PreparedStatement ppstmt = null;
        try {
            if(null == limit) limit = 200;
            if(null == page) page = 1;
            ppstmt =  conn.prepareStatement(MessageFormat.format("select * from {0} order by id desc limit {1}, {2}", tableName, (page - 1) * limit, limit));
            ResultSet r = ppstmt.executeQuery();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            while (r.next()){
                if(1 != stringBuilder.length()) stringBuilder.append(",");
                stringBuilder.append(r.getString(2));
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        } catch (SQLException e) {
            try {
                if(null != ppstmt && ppstmt.isClosed()) {
                    ppstmt.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    public int logCount(String key) {
        init();
        String tableName = "logs_" + key;
        if(false == tableExist(tableName)) return 0;
        PreparedStatement ppstmt = null;
        try {
            ppstmt = conn.prepareStatement(MessageFormat.format("select count(id) from {0}", tableName));
            ResultSet resultSet = ppstmt.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            try {
                if(null != ppstmt && ppstmt.isClosed()) {
                    ppstmt.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return 0;
    }


    public String get(String key) {
        init();
        ////查询数据
        PreparedStatement ppstmt = null;
        try {
            ppstmt = conn.prepareStatement("select * from cacheMap where key=?");
            ppstmt.setString(1, key);
            ResultSet r = ppstmt.executeQuery();

            while (r.next()){
                return r.getString(2);
            }
        } catch (SQLException e) {
            try {
                if(null != ppstmt && ppstmt.isClosed()) {
                    ppstmt.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }
    public int set(String key, String value) {
        init();
        PreparedStatement ppstmt = null;
        try {
            if(null != get(key)) {
                ppstmt = conn.prepareStatement("update cacheMap set value=? where key=?");
                ppstmt.setString(1, value);
                ppstmt.setString(2, key);
            } else {
                ppstmt =  conn.prepareStatement("insert into cacheMap values(?, ?)");
                ppstmt.setString(1, key);
                ppstmt.setString(2, value);
            }

            return  ppstmt.executeUpdate();
        } catch (SQLException e) {
            try {
                if(null != ppstmt && ppstmt.isClosed()) {
                    ppstmt.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }
        return -1;
    }
    public int remove(String key) {
        init();
        PreparedStatement ppstmt = null;
        try {
            ppstmt = conn.prepareStatement("delete from cacheMap where key=?");
            ppstmt.setString(1, key);
            return  ppstmt.executeUpdate();
        } catch (SQLException e) {
            try {
                if(null != ppstmt && ppstmt.isClosed()) {
                    ppstmt.close();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return -1;
    }
}





