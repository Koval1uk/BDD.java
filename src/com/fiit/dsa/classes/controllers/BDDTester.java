package com.fiit.dsa.classes.controllers;

import com.fiit.dsa.classes.exceptions.InvalidBDDArguments;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BDDTester
{
    private String order = "";
    private int averageNodesNum;
    private double averageUseTime;
    private double averageBestTime;
    private double averageBestUsage;
    private double averageCreateTime;
    private double averageMemoryUsage;
    private double averageReductionRate;
    private int averageNodesNumWithoutReduction;

    private final char[] variables = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public BDDTester(int variablesNum) throws IOException, InvalidBDDArguments
    {
        String line;
        BufferedReader bufferedReader;

        for(int i = 0; i < variablesNum; i++)
            order += variables[i];

        System.out.printf("Testing %d variables\n", variablesNum);

        bufferedReader = new BufferedReader(new FileReader(String.format("dataset/%d.txt", variablesNum)));

        while ((line = bufferedReader.readLine()) != null)
            testBooleanFunction(line);

        System.out.printf("Number of variables: %d\n", variablesNum);
        System.out.printf("Reduction: %f%%\n", averageReductionRate / 1000.0);
        System.out.printf("BDD_use time taken: %fms\n", averageUseTime / 1000.0);
        System.out.printf("BDD_create time taken: %fms\n", averageCreateTime / 1000.0);
        System.out.printf("BDD_create memory usage: %f MB\n", averageMemoryUsage / 1000.0);
        System.out.printf("BDD_create_with_best_order time taken: %fms\n", averageBestTime / 1000.0);
        System.out.printf("BDD_create_with_best_order memory usage: %f MB\n", averageBestUsage / 1000.0);


        System.out.printf("Number of nodes: %d\n", averageNodesNum / 1000);
        System.out.printf("Number of nodes without reduction: %d\n", averageNodesNumWithoutReduction / 1000);

        System.out.println();
    }

    private void testBooleanFunction(String boolFunc) throws InvalidBDDArguments
    {
        long start, end;
        long createTime, testTime, createTime1;
        long memoryBefore, memoryAfter, memoryAfter1;

        int length = order.length();
        int[] boolArgs = new int[length];

        BDD bdd;
        Runtime runtime;
        StringBuilder boolArg;

        testTime = 0;

        bdd = new BDD();
        runtime = Runtime.getRuntime();


        System.gc();
        memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        start = System.nanoTime();
        bdd.bdd_create(boolFunc, order);
        createTime = System.nanoTime() - start;

        memoryAfter = (runtime.totalMemory() - runtime.freeMemory()-memoryBefore);
// bdd_best_order

        System.gc();
        memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        start = System.nanoTime();
        BDD.BDD_create_with_best_order(boolFunc, order);
        createTime1 = System.nanoTime() - start;

        memoryAfter1 = (runtime.totalMemory() - runtime.freeMemory()-memoryBefore);

        while (true)
        {
            boolean exit = true;
            boolArg = new StringBuilder();

            for(int i = 0; i < length; i++)
                boolArg.append(boolArgs[i]);

            start = System.nanoTime();
            bdd.bdd_use(boolArg.toString());
            end = System.nanoTime() - start;
            testTime += end;


            for(int i = 0; i < length; i++)
            {
                if(boolArgs[i] == 1)
                    continue;

                exit = false;
            }

            if(exit)
                break;


            for(int i = length - 1; i != -1; i--)
            {
                if(++boolArgs[i] > 1)
                    boolArgs[i] = 0;
                else
                    break;
            }
        }

        averageNodesNum += bdd.getNodeStat();
        averageReductionRate += bdd.getStatReductionRate();
        averageMemoryUsage += (double)memoryAfter / 1024.0F / 1024.0F;
        averageUseTime += TimeUnit.NANOSECONDS.toMillis(testTime) / 1000.0F;
        averageNodesNumWithoutReduction += bdd.getNodeStatWithoutReduction();
        averageCreateTime += TimeUnit.NANOSECONDS.toMillis(createTime) / 1000.0F;
        averageBestTime += TimeUnit.NANOSECONDS.toMillis(createTime1) / 1000.0F;
        averageBestUsage += (double)memoryAfter1 / 1024.0F / 1024.0F;
    }
}
