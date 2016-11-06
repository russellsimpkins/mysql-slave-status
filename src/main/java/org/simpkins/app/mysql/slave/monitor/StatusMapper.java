package org.simpkins.app.mysql.slave.monitor;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * myBatis mapper object.
 * @author Russell Simpkins <russellsimpkins at google.com>
 */
@Mapper
public interface StatusMapper {

    @Select("show slave status")
    Status selectSlaveStatus();
}
