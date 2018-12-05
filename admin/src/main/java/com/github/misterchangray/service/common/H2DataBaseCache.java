package com.github.misterchangray.service.common;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.text.MessageFormat;


@Component
public class H2DataBaseCache {
    private static Connection conn = null;

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
            if(null != conn) return;
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
        try {
            PreparedStatement ppstmt = null;
            ppstmt =  conn.prepareStatement("insert into logs values(null, ?, ?)");
            ppstmt.setString(1, key);
            ppstmt.setString(2, value);

            return  ppstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public String log(String key, Integer limit, Integer page) {
        init();
        try {
            if(null == limit) limit = 200;
            if(null == page) page = 1;

            PreparedStatement ppstmt = null;
            ppstmt =  conn.prepareStatement("select * from logs where key = ? order by id desc limit 200 ");
            ppstmt.setString(1, key);
            ResultSet r = ppstmt.executeQuery();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            while (r.next()){
                if(1 != stringBuilder.length()) stringBuilder.append(",");
                stringBuilder.append(r.getString(3));
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String get(String key) {
        init();
        ////查询数据
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            PreparedStatement ppstmt = conn.prepareStatement("select * from cacheMap where key=?");
            ppstmt.setString(1, key);
            ResultSet r = ppstmt.executeQuery();

            while (r.next()){
                return r.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int set(String key, String value) {
        init();
        try {
            PreparedStatement ppstmt = null;
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
            e.printStackTrace();
        }
        return -1;
    }
    public int remove(String key) {
        init();
        try {
            PreparedStatement ppstmt = conn.prepareStatement("delete from cacheMap where key=?");
            ppstmt.setString(1, key);
            return  ppstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}





