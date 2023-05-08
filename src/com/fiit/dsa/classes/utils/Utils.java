package com.fiit.dsa.classes.utils;

import com.fiit.dsa.classes.models.LogicalFunction;
import com.fiit.dsa.interfaces.ITreePrinter;

import java.util.*;
import java.util.stream.Collectors;

public class Utils implements ITreePrinter
{

    public static boolean functionsContainsOne(List<LogicalFunction> functions)
    {
        for (LogicalFunction function : functions)
        {
            if (function.getFunction() == null)
                continue;

            if (function.getFunction().contains("1"))
                return true;
        }
        return false;
    }

    public static int hashCodeFunctions(List<LogicalFunction> functions)
    {
        if(functions == null)
            return 0;

        return displayFunctions(functions).hashCode();
    }

    public static void sortVariables(List<LogicalFunction> functions, String order)
    {

        for (LogicalFunction function : functions)
        {
            if (function.getFunction() == null)
                continue;

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < order.length(); i++)
            {
                char variable = order.charAt(i);
                String func = function.getFunction();

                int pos = func.indexOf(variable);

                if (pos == -1)
                    continue;

                if (pos == 0 || func.charAt(pos - 1) != '!')
                    stringBuilder.append(variable);

                else
                    stringBuilder.append(Utils.getNegativeVariable(variable));
            }
            function.setFunction(stringBuilder.toString());
        }

    }

    public static List<LogicalFunction> removeSameFunctions(List<LogicalFunction> functions)
    {
        if(functions.size() == 0)
            return null;

        HashSet<String> hashSet = new HashSet<>();
        List<LogicalFunction> result = new ArrayList<>();

        functions.forEach(function -> hashSet.add(function.getFunction()));
        hashSet.forEach(function -> result.add(new LogicalFunction(function)));

        return result;
    }


    public static String getNegativeVariable(char variable) { return "!" + variable; }
    public static String prepareFunction(String function) { return function.replaceAll("\\s", ""); }
    public static String displayFunctions(List<LogicalFunction> functions) { return functions.stream().map(LogicalFunction::getFunction).collect(Collectors.joining(" + ")); }
    public static List<LogicalFunction> getLogicalFunctions(String str) { return Collections.list(new StringTokenizer(str, "+")).stream().map(token -> new LogicalFunction((String) token)).collect(Collectors.toList()); }
}
