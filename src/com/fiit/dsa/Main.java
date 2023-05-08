package com.fiit.dsa;

import com.fiit.dsa.classes.controllers.BDDTester;
import com.fiit.dsa.classes.exceptions.InvalidBDDArguments;

import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            for(int i = 3; i < 27; i++)
                new BDDTester(i);
        }
        catch (InvalidBDDArguments | IOException exception) { exception.printStackTrace(); }
    }
}
