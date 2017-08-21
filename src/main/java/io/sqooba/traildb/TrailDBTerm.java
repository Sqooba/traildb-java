package io.sqooba.traildb;


public class TrailDBTerm {

    
    public TrailDBTerm(String name, String value, boolean isNegative) {
        this.fieldName = name;
        this.fieldValue = value;
        this.isNegative = isNegative;
    }

    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public boolean isNegative() {
        return isNegative;
    }

    private String fieldValue;
    private boolean isNegative;
    
}
