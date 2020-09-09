package com.joutvhu.dynamic.jpa.model;

import com.joutvhu.dynamic.jpa.entity.TableA;
import com.joutvhu.dynamic.jpa.entity.TableB;

public class TableAB {
    private Long fieldA;
    private Long fieldB;
    private String fieldC;
    private Long fieldD;
    private String fieldE;

    public TableAB(TableA a, TableB b) {
        fieldA = a.getFieldA();
        fieldB = a.getFieldB();
        fieldC = a.getFieldC();
        fieldD = b.getFieldD();
        fieldE = b.getFieldE();
    }

    public Long getFieldA() {
        return fieldA;
    }

    public void setFieldA(Long fieldA) {
        this.fieldA = fieldA;
    }

    public Long getFieldB() {
        return fieldB;
    }

    public void setFieldB(Long fieldB) {
        this.fieldB = fieldB;
    }

    public String getFieldC() {
        return fieldC;
    }

    public void setFieldC(String fieldC) {
        this.fieldC = fieldC;
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
