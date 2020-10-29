package com.joutvhu.dynamic.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TABLE_C")
public class TableC {
    @Id
    @Column(name = "FIELD_A")
    private Long fieldA;

    @Column(name = "FIELD_B")
    private String fieldB;

    @Column(name = "FIELD_C")
    private Long fieldC;

    public Long getFieldA() {
        return fieldA;
    }

    public void setFieldA(Long fieldA) {
        this.fieldA = fieldA;
    }

    public String getFieldB() {
        return fieldB;
    }

    public void setFieldB(String fieldB) {
        this.fieldB = fieldB;
    }

    public Long getFieldC() {
        return fieldC;
    }

    public void setFieldC(Long fieldC) {
        this.fieldC = fieldC;
    }
}
