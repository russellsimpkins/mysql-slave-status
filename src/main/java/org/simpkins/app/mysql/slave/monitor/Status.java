package org.simpkins.app.mysql.slave.monitor;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.SerializedName;

/**
 * A simple pojo to map back from the database result. I only bothered
 * to define secondsBehindMaster, but there are other fields returned
 * from "show slave status"
 */
@Component("status")
public class Status {
    @SerializedName("slave") private String host;
    @SerializedName("seconds_behind") private int secondsBehindMaster;

    public void setSecondsBehindMaster(int v) {
        secondsBehindMaster = v;
    }

    public int getSecondsBehindMaster() {
        return secondsBehindMaster;
    }
    
    public void setHost(String v) {
        this.host = v;
    }

    @Override
    public String toString() {
        return Integer.toString(secondsBehindMaster);
    }
}
