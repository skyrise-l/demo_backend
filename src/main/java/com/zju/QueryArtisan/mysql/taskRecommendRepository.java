package com.zju.QueryArtisan.mysql;


import com.zju.QueryArtisan.entity.mysqlEntity.TaskRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface taskRecommendRepository extends JpaRepository<TaskRecommend, Long>{
    @Query("SELECT t FROM TaskRecommend t WHERE t.id = (SELECT MAX(t2.id) FROM TaskRecommend t2)")
    TaskRecommend findMaxIdTaskRecommend();


    @Modifying
    @Query(value = "DELETE FROM TaskRecommend WHERE id = ?1 AND id != 1", nativeQuery = true)
    void deleteMaxIdTaskRecommend(Long id);
}