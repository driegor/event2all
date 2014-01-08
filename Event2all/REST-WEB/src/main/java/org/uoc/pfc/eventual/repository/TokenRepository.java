package org.uoc.pfc.eventual.repository;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.uoc.pfc.eventual.config.AppConst;
import org.uoc.pfc.eventual.model.security.Token;
import org.uoc.pfc.eventual.repository.impl.BaseRepository;

public class TokenRepository extends BaseRepository<Token> {

	public TokenRepository(MongoRepositoryFactory mongoRepositoryFactory, MongoTemplate mongoTemplate) {
		super(mongoRepositoryFactory, mongoTemplate, Token.class);
	}

	public Token find(String token) {
		Query query = new Query();
		query.addCriteria(Criteria.where("token").is(token));
		return executeQuery(query, Boolean.TRUE);
	}

	public void purge() {
		Query query = new Query();
		query.addCriteria(Criteria.where("createdDate").lt(new DateTime(System.currentTimeMillis() - AppConst.Security.CSRF_TOKEN_DURATION)));
		delete(executeQuery(query));
	}

	public void purge(String token) {
		Query query = new Query();
		query.addCriteria(Criteria.where("token").is(token));
		delete(executeQuery(query));
	}
}
