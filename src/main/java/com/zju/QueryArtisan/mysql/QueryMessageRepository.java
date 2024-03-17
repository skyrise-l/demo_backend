package com.zju.QueryArtisan.mysql;


import com.zju.QueryArtisan.entity.QueryMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueryMessageRepository extends JpaRepository<QueryMessage, Long> {
    List<QueryMessage> findAllByQueryId(Long id);
}
