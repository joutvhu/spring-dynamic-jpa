package com.joutvhu.dynamic.jpa;

import com.joutvhu.dynamic.jpa.entity.TableA;
import com.joutvhu.dynamic.jpa.entity.TableB;
import com.joutvhu.dynamic.jpa.model.TableAB;
import com.joutvhu.dynamic.jpa.repository.TableARepository;
import com.joutvhu.dynamic.jpa.repository.TableBRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
//@TestPropertySource(locations = "classpath:application.properties")
//@ContextConfiguration(classes = JpaDynamicApplication.class)
@SpringBootTest(classes = JpaDynamicApplication.class)
@Transactional
public class JpaDynamicApplicationTest {
    @Autowired
    private TableARepository tableARepository;
    @Autowired
    private TableBRepository tableBRepository;

    @Test
    public void findA1() {
        List<TableA> result = tableARepository.findA1(410L, "DSFGT4510A");
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void findA1CNull() {
        List<TableA> result = tableARepository.findA1(104L, null);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void findA1CEmpty() {
        List<TableA> result = tableARepository.findA1(104L, "");
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void findA2() {
        List<TableA> result = tableARepository.findA2(195L, "DSFGT4510A");
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void findAllA() {
        List<TableA> result = tableARepository.findAll();
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void findJ1() {
        List<TableAB> result = tableARepository.findJ(101L, 12042107L);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void findJ2() {
        List<TableAB> result = tableARepository.findJ(104L, null);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void findJ3() {
        List<TableAB> result = tableARepository.findJ(null, 41017100L);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void findJ4() {
        List<TableAB> result = tableARepository.findJ(null, null);
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void findB1StartH() {
        List<TableB> result = tableBRepository.findB1("HBTVB");
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void findB1StartG() {
        List<TableB> result = tableBRepository.findB1("GSDRB");
        Assert.assertEquals(5, result.size());
    }

    @Test
    public void findB1All() {
        List<TableB> result = tableBRepository.findB1(null);
        Assert.assertEquals(5, result.size());
    }

    @Test
    public void findB2() {
        List<TableB> result = tableBRepository.findB2(50000000L, Pageable.unpaged());
        Assert.assertEquals(4, result.size());
    }

    @Test
    public void findB2UL() {
        List<TableB> result = tableBRepository.findB2(null, Pageable.unpaged());
        Assert.assertEquals(5, result.size());
    }

    @Test
    public void findB2P() {
        List<TableB> result = tableBRepository.findB2(50000000L, PageRequest.of(1, 2, Sort.by("fieldA")));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("HTYRB", result.get(0).getFieldE());
    }

    @Test
    public void findB3() {
        Page<TableB> result = tableBRepository.findB3(50000000L, PageRequest.of(0, 2, Sort.by("fieldA")));
        Assert.assertEquals(2, result.getTotalPages());
    }

    @Test
    public void sumB1() {
        long result = tableBRepository.sumB1(40000000L);
        Assert.assertEquals(33452681L, result);
    }
}