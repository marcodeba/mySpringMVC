package com.gupaoedu.vip.mongo.demo.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.core.common.mongo.BaseDaoSupport;
import javax.core.common.mongo.QueryRule;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.gupaoedu.vip.mongo.demo.entity.Hotel;


@Repository
public class HotelDao extends BaseDaoSupport<Hotel, Long>{
	
	public void getByName(String name){
		
		QueryRule queryRule = QueryRule.getInstance();
		
		super.find(queryRule);
		
	}
	
	public List<Hotel> getAll(){
		
		QueryRule queryRule = QueryRule.getInstance();
		
		return super.find(queryRule);
		
	}
	
	@Resource(name="mongoTemplate")
	protected void setTemplate(MongoTemplate template) {
		super.setTemplate(template);
	}




	@Override
	protected String getPKColumn() {
		return "id";
	}
	
}
