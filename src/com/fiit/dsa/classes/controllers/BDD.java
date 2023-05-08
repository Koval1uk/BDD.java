package com.fiit.dsa.classes.controllers;

import com.fiit.dsa.classes.exceptions.InvalidBDDArguments;
import com.fiit.dsa.classes.models.BDDNode;
import com.fiit.dsa.classes.models.LogicalFunction;
import com.fiit.dsa.classes.utils.Utils;
import com.fiit.dsa.interfaces.IBDD;

import java.util.*;

public class BDD implements IBDD
{
    private String order;
    private BDDNode root;
    private BDDStats stats;
    private final BDDNode TERMINAL_TRUE;
    private final BDDNode TERMINAL_FALSE;
    private final HashMap<Integer, BDDNode> reducedFunctions;


    private class BDDStats
    {
        int nodes;

        public int getNodesWithoutReduction() { return (int)Math.pow(2, order.length() + 1) - 1; }
    }


    public static BDD BDD_create_with_best_order (String function, String order) throws InvalidBDDArguments
    {

        BDD bdd = new BDD();
        String bestOrder = order;

        bdd.bdd_create(function, bestOrder);

        int bestOrderSize = bdd.getReducedFunctions().size();

        for (int nx = 0; nx < 1_000; nx++)
        {
            Random random = new Random();
            char[] charArray = order.toCharArray();

            for (int i = charArray.length - 1; i > 0; i--)
            {
                int j = random.nextInt(i + 1);
                char temp = charArray[i];
                charArray[i] = charArray[j];
                charArray[j] = temp;
            }

            String currentOrder = new String(charArray);
            bdd = new BDD();
            bdd.bdd_create(function, currentOrder);

            if(bdd.getReducedFunctions().size() <= bestOrderSize)
            {
                bestOrder = currentOrder;
                bestOrderSize = bdd.getReducedFunctions().size();
            }
        }

        bdd = new BDD();
        bdd.bdd_create(function, bestOrder);
        return bdd;
    }

    public BDD()
    {
        root = null;
        stats = new BDDStats();
        reducedFunctions = new HashMap<>();
        TERMINAL_TRUE = new BDDNode("1");
        TERMINAL_FALSE = new BDDNode("0");
    }

    public HashMap<Integer, BDDNode> getReducedFunctions() { return reducedFunctions; }

    private void validateInputs(String function, String order) throws InvalidBDDArguments
    {
        if(function == null || order == null)
            throw new InvalidBDDArguments("Function or order is null");
    }

    private BDDNode bddCreate(List<LogicalFunction> logicalFunctions, String order)
    {
        List<LogicalFunction> cpyLogicalFunctions = new ArrayList<>(logicalFunctions);
        logicalFunctions = Utils.removeSameFunctions(logicalFunctions);

        if (reducedFunctions.get(Utils.hashCodeFunctions(cpyLogicalFunctions)) != null)
            return reducedFunctions.get(Utils.hashCodeFunctions(cpyLogicalFunctions));


        assert logicalFunctions != null;
        if(Utils.functionsContainsOne(logicalFunctions))
            return TERMINAL_TRUE;


        char parentVariable = order.charAt(0);


        List<LogicalFunction> leftFunctions = new ArrayList<>();
        List<LogicalFunction> rightFunctions = new ArrayList<>();

        for(LogicalFunction logicalFunction : logicalFunctions)
        {
            if(logicalFunction.getFunction() == null)
                continue;

            int result = logicalFunction.getVariableState(parentVariable);
            switch (result)
            {
                case -1 ->
                {
                    leftFunctions.add(logicalFunction);
                    rightFunctions.add(logicalFunction);
                }
                case 0 ->
                {
                    leftFunctions.add(logicalFunction);
                    logicalFunction.removeVariable(Utils.getNegativeVariable(parentVariable));
                }
                case 1 ->
                {
                    rightFunctions.add(logicalFunction);
                    logicalFunction.removeVariable(String.valueOf(parentVariable));
                }
                default -> throw new IllegalStateException("Unexpected value: " + result);
            }
        }


        BDDNode node;
        BDDNode lNode;
        BDDNode rNode;

        if (Utils.functionsContainsOne(leftFunctions))
            lNode = TERMINAL_TRUE;

        else if (leftFunctions.size() == 0)
            lNode = TERMINAL_FALSE;

        else
            lNode = bddCreate(leftFunctions, order.substring(1));

        if (Utils.functionsContainsOne(rightFunctions))
            rNode = TERMINAL_TRUE;
        else if (rightFunctions.size() == 0)
            rNode = TERMINAL_FALSE;
        else
            rNode = bddCreate(rightFunctions, order.substring(1));


        if (lNode == rNode)
            node = lNode;

        else
        {
            stats.nodes++;
            node = new BDDNode(Utils.displayFunctions(cpyLogicalFunctions));
            node.setLeft(lNode);
            node.setRight(rNode);
        }
        reducedFunctions.put(Utils.hashCodeFunctions(cpyLogicalFunctions), node);
        return node;
    }

    @Override
    public void bdd_create(String function, String order) throws InvalidBDDArguments
    {
        this.order = String.join("", new HashSet<>(Collections.singletonList(order)));

        reducedFunctions.clear();
        validateInputs(function, this.order);

        List<LogicalFunction> logicalFunctions = Utils.getLogicalFunctions(Utils.prepareFunction(function));
        logicalFunctions.forEach(LogicalFunction::optimize);
        Utils.sortVariables(logicalFunctions, order);

        root = bddCreate(logicalFunctions, order);
    }

    @Override
    public boolean bdd_use(String number) { return getBDDResult(root, number, 0); }

    private boolean getBDDResult(BDDNode root, String number, int pos)
    {
        if(root == TERMINAL_TRUE)
            return true;

        if(root == TERMINAL_FALSE)
            return false;


        if(root.getFunction().contains(String.valueOf(order.charAt(pos))))
            if(number.charAt(pos) == '0')
                return getBDDResult((BDDNode) root.getLeft(), number, pos + 1);
            else
                return getBDDResult((BDDNode) root.getRight(), number, pos + 1);
        else
            return getBDDResult(root, number, pos + 1);
    }
    public BDDNode getRoot() { return root; }
    public String getOrder() { return order; }
    public int getNodeStat() { return stats.nodes; }
    public int getNodeStatWithoutReduction() { return stats.getNodesWithoutReduction(); }
    public double getStatReductionRate() { return (1.0 - (double)getNodeStat() / (double)getNodeStatWithoutReduction()) * 100.0; }

}
