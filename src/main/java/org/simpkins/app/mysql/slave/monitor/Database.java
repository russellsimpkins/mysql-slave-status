package org.simpkins.app.mysql.slave.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;

@Component
@ConfigurationProperties
/**
 * The Database class is here to read the slaves json file from disk
 * and return an ArrayList<String> of database slaves. The name of
 * the slaves file is expected to be configured via property file 
 * 
 * @author Russell Simpkins
 */
public class Database {
    public static final Log log = LogFactory.getLog(Database.class);
    private String slavesFile;
    private String[] dbhosts;
    
    /**
     * This should get set spring via the application.properties
     * @param filename
     */
    public void setSlavesFile(String filename) {
        slavesFile = filename;
    }

    /** 
     * This function is going to read a json file that has all of the slaves
     * we need to check. 
     * @return ArrayList<String> of hostnames or IP addresses.
     * @throws JsonSyntaxException
     * @throws JsonIOException
     * @throws IOException
     */
    public ArrayList<String> getDbSlaves() throws JsonSyntaxException, JsonIOException, IOException {
        ArrayList<String> dbslaves = new ArrayList<>();
        if (dbhosts != null) {
            for (String h : dbhosts) {
                dbslaves.add(h);
            }
            return dbslaves;
        }
        if (slavesFile != null) {
            File f = new File(slavesFile);
            FileInputStream input = new FileInputStream(f);
            Gson gson = new Gson();
            Hosts hosts = gson.fromJson(new InputStreamReader(input), Hosts.class);
            dbhosts = hosts.getHosts();
            if (hosts != null) {
                for (String host : hosts.getHosts()) {
                    dbslaves.add(host);
                }
            }
        }
        return dbslaves;
    }
    
    /**
     * This function needs a host and the environment to configure a new ibatis
     * sql session.
     * @param host - the slave db to connect to
     * @param env - spring environment to get properties
     * @return SqlSessionFactory that's ready to use.
     */
    public SqlSessionFactory getSessionForHost(String host, Environment env) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setDatabaseName(env.getProperty("db"));
        ds.setUser(env.getProperty("uname"));
        ds.setPassword(env.getProperty("passwd"));
        ds.setServerName(env.getProperty("url"));
        ds.setConnectTimeout(Integer.parseInt(env.getProperty("connectTimeout")));
        org.apache.ibatis.mapping.Environment e = new org.apache.ibatis.mapping.Environment(env.getProperty("environment"),
                new JdbcTransactionFactory(), ds);
        org.apache.ibatis.session.Configuration c = new
                org.apache.ibatis.session.Configuration(e);
        c.addMapper(StatusMapper.class);
        c.setMapUnderscoreToCamelCase(true);
        return new SqlSessionFactoryBuilder().build(c);
    }
}
