package org.uoc.pfc.eventual.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.uoc.pfc.eventual.repository.TokenRepository;

@Configuration
public class RepositoryConfig {

    @Autowired
    private MongoTemplate template;

    @Bean
    public MongoRepositoryFactory getMongoRepositoryFactory() {
	try {
	    return new MongoRepositoryFactory(template);
	} catch (Exception e) {
	    throw new RuntimeException("error creating mongo repository factory", e);
	}
    }

    @Bean
    public EventRepository getEventRespository() {
	try {
	    return new EventRepository(getMongoRepositoryFactory(), template);
	} catch (Exception e) {
	    throw new RuntimeException("error creating Repository", e);
	}
    }

    @Bean
    public UserRepository getUserRespository() {
	try {
	    return new UserRepository(getMongoRepositoryFactory(), template);
	} catch (Exception e) {
	    throw new RuntimeException("error creating Repository", e);
	}
    }

    @Bean
    public TokenRepository getTokenRepository() {
	try {
	    return new TokenRepository(getMongoRepositoryFactory(), template);
	} catch (Exception e) {
	    throw new RuntimeException("error creating Repository", e);
	}
    }
}
