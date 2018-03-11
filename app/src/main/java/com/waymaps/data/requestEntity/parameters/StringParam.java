package com.waymaps.data.requestEntity.parameters;

/**
 * Created by Admin on 10.03.2018.
 */

public class StringParam extends Parameter {
    private String string;

    public StringParam() {
    }

    public StringParam(String string) {
        this.string = string;
    }

    public String getId() {
        return string;
    }

    public void setId(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public String getParameters() {
        return "'"+toString()+"'";
    }
}