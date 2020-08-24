package com.saptakdas.chemistry.chemicalequation.chemicalnodechart;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * This class can be used to represent node graph structure.
 * @author Saptak Das
 */
public class NodeGraph{
    /**
     * Inner Class to represent nodes in graph
     */
    public static class Node {
        private static Hashtable reactants;
        private static Hashtable products;
        LinkedList elementsUsed=new LinkedList();
        String type;
        int layer;
        LinkedList children;
        LinkedList leaves;
        LinkedList reactantBool;
        LinkedList productBool;
        private String message;

        /**
         * @param type Type of node
         * @param otherArgs Passes any other necessary information to create node
         */
        public Node(String type, Hashtable otherArgs){
            System.out.println(type);
            System.out.println(otherArgs);
            if (type.equals("origin")){
                this.type="origin";
                this.layer=1;
                this.children=new LinkedList();
                this.leaves=new LinkedList();
                this.reactantBool= ChemicalEquationNodeChart.makeCoefficientBooleans(reactants);
                this.productBool=ChemicalEquationNodeChart.makeCoefficientBooleans(products);
                changeCoefficentBoolean(reactantBool,productBool, (LinkedList) otherArgs.get("Elements Used"));
                this.elementsUsed=(LinkedList) otherArgs.get("Elements Used");
            }
            else if (type.equals("child")){
                var parentInfo=((Node) otherArgs.get("Parent"));
                this.type="child";
                this.layer=parentInfo.layer+1;
                this.children=new LinkedList();
                this.leaves=new LinkedList();
                LinkedList<Boolean> parentReactantBool=parentInfo.reactantBool;
                LinkedList<Boolean> parentProductBool=parentInfo.productBool;
                changeCoefficentBoolean(parentReactantBool,parentProductBool, (LinkedList) otherArgs.get("Elements Used"));
                this.elementsUsed=(LinkedList) otherArgs.get("Elements Used");
            }
            else{
                //This is a leaf node
                this.type="leaf";
                this.message= (String) otherArgs.get("Message");
            }
        }
        @Override
        public String toString() {
            if (type.equals("leaf"))
                return message;
            else
                return String.valueOf(elementsUsed);
        }

        /**
         * Provides reactant and product Hashtable representations
         * @param reactantsHashtable Hashtable representation of reactant side
         * @param productsHashtable Hashtable representation of product side
         */
        public static void addEquation(Hashtable reactantsHashtable, Hashtable productsHashtable){
            reactants=reactantsHashtable;
            products=productsHashtable;
        }

        private static Hashtable getReactants() {
            return reactants;
        }

        private static Hashtable getProducts() {
            return products;
        }

        /**
         * Changes coefficient boolean representing filled or unfilled variable.
         * @param oldReactant Current Reactant Boolean Coefficient LinkedList
         * @param oldProduct Current Product Boolean Coefficient LinkedList
         * @param elements Remaining unused elements
         */
        private void changeCoefficentBoolean(LinkedList oldReactant, LinkedList oldProduct, LinkedList elements){
            LinkedList oldReactantBool=ChemicalEquationNodeChart.copyLinkedlist(oldReactant);
            LinkedList oldProductBool=ChemicalEquationNodeChart.copyLinkedlist(oldProduct);
            for (Object e:elements){
                var element=(String) e;
                LinkedList<Integer> reactantIndex=new LinkedList();
                LinkedList<Integer> productIndex=new LinkedList();
                for (int i=0; i<getReactants().size(); i++){
                    Hashtable compound=(Hashtable) getReactants().get(i);
                    if (compound.containsKey(element)) reactantIndex.addLast(i);
                }
                for (int i=0; i<getProducts().size(); i++){
                    Hashtable compound=(Hashtable) getProducts().get(i);
                    if (compound.containsKey(element)) productIndex.addLast(i);
                }
                //Changing Indexes
                for (Integer rIndex: reactantIndex)
                    oldReactantBool.set(rIndex,true);
                for (Integer pIndex: productIndex)
                oldProductBool.set(pIndex,true);
            }
            reactantBool=oldReactantBool;
            productBool=oldProductBool;
        }
    }
    public static void main(String[] args) throws IOException {
        DefaultDirectedGraph<Node, DefaultEdge> g = new DefaultDirectedGraph(DefaultEdge.class);
        Hashtable hashtable1=new Hashtable();
        Hashtable hashtable2=new Hashtable();
        LinkedList linkedList1=new LinkedList();
        LinkedList linkedList2=new LinkedList();
        linkedList2.add("O");
        hashtable2.put("Elements Used",linkedList2);
        linkedList1.add("Mn");
        hashtable1.put("Elements Used",linkedList1);
        Node x1= new Node("origin", hashtable1);
        Node x2= new Node("origin", hashtable2);
        Node x3 = new Node("origin", createHashtable("H"));
        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);
        g.addEdge(x1, x2);
        g.addEdge(x3, x2);
        displayGraph(g);
    }

    /**
     * @param element String element
     * @return New Hashtable filled with LinkedList that has an element
     */
    private static Hashtable createHashtable(String element){
        Hashtable hashtable=new Hashtable();
        LinkedList linkedList=new LinkedList();
        linkedList.add(element);
        hashtable.put("Elements Used",linkedList);
        return hashtable;
    }

    /**
     * Display .png file for node graph
     * @param graphName DefaultDirectedGraph variable
     */
    public static void displayGraph(DefaultDirectedGraph graphName) {
        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<String, DefaultEdge>(graphName);
        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());
        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("src/GraphPics/graph.png");

        try {
            imgFile.createNewFile();
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
