package org.simpkins.app.mysql.slave.monitor;

import com.google.gson.annotations.SerializedName;
/**
 * A simple pojo to deserialize json into
 * @author Russell Simpkins <russellsimpkins at gmail.com>
 */
public class Hosts {
    @SerializedName("slaves") private String[] hosts;
    
    public void setHosts(String[] s) {
        hosts = s;
    }
    
    public String[] getHosts() {
        return this.hosts;
    }
}
