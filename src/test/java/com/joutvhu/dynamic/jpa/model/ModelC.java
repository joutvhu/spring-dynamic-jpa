package com.joutvhu.dynamic.jpa.model;

public class ModelC {
    private Long fieldA;

    private String fieldC;

    public ModelC(Long fieldA, String fieldC) {
        this.fieldA = fieldA;
        this.fieldC = fieldC;
    }

    public Long getFieldA() {
        return fieldA;
    }

    public void setFieldA(Long fieldA) {
        this.fieldA = fieldA;
    }

    public String getFieldC() {
        return fieldC;
    }

    public void setFieldC(String fieldC) {
        this.fieldC = fieldC;
    }
}
