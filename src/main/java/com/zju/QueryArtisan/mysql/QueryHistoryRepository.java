package com.zju.QueryArtisan.mysql;


import org.springframework.data.jpa.repository.JpaRepository;
import com.zju.QueryArtisan.entity.mysqlEntity.QueryHistory;

import java.util.Optional;

public interface QueryHistoryRepository extends JpaRepository<QueryHistory, String >{
    Optional<QueryHistory> findById(String Id);
}

