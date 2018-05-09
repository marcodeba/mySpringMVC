package com.gupaoedu.vip.orm.demo.dao;

import com.gupaoedu.vip.orm.framework.BaseDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by Tom on 2018/5/9.
 */
@Repository
public class MemberDao extends BaseDaoSupport {

    @Resource(name="dataSource")
    protected void setDataSource(DataSource dataSource) {
        System.out.println(dataSource);
    }
}
