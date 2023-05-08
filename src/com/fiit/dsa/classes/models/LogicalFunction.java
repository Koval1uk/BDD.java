package com.fiit.dsa.classes.models;

import com.fiit.dsa.classes.utils.Utils;

import java.util.HashSet;

public class LogicalFunction
{
    private String function;

    public LogicalFunction(String function) { this.function = function;}

    public void optimize()
    {
        HashSet<String> variables = new HashSet<>();

        for (int i = 0; i < function.length(); i++)
        {
            if(function.charAt(i) != '!')
            {
                char variable = function.charAt(i);
                String negative =  Utils.getNegativeVariable(variable);

                if(variables.contains(negative))
                {
                    function = null;
                    return;
                }
                variables.add(String.valueOf(variable));
            }
            else
            {
                char variable = function.charAt(i + 1);

                if(variables.contains(String.valueOf(variable)))
                {
                    function = null;
                    return;
                }
                i++;
                variables.add(Utils.getNegativeVariable(variable));
            }
        }
        function = "";
        variables.forEach(variable -> function += variable);
    }

    public String getFunction()
    {
        if (function == null || function.equals(""))
            return null;

        return function;
    }
    public void setFunction(String function) { this.function = function; }

    @Override
    public String toString() { return "'" + function + "'"; }

    public int getVariableState(char parentVariable)
    {
        if(function == null)
            return -1;

        for (int i = 0; i < function.length(); i++)
        {
            if(function.charAt(i) == '!' && function.charAt(i + 1) == parentVariable)
                return 0;

            if(function.charAt(i) == parentVariable)
                return 1;
        }
        return -1;
    }

    public void removeVariable(String parentVariable)
    {
        function = function.replace(parentVariable, "");

        if(function.length() == 0)
            function = "1";
    }

    @Override
    public int hashCode() { return function == null ? 0 : function.hashCode(); }
}
