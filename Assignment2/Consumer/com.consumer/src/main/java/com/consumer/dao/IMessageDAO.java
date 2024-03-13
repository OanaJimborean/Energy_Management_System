package com.consumer.dao;

import com.consumer.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMessageDAO extends JpaRepository<Message, Long> {

}
