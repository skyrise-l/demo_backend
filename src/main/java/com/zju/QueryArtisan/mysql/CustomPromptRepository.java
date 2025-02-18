package com.zju.QueryArtisan.mysql;


import com.zju.QueryArtisan.entity.CustomPrompt;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomPromptRepository extends JpaRepository<CustomPrompt, Long> {

}

