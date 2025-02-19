package com.zju.QueryArtisan.mysql;

import com.zju.QueryArtisan.entity.BatchQueries;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BatchQueriesRepository extends JpaRepository<BatchQueries, Long> {

}
