package com.waymaps.data.requestEntity.parameters;

import java.util.ArrayList;

/**
 * Created by Admin on 05.02.2018.
 */

public class ComplexParameters extends Parameter {

    private ArrayList<Parameter> params = new ArrayList<>();

    public ComplexParameters(Parameter...parameters){
        for (Parameter parameter : parameters){
            params.add(parameter);
        }
    }

    @Override
    public String getParameters() {
        String result ="";
        for (Parameter p : params){
            result+=p.getParameters() + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }
}
