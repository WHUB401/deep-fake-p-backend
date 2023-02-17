package com.pharaoh.deepfake.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;

@Component
public class VariableTable {

    CrmProperties crmProperties = SpringContextUtil.getBean(CrmProperties.class);

    public boolean create( String tableName ){
        {
            try (Connection conn = DriverManager.getConnection(crmProperties.getMysqlUrl(), crmProperties.getUserName(), crmProperties.getPassword());
                 Statement stat = conn.createStatement();){
                String sqlStatement = "CREATE TABLE "
                        +tableName
                        + "(  filename varchar(50) not null ,"
                        + " PRIMARY KEY ( `filename` )"
                        + " ) ENGINE=InnoDB AUTO_INCREMENT=2897 DEFAULT CHARSET=utf8 COMMENT='图片数据集'; ";
                stat.executeLargeUpdate(sqlStatement);
                return true;
            } catch (SQLException e) {
                myLogger.error("DeefakeDetection Error!",e);
                return false;
            }
        }
    }

    public boolean insert( String tableName, String filename ){

        try (Connection conn = DriverManager.getConnection(crmProperties.getMysqlUrl(), crmProperties.getUserName(), crmProperties.getPassword());
             Statement stat = conn.createStatement();){
            String sqlStatement = "insert into " + tableName + " values('" + filename + "')";
            stat.executeLargeUpdate(sqlStatement);
            return true;
        } catch (SQLException e) {
            myLogger.error("DeefakeDetection Error!",e);
            return false;
        }

    }

    public boolean delete( String tableName, String filename ){

        try (Connection conn = DriverManager.getConnection(crmProperties.getMysqlUrl(), crmProperties.getUserName(), crmProperties.getPassword());
             Statement stat = conn.createStatement();){
            String sqlStatement = "delete from " + tableName + " where "+"filename='" + filename + "'";
            stat.executeUpdate(sqlStatement);
            return true;
        } catch (SQLException e) {
            myLogger.error("DeefakeDetection Error!",e);
            return false;
        }

    }

    public List<String> findAll( String tableName ){

        List<String> filenameList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(crmProperties.getMysqlUrl(), crmProperties.getUserName(), crmProperties.getPassword());
             Statement stat = conn.createStatement();){
            String sqlStatement = "select * from " + tableName;
            ResultSet result = stat.executeQuery(sqlStatement);
            while (result.next()) {
                filenameList.add(result.getString("filename"));
            }
        } catch (SQLException e) {
            myLogger.error("DeefakeDetection Error!",e);
        }
        return filenameList;
    }
}
