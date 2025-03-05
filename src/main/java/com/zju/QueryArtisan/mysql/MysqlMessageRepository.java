package com.zju.QueryArtisan.mysql;


import com.zju.QueryArtisan.entity.mysqlEntity.MysqlMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MysqlMessageRepository extends JpaRepository<MysqlMessage, String> {
    MysqlMessage findByHashValue(String HashValue);
}
