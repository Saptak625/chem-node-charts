package com.saptakdas.chemistry.chemicalequation.chemicalnodechart;

import com.saptakdas.chemistry.chemicalequation.chemicalnodechart.NodeGraph.Node;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

import static com.saptakdas.misc.Probability.getCombinations;

/**
 * This class can be used to create a graph of the balancing of a chemical equation.
 * @author Saptak Das
 */
public class ChemicalEquationNodeChart {
    public static void main(String[] args) {
        String reactantsString= input("Enter Reactants Side: ");
        String productsString = input("Enter Products Side: ");
        //Prepping equation
        String usePolyatomicReplacement= Character.toString(input("Use polyatomic substitution(T or F): ").toLowerCase().charAt(0));
        if(usePolyatomicReplacement.equals("t")){
            String[] replacementResults=polyatomicReplacement(reactantsString, productsString);
            reactantsString=replacementResults[0];
            productsString=replacementResults[1];
        }
        Hashtable reactants = parseString(reactantsString);
        Hashtable products = parseString(productsString);
        LinkedList reactantsElements = getElements(reactantsString);
        Collections.sort(reactantsElements);
        LinkedList productsElements = getElements(productsString);
        Collections.sort(productsElements);
        LinkedList elements;
        boolean contain;
        for (int i=0; i<reactantsElements.size(); i++) {
            contain=productsElements.contains(reactantsElements.get(i));
            if (!contain){
                System.out.println("Error: Same elements need to be on both sides of the equation.");
                System.exit(0);
            }
        }
        for (int i=0; i<productsElements.size(); i++) {
            contain=reactantsElements.contains(productsElements.get(i));
            if (!contain){
                System.out.println("Error: Same elements need to be on both sides of the equation.");
                System.exit(0);
            }
        }
        elements=reactantsElements;
        System.out.println(elements);
        System.out.println(reactants);
        System.out.println(products);
        //Finds Variable Space
        System.out.println("If you specify false, variable space will default to ideal.");
        String specifyVariableSpace= Character.toString(input("Specify Variable Space(T or F): ").toLowerCase().charAt(0));
        int variableSpace;
        if (specifyVariableSpace.equals("t")) {
            variableSpace = Integer.valueOf(input("Enter Variable Space: "));
        }
        else {
            variableSpace = idealVariableSpace(elements, reactants, products);
        }
        System.out.println(variableSpace);

        //Start of Node Chart Generation
        makeNodeChart(variableSpace,elements, reactants, products);
    }

    /**
     * For getting input
     * @param text Input information
     * @return String value of what was returned.
     */
    public static String input(String text) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(text);
        return scanner.nextLine();
    }

    /**
     * Replaces common polyatomic ions with their own custom element.
     * @param reactant_String String representation of reactant side
     * @param product_String String representation of product side
     * @return String[] Packaged version of new reactant and product strings
     */
    private static String[] polyatomicReplacement(String reactant_String, String product_String) {
        reactant_String = reactant_String.replace(" ", "");
        product_String = product_String.replace(" ", "");
        String[] replacementNames = {"A", "D", "E", "G", "J", "L", "M", "Q", "R", "X"};
        String[] commonIons = {"NH4", "C2H3O2", "HCO3", "HSO4", "ClO", "ClO3", "ClO2", "OCN", "CN", "H2PO4", "OH", "NO3", "NO2", "ClO4", "MnO4", "SCN", "CO3", "CrO4", "Cr2O7", "HPO4", "SO4", "SO3", "S2O3", "BO3", "PO4"};
        int index = 0;
        for (String ion : commonIons) {
            if (reactant_String.contains(ion) && product_String.contains(ion)) {
                reactant_String = reactant_String.replace(ion, replacementNames[index]);
                product_String = product_String.replace(ion, replacementNames[index]);
                index++;
            }
        }
        return new String[]{reactant_String, product_String};
    }

    /**
     * Gets all elements from an equation string
     * @param inputString String representation of each side of a chemical equation
     * @return LinkedList Gets a LinkedList of all the elements on the given side
     */
    static LinkedList getElements(String inputString){
        LinkedList<String> elements=new LinkedList<String>();
        String elementString="";
        char character = 0;
        for (int i=0; i<inputString.length(); i++){
            character=inputString.charAt(i);
            if (Character.isLetter(character)){
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))){
                    if (elementString!=""){
                        if (!elements.contains(elementString)){
                            elements.add(elementString);
                        }
                        elementString="";
                    }
                }
                elementString = elementString.concat(Character.toString(character));
            }
            else if (Character.toString(character).equals("+")){
                if (!elements.contains(elementString)){
                    elements.add(elementString);
                }
                elementString="";
            }
        }
        if (!Character.toString(character).equals("")){
            if (!elements.contains(elementString)) {
                elements.add(elementString);
            }
        }
        return elements;
    }

    /**
     * Parses compound to give all elements in compound
     * @param inputString String representation of a compound
     * @return Hashtable Representation of compound as Hashtable
     */
    private static Hashtable parseCompound(String inputString) {
        Hashtable<String, Integer> dictionary = new Hashtable<String, Integer>();
        String symbol = "";
        String numString = "";
        for (int i = 0; i < inputString.length(); i++) {
            char character = inputString.charAt(i);
            //System.out.println(i);
            //System.out.println(character);
            //System.out.println("");
            if (Character.isLetter(character)) {
                //Checks that this is a letter
                if (String.valueOf(character).toUpperCase().equals(String.valueOf(character))){
                    //This is uppercase
                    if (symbol!=""){
                        //Symbol is filled and needs to be dumped
                        try{
                            dictionary.put(symbol, Integer.valueOf(numString));
                        }catch (NumberFormatException exception) {
                            dictionary.put(symbol, 1);
                        }
                        symbol="";
                        numString="";
                    }
                    symbol = symbol.concat(String.valueOf(character));
                }
                else{
                    symbol = symbol.concat(String.valueOf(character));
                }
            } else if (Character.isDigit(character)) {
                //This is a number
                numString = numString.concat(String.valueOf(character));
            }
            //System.out.println(i);
            //System.out.println("Character: "+character);
            //System.out.println(symbol);
            //System.out.println(numString);
            //System.out.println(dictionary);
            //System.out.println("--Spacer--");
        }
        if (numString.equals("")){
            numString="1";
        }
        dictionary.put(symbol, Integer.valueOf(numString));
        return dictionary;
    }

    /**
     * Parses whole equation string.
     * @param inputString String representation of each side
     * @return Hashtable Hashtable representation of given side
     */
    static Hashtable parseString(String inputString){
        Hashtable<Integer, Hashtable> compoundTable=new Hashtable<Integer, Hashtable>();
        String storeString = "";
        Integer index=0;
        for (int j=0; j<inputString.length(); j++) {
            if (Character.toString(inputString.charAt(j)).equals("+")) {
                compoundTable.put(index, parseCompound(storeString));
                storeString="";
                index=index+1;

            }
            else {
                storeString=storeString.concat(Character.toString(inputString.charAt(j)));
            }
        }
        compoundTable.put(index, parseCompound(storeString));
        return compoundTable;
    }

    /**
     * Main Logic to get combinations and show result in graph picture.
     * @param variableSpace The number of variables that will be used to solve
     * @param elements LinkedList of all elements
     * @param reactants Hashtable representation of reactant side
     * @param products Hashtable representation of product side
     */
    private static void makeNodeChart(int variableSpace, LinkedList elements, Hashtable reactants, Hashtable products) {
        //Get all possible start nodes
        LinkedList<String> startNodes=new LinkedList<>();
        int reactantCounter=0;
        int productCounter=0;
        for (int i=0; i<elements.size(); i++){
            reactantCounter=0;
            productCounter=0;
            String element= (String) elements.get(i);
            for (int j=0; j<reactants.size(); j++){
                Hashtable compound=(Hashtable) reactants.get(j);
                if (compound.containsKey(element)){
                    reactantCounter+=1;
                }
            }
            for (int j=0; j<products.size(); j++){
                Hashtable compound=(Hashtable) products.get(j);
                if (compound.containsKey(element)){
                    productCounter+=1;
                }
            }
            if (reactantCounter==1 && productCounter==1){
                startNodes.add(element);
            }
        }
        //Generating Origin Nodes based on Variable Space
        Node.addEquation(reactants, products);
        DefaultDirectedGraph<Node, DefaultEdge> graph = new DefaultDirectedGraph(DefaultEdge.class);
        Node newNode = null;
        //Note nodeTable contains only parent and child nodes
        Hashtable<Integer,LinkedList<Node>> nodeTable=new Hashtable();
        var nodeLinkedlist=new LinkedList();
        if (variableSpace==1){
            //Origin Nodes are just Root Nodes
            for (String element:startNodes){
                newNode=new Node("origin", filledHashtable("Elements Used", filledLinkedlist(element)));
                nodeLinkedlist.addLast(newNode);
            }
        }
        else {
            //Get all combinations
            var combinations=getCombinations(startNodes,variableSpace);
            for (Object obj :combinations){
                LinkedList elementsList=(LinkedList) obj;
                //Trimming number of combinations
                if (checkCombination(elementsList,reactants,products)) {
                    newNode = new Node("origin", filledHashtable("Elements Used", elementsList));
                    nodeLinkedlist.addLast(newNode);
                }
            }
        }
        nodeTable.put(1,nodeLinkedlist);

        //Child Nodes and Connections
        var layer = 2;
        while (layer<=elements.size()+2-variableSpace){
            var possibleElements=new Hashtable<String, LinkedList>();
            LinkedList lastNodeLayer=nodeTable.get(layer-1);
            for (Object n:lastNodeLayer){
                //Get all needed nodes
                Node node=(Node) n;
                var nodeList=new LinkedList();
                if ((!node.reactantBool.contains(false) && !node.productBool.contains(false))) {
                    node.leaves.addLast("success");
                    System.out.println("Is this getting executed?"+node.leaves);
                    continue;
                }

                nodeList.addLast(node);
                System.out.println("Elements: "+elements);
                LinkedList<String> elementsRemaining=removeElements(elements,node.elementsUsed);
                System.out.println("Node: "+node);
                System.out.println(elementsRemaining);
                System.out.println("---------------------------------------------------------------------------------------------->"+elementsRemaining);
                System.out.println("Already Used: "+node.elementsUsed);
                for (String element: elementsRemaining){
                    System.out.println("Element: "+element);
                    reactantCounter=0;
                    productCounter=0;
                    var reactantFilledCounter=0;
                    var productFilledCounter=0;
                    var reactantUnfilledCounter=0;
                    var productUnfilledCounter=0;
                    var reactantIndexes=new LinkedList();
                    var productIndexes=new LinkedList();
                    Integer unfilledIndex=null;
                    Boolean reactantUnfilled=true;
                    for (int i=0; i<reactants.size(); i++){
                        Hashtable compound=(Hashtable) reactants.get(i);
                        if (compound.containsKey(element)){
                            reactantCounter++;
                            if ((Boolean) node.reactantBool.get(i)){
                                //Value is filled
                                reactantIndexes.addLast(i);
                                reactantFilledCounter++;
                            }
                            else{
                                unfilledIndex=i;
                                reactantUnfilledCounter++;
                            }
                        }
                    }
                    for (int i=0; i<products.size(); i++){
                        Hashtable compound=(Hashtable) products.get(i);
                        if (compound.containsKey(element)){
                            productCounter++;
                            if ((Boolean) node.productBool.get(i)){
                                //Value is filled
                                productIndexes.addLast(i);
                                productFilledCounter++;
                            }
                            else{
                                unfilledIndex=i;
                                reactantUnfilled=false;
                                productUnfilledCounter++;
                            }
                        }
                    }
                    System.out.println("<------------------------------------------------------------------------------------------------------------------------------------>");
                    System.out.println("Element: "+element);
                    System.out.println("Node Elements Used: "+ node.elementsUsed);
                    System.out.println("Boolean Charts: "+node.reactantBool+" and "+ node.productBool);
                    System.out.println("Counters: "+reactantCounter+" and "+productCounter);
                    System.out.println("Unfilled Counters: "+reactantUnfilledCounter+" and "+productUnfilledCounter);
                    System.out.println("Filled Counter: "+reactantFilledCounter+" and "+productFilledCounter);
                    System.out.println("<------------------------------------------------------------------------------------------------------------------------------------>");
                    if ((reactantCounter==1 && productCounter==1) && (reactantUnfilledCounter==1 ^ productUnfilledCounter==1) && (reactantFilledCounter==1 ^ productFilledCounter==1)){
                        //One to one semi-established relationship
                        System.out.println("Simple relationship");
                        if (!possibleElements.containsKey(element)) {
                            possibleElements.put(element, nodeList);
                            System.out.println("-------------------Putting element " + element + " and node " + node);
                            System.out.println("Possible Elements: "+possibleElements);
                        }
                        else if (possibleElements.containsKey(element) && !possibleElements.get(element).contains(node)){
                            System.out.println("-------------------Putting element "+element+" and node "+node);
                            var oldValue=copyLinkedlist(possibleElements.get(element));
                            oldValue.addLast(node);
                            possibleElements.remove(element);
                            possibleElements.put(element, oldValue);
                            System.out.println("Possible Elements: "+possibleElements);
                        }
                        //Add this as a child node path for current node
                        LinkedList childElements=copyLinkedlist(node.elementsUsed);
                        childElements.addLast(element);
                        node.children.addLast(childElements);
                    }
                    else if((reactantCounter+productCounter-1==reactantFilledCounter+productFilledCounter) && (reactantUnfilledCounter==1 ^ productUnfilledCounter==1)){
                        //This is a complex relationship
                        System.out.println("Complex relationship");
                        if (!possibleElements.containsKey(element)) {
                            possibleElements.put(element, nodeList);
                            System.out.println("-------------------Putting element " + element + " and node " + node);
                            System.out.println("Possible Elements: "+possibleElements);
                        }
                        else if (possibleElements.containsKey(element) && !possibleElements.get(element).contains(node)){
                            System.out.println("-------------------Putting element "+element+" and node "+node);
                            var oldValue=copyLinkedlist(possibleElements.get(element));
                            oldValue.addLast(node);
                            possibleElements.remove(element);
                            possibleElements.put(element, oldValue);
                            System.out.println("Possible Elements: "+possibleElements);
                        }
                        //Add this as a child node path for current node
                        LinkedList childElements=copyLinkedlist(node.elementsUsed);
                        childElements.addLast(element);
                        node.children.addLast(childElements);
                    }
                    else {
                        //Remove this afterwards
                        System.out.println("-----------------------------Nothing was added.");
                    }
                }
                if (possibleElements==new Hashtable()){
                    //this is a deadend leaf node
                    node.leaves.addLast("deadend");
                }
            }
            //Process all possible elements into nodes
            System.out.println("Possible Elements: "+possibleElements);
            if (possibleElements!=new Hashtable()){
                //You will create at least one child node using possible elements.
                //Work on first adding all child nodes
                LinkedList<Node> nodeContainer=new LinkedList();
                var allKeys=possibleElements.keys();
                while (allKeys.hasMoreElements()) {
                    String key=allKeys.nextElement();
                    var correspondingLinkedlist=(LinkedList<Node>) possibleElements.get(key);
                    for (Node node: correspondingLinkedlist) {
                        LinkedList<String> elementsList=copyLinkedlist(node.elementsUsed);
                        if (!elementsList.contains(key)) {
                            elementsList.addLast(key);
                        }
                        else
                            continue;
                        if (!checkForCopies(elementsList,elements)) {
                            Boolean checkBoolean = false;
                            //Check if node combination exists already
                            if (nodeContainer != new LinkedList()) {
                                for (Node checkNode : nodeContainer) {
                                    checkBoolean = checkLinkedlistEquality(checkNode.elementsUsed, elementsList);
                                    if (checkBoolean)
                                        break;
                                }
                            }
                            //Else no checking needed
                            //Skip this combination
                            if (checkBoolean) {
                                continue;
                            }
                            //Create new child node
                            Hashtable information = new Hashtable();
                            information.put("Elements Used", elementsList);
                            information.put("Parent", node);
                            System.out.println("Elements List: "+ elementsList);
                            Node newChildNode = new Node("child", information);
                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Boolean Charts: "+newChildNode.reactantBool+" and "+newChildNode.productBool);
                            nodeContainer.addLast(newChildNode);
                        }
                    }
                }
                if (nodeContainer!=new LinkedList())
                    nodeTable.put(layer, nodeContainer);
            }
            layer++;
        }

        System.out.println("This is the node table"+nodeTable);
        //Create all nodes and edges
        //Establish all edges
        System.out.println("---------------------------------------------This is for establishing the edges------------------------------------------------");
        for (int i=1; i<nodeTable.size()+1; i++){
            LinkedList<Node> layerNodes=nodeTable.get(i);
            for (Node node:layerNodes){
                LinkedList<String> parentUsedElements=node.elementsUsed;
                for (Node possibleChildNodes: nodeTable.get(i+1)){
                    LinkedList<String> childUsedElement=possibleChildNodes.elementsUsed;
                    var contains=true;
                    System.out.println("Possible Parent: "+parentUsedElements);
                    System.out.println("Possible Child: "+ childUsedElement);
                    for (String element: parentUsedElements){
                        contains=childUsedElement.contains(element);
                        if (!contains) break;
                    }
                    if (!contains) continue;
                    else{
                        if (!node.children.contains(childUsedElement)) {
                            node.children.addLast(childUsedElement);
                            System.out.println("Parent established edge with child!!!");
                        }
                    }
                }
            }
        }
        System.out.println("-----------------------------------------------------------------end-----------------------------------------------------------");
        //Create all vertices
        for (int i=1; i<nodeTable.size()+1; i++){
            LinkedList<Node> layerNodes=nodeTable.get(i);
            for (Node node:layerNodes){
                System.out.println(node);
                graph.addVertex(node);
            }
        }
        //Add all leaf nodes and leaf edges
        for (int i=1; i<nodeTable.size()+1; i++){
            LinkedList<Node> layerNodes=nodeTable.get(i);
            for (Node node:layerNodes){
                System.out.println(node);
                for (Object msg: node.leaves) {
                    String message=(String) msg;
                    Hashtable information=new Hashtable();
                    information.put("Message", message);
                    Node newLeafNode=new Node("leaf", information);
                    graph.addVertex(newLeafNode);
                    graph.addEdge(node, newLeafNode);
                }
            }
        }
        //Add all other edges
        for (int i=1; i<nodeTable.size()+1; i++){
            LinkedList<Node> layerNodes=nodeTable.get(i);
            for (Node node:layerNodes) {
                if (node.children != new LinkedList()) {
                    for (Object c : node.children) {
                        var child = (LinkedList) c;
                        LinkedList<Node> nextLayer = nodeTable.get(i + 1);
                        for (Node possibleChildNode:nextLayer){
                            if(checkLinkedlistEquality(possibleChildNode.elementsUsed, child)){
                                graph.addEdge(node,possibleChildNode);
                            }
                        }
                    }
                }
            }
        }
        //Display
        NodeGraph.displayGraph(graph);
    }

    /**
     * @param elementsUsed LinkedList of elements used
     * @param elements LinkedList of the current remaining elements
     * @return boolean True if there are no copies of Strings in LinkedList elementsUsed that are in LinkedList elements
     */
    private static boolean checkForCopies(LinkedList<String> elementsUsed, LinkedList<String> elements){
        for (String element: elements){
            var counter=1;
            for (String element2: elements){
                if (element.equals(element2))
                    counter++;
                if (counter>1){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param a LinkedList
     * @param b LinkedList
     * @return boolean True if all elements in a are in b
     */
    private static boolean checkLinkedlistEquality(LinkedList<String> a, LinkedList<String> b){
        for (String objA: a){
            if (!b.contains(objA))
                return false;
        }
        return true;
    }

    /**
     * Removes used elements.
     * @param allElements All remaining elements
     * @param elements Elements used
     * @return LinkedList Remaining unused elements
     */
    private static LinkedList removeElements(LinkedList<String> allElements, LinkedList<String> elements){
        var elementsRemaining=new LinkedList<String>();
        for (String element: allElements)
            elementsRemaining.addLast(element);
        for (String e:elements){
            elementsRemaining.remove(e);
        }
        return elementsRemaining;
    }

    /**
     * Check current combination.
     * @param elementsList Current list of elements
     * @param reactants Hashtable representation of reactants
     * @param products Hashtable representation of products
     * @return boolean True if combination is valid.
     */
    private static boolean checkCombination(LinkedList elementsList, Hashtable reactants, Hashtable products){
        var reactantBoolean=makeCoefficientBooleans(reactants);
        var productBoolean=makeCoefficientBooleans(products);
        Integer reactantIndex=null;
        Integer productIndex=null;
        for (Object e: elementsList) {
            String element=(String) e;
            reactantIndex=null;
            productIndex=null;
            for (int i=0; i<reactants.size(); i++){
                Hashtable compound= (Hashtable) reactants.get(i);
                if (compound.containsKey(element))
                    reactantIndex=i;
            }
            for (int i=0; i<products.size(); i++){
                Hashtable compound= (Hashtable) products.get(i);
                if (compound.containsKey(element))
                    productIndex=i;
            }
            if (reactantIndex!=null && productIndex!=null){
                if (reactantBoolean.get(reactantIndex)!=true && productBoolean.get(productIndex)!=true){
                    reactantBoolean.set(reactantIndex,true);
                    productBoolean.set(productIndex, true);
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param element Element to add in new LinkedList
     * @return LinkedList that is filled with element
     */
    private static LinkedList filledLinkedlist(String element) {
        LinkedList<String> list=new LinkedList();
        list.add(element);
        return list;
    }

    /**
     * @param toBeCopied LinkedList to be copied
     * @return Copied LinkedList
     */
    public static LinkedList copyLinkedlist(LinkedList toBeCopied){
        var linkedlist=new LinkedList();
        for (Object object: toBeCopied)
            linkedlist.addLast(object);
        return linkedlist;
    }

    private static Hashtable<String, LinkedList> filledHashtable(String key, LinkedList value){
        Hashtable hashtable=new Hashtable();
        hashtable.put(key, value);
        return hashtable;
    }

    /**
     * Calculates ideal variable space needed to solve equation.
     * @param elements LinkedList of all elements
     * @param reactants Hashtable representation of reactants
     * @param products Hashtable representation of products
     * @return int value of number of variables needed
     */
    private static int idealVariableSpace(LinkedList elements, Hashtable reactants, Hashtable products){
        int mainCounter=0;
        int reactantCounter=0;
        int productCounter=0;
        LinkedList<Boolean> reactantBooleans=makeCoefficientBooleans(reactants);
        LinkedList<Boolean> productBooleans=makeCoefficientBooleans(products);
        Integer reactantIndex=null;
        Integer productIndex=null;
        for (int i=0; i<elements.size(); i++){
            reactantCounter=0;
            productCounter=0;
            reactantIndex=null;
            productIndex=null;
            String element= (String) elements.get(i);
            for (int j=0; j<reactants.size(); j++){
                Hashtable compound=(Hashtable) reactants.get(j);
                if (compound.containsKey(element)){
                    reactantCounter+=1;
                    reactantIndex=j;
                }
            }
            for (int j=0; j<products.size(); j++){
                Hashtable compound=(Hashtable) products.get(j);
                if (compound.containsKey(element)){
                    productCounter+=1;
                    productIndex=j;
                }
            }
            if ((reactantCounter==1 && productCounter==1) && (!(reactantBooleans.get(reactantIndex)) && !(productBooleans.get(productIndex)))){
                mainCounter+=1;
                reactantBooleans.set(reactantIndex,true);
                productBooleans.set(productIndex,true);
            }
        }
        return mainCounter;
    }

    /**
     * @param side Hashtable representing equation side
     * @return LinkedList of size of Hashtable size filled with booleans representing each compound
     */
    public static LinkedList<Boolean> makeCoefficientBooleans(Hashtable side){
        LinkedList<Boolean> booleanList=new LinkedList<Boolean>();
        for (int i=0; i<side.size(); i++){
            booleanList.add(false);
        }
        return booleanList;
    }
}
