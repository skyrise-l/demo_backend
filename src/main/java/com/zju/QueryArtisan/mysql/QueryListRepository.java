package com.zju.QueryArtisan.mysql;


import com.zju.QueryArtisan.entity.QueryList;
import com.zju.QueryArtisan.entity.QueryMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QueryListRepository extends JpaRepository<QueryList, Long> {
    QueryList findByHashValue(String HashValue);
}
