package com.waymaps.data.requestEntity.parameters;

/**
 * Created by Admin on 05.02.2018.
 */
//@todo change to StringParam
public class IdParam extends Parameter {
    private String id;

    public IdParam() {
    }

    public IdParam(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getParameters() {
        return toString();
    }
}
