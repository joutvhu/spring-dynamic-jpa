package com.joutvhu.dynamic.jpa.repository.handlebars;

import com.joutvhu.dynamic.jpa.DynamicQuery;
import com.joutvhu.dynamic.jpa.entity.TableB;
import com.joutvhu.dynamic.jpa.model.ModelC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HandlebarsTableBRepository extends JpaRepository<TableB, Long> {
    @DynamicQuery
    List<TableB> findB1(String fieldE);

    @DynamicQuery
    List<TableB> findB2(Long maxD, Pageable pageable);

    @DynamicQuery
    Page<TableB> findB3(Long maxD, Pageable pageable);

    @DynamicQuery(nativeQuery = true)
    Long sumB1(Long maxD);

    @DynamicQuery("select t from TableB t\n" +
            "{{#if modelC.fieldC}}\n" +
            "  where t.fieldE = :#{#modelC.fieldC}\n" +
            "{{/if}}")
    List<TableB> findB4(ModelC modelC);
}
