package com.fiit.dsa.interfaces;

import com.fiit.dsa.classes.exceptions.InvalidBDDArguments;

public interface IBDD
{
    void bdd_create(String function, String order) throws InvalidBDDArguments;

    boolean bdd_use(String number);
}
