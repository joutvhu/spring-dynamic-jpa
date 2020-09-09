package com.joutvhu.dynamic.jpa.repository;

import com.joutvhu.dynamic.jpa.DynamicQuery;
import com.joutvhu.dynamic.jpa.entity.TableB;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableBRepository extends JpaRepository<TableB, String> {
    @DynamicQuery
    List<TableB> findB1(String fieldE);

    @DynamicQuery
    List<TableB> findB2(Long maxD, Pageable pageable);

    @DynamicQuery
    Page<TableB> findB3(Long maxD, Pageable pageable);

    @DynamicQuery(nativeQuery = true)
    Long sumB1(Long maxD);
}
