package com.joutvhu.dynamic.jpa.repository.handlebars;

import com.joutvhu.dynamic.jpa.DynamicQuery;
import com.joutvhu.dynamic.jpa.entity.TableA;
import com.joutvhu.dynamic.jpa.model.TableAB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HandlebarsTableARepository extends JpaRepository<TableA, Long> {
    @DynamicQuery(value = "select t from TableA t where t.fieldB = :fieldB\n" +
            "{{#if fieldC}}\n" +
            "  and t.fieldC = :fieldC\n" +
            "{{/if}}"
    )
    List<TableA> findA1(Long fieldB, String fieldC);

    @Query(value = "select t from TableA t where t.fieldA = :fieldA and t.fieldC = :fieldC")
    List<TableA> findA2(Long fieldA, String fieldC);

    @DynamicQuery(value = "select new com.joutvhu.dynamic.jpa.model.TableAB(a, b) from TableA a inner join TableB b\n" +
            "on a.fieldA = b.fieldA\n" +
            "{{#fieldB}}\n" +
            "  and a.fieldB = :fieldB\n" +
            "{{/fieldB}}" +
            "{{#fieldD}}\n" +
            "  and b.fieldD = :fieldD\n" +
            "{{/fieldD}}"
    )
    List<TableAB> findJ(Long fieldB, Long fieldD);
}
