package com.joutvhu.dynamic.jpa.freemarker;

import com.joutvhu.dynamic.jpa.JpaDynamicApplication;
import com.joutvhu.dynamic.jpa.entity.TableA;
import com.joutvhu.dynamic.jpa.entity.TableB;
import com.joutvhu.dynamic.jpa.entity.TableC;
import com.joutvhu.dynamic.jpa.model.ModelC;
import com.joutvhu.dynamic.jpa.model.TableAB;
import com.joutvhu.dynamic.jpa.repository.freemarker.FreemarkerTableARepository;
import com.joutvhu.dynamic.jpa.repository.freemarker.FreemarkerTableBRepository;
import com.joutvhu.dynamic.jpa.repository.freemarker.FreemarkerTableCRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JpaDynamicApplication.class, properties = "dynamic.jpa.template=freemarker")
@Transactional
public class JpaDynamicFreemarkerTest {

    @Autowired
    private FreemarkerTableARepository tableARepository;

    @Autowired
    private FreemarkerTableBRepository tableBRepository;

    @Autowired
    private FreemarkerTableCRepository tableCRepository;

    @Test
    public void findA1() {
        List<TableA> result = tableARepository.findA1(410L, "DSFGT4510A");
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findA1CNull() {
        List<TableA> result = tableARepository.findA1(104L, null);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findA1CEmpty() {
        List<TableA> result = tableARepository.findA1(104L, "");
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findA2() {
        List<TableA> result = tableARepository.findA2(195L, "DSFGT4510A");
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findAllA() {
        List<TableA> result = tableARepository.findAll();
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void findJ1() {
        List<TableAB> result = tableARepository.findJ(101L, 12042107L);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findJ2() {
        List<TableAB> result = tableARepository.findJ(104L, null);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void findJ3() {
        List<TableAB> result = tableARepository.findJ(null, 41017100L);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findJ4() {
        List<TableAB> result = tableARepository.findJ(null, null);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void findB1StartH() {
        List<TableB> result = tableBRepository.findB1("HBTVB");
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findB1StartG() {
        List<TableB> result = tableBRepository.findB1("GSDRB");
        Assertions.assertEquals(5, result.size());
    }

    @Test
    public void findB1All() {
        List<TableB> result = tableBRepository.findB1(null);
        Assertions.assertEquals(5, result.size());
    }

    @Test
    public void findB2() {
        List<TableB> result = tableBRepository.findB2(50000000L, Pageable.unpaged());
        Assertions.assertEquals(4, result.size());
    }

    @Test
    public void findB2UL() {
        List<TableB> result = tableBRepository.findB2(null, Pageable.unpaged());
        Assertions.assertEquals(5, result.size());
    }

    @Test
    public void findB2P() {
        List<TableB> result = tableBRepository.findB2(50000000L, PageRequest.of(1, 2, Sort.by("fieldA")));
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("HTYRB", result.get(0).getFieldE());
    }

    @Test
    public void findB3() {
        Page<TableB> result = tableBRepository.findB3(50000000L, PageRequest.of(0, 2, Sort.by("fieldA")));
        Assertions.assertEquals(2, result.getTotalPages());
    }

    @Test
    public void sumB1() {
        long result = tableBRepository.sumB1(40000000L);
        Assertions.assertEquals(33452681L, result);
    }

    @Test
    public void findB4() {
        List<TableB> result = tableBRepository.findB4(new ModelC(0L, "HTYRB"));
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findC1() {
        List<Long> c = new ArrayList<>();
        c.add(101L);
        c.add(104L);
        c.add(410L);
        Page<TableC> result = tableCRepository.search(null, "T", c,
                PageRequest.of(0, 2, Sort.by("fieldA")));
        Assertions.assertEquals(2, result.getTotalPages());
        Assertions.assertEquals(3L, result.getTotalElements());
    }
}