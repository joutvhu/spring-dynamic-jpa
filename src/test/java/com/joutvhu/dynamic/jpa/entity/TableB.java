package com.joutvhu.dynamic.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TABLE_B")
public class TableB {
    @Id
    @Column(name = "FIELD_A")
    private Long fieldA;

    @Column(name = "FIELD_D")
    private Long fieldD;

    @Column(name = "FIELD_E")
    private String fieldE;

    public Long getFieldA() {
        return fieldA;
    }

    public void setFieldA(Long fieldA) {
        this.fieldA = fieldA;
    }

    public Long getFieldD() {
        return fieldD;
    }

    public void setFieldD(Long fieldD) {
        this.fieldD = fieldD;
    }

    public String getFieldE() {
        return fieldE;
    }

    public void setFieldE(String fieldE) {
        this.fieldE = fieldE;
    }
}
