package com.zju.QueryArtisan.mysql;


import com.zju.QueryArtisan.entity.MysqlMessage;
import com.zju.QueryArtisan.entity.QueryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MysqlMessageRepository extends JpaRepository<MysqlMessage, Long> {
    QueryData findByHashValue(String HashValue);
}
