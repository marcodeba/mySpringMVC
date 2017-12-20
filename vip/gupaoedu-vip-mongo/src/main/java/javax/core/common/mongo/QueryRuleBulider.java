package javax.core.common.mongo;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class QueryRuleBulider {
	
	public  QueryRuleBulider(QueryRule queryRule) {
		//根据RuleList来循环，动态生成各种Criteria
		queryRule.getRuleList();
	}
	
	public Query getQuery(){
		Query query = new Query(new Criteria());
		return query;
	}
	
}
