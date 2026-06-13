package net.souldev07.journalApp.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import net.souldev07.journalApp.entity.User;

@Repository
public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<User> getUsersWithSentimentAnalysisEnabled() {
        Query query = new Query();
        // query.addCriteria(Criteria.where("email").exists(true).ne(null).ne(""));
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"));
        query.addCriteria(Criteria.where("sentimentAnalysisEnabled").is(true));

        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }
}
