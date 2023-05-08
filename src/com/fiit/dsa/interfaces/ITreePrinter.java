package com.fiit.dsa.interfaces;


public interface ITreePrinter
{
    static void printTree(INode root, String indent, boolean side)
    {
        if(root == null)
            return;

        System.out.print(indent);

        if (side)
        {
            System.out.print("R----");
            indent += "   ";
        }
        else
        {
            System.out.print("L----");
            indent += "|  ";
        }

        System.out.println(root);
        printTree(root.getLeft(), indent, false);
        printTree(root.getRight(), indent, true);
    }
}
