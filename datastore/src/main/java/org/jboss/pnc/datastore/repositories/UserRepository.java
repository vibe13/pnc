package org.jboss.pnc.datastore.repositories;

import org.jboss.pnc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, Integer>, QueryDslPredicateExecutor<User> {

}
