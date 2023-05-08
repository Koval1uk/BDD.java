package com.fiit.dsa.classes.models;

import com.fiit.dsa.interfaces.INode;

public class BDDNode implements INode
{
    private INode left;
    private INode right;
    public String function;

    public BDDNode(String function) { this.function = function; }
    @Override
    public INode getLeft() { return left; }
    @Override
    public INode getRight() { return right; }
    public String getFunction() { return function; }
    @Override
    public void setLeft(INode left) { this.left = left; }
    @Override
    public void setRight(INode right) { this.right = right; }

    @Override
    public int hashCode() { return function.hashCode(); }

    @Override
    public boolean equals( Object obj ) { return obj.hashCode() == this.hashCode(); }

    @Override
    public String toString() { return function + "\t@[" + System.identityHashCode(function) + "]"; }
}
