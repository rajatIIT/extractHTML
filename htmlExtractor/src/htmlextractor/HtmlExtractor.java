/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package htmlextractor;

import au.com.bytecode.opencsv.CSVReader;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Rajat-HP
 *
 *
 * A simple utility to extract location data from HTML web pages.
 *
 */
public class HtmlExtractor {

    static HashMap finalResult = new HashMap(), cityData = new HashMap();

    static String currentpath = "", directory = "C:/Users/Rajat/Google Drive/part time/escort",citycsvPath="";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the address of the html Directory: ");
//get user input for a
        String myString = reader.nextLine();
        directory = myString;
        System.out.println("Enter the address of the city csv file: ");
        myString = reader.nextLine();
        citycsvPath=myString;

        createCityMap();
        getFilesAndExtract();
        Object[] possibilities = {"ham", "spam", "yam"};

        //     createFrame();
        System.out.println(finalResult.keySet().toString());
        
        
            Iterator SetIterator = finalResult.keySet().iterator();
            Iterator SetValueIterator = finalResult.values().iterator();
            
            while(SetIterator.hasNext())
                System.out.println(  SetValueIterator.next() +  " found in " + SetIterator.next());

//        // extract useful information from files
//        createCityMap();
//        currentpath = "C:/Users/Rajat/Google Drive/part time/escort/2escorts.net/http%3A%2F%2F2escorts.net%2Fescort%2FFrance%2FParis%2F";
//        HashMap fileMap = extractFromFile(currentpath);
//        analyzeAndUpdateFinal(currentpath, fileMap, null);
//        // transfer that useful information to check with maps
//
//        System.out.println(cityData.get("persico"));
    }

    public static HashMap extractFromFile(String path) throws IOException {

        // Strictly works on given path
        System.out.println("Extracting metadata from " + path);

        Document myDocument = Jsoup.parse(readFile(path, Charset.defaultCharset()), "UTF-16");

        // get the meta data
        // System.out.println(myDocument.toString());
        Elements myElements = myDocument.getElementsByTag("meta");
        // System.out.println("elements by meta tag: " + myElements.toString());
        HashMap pageMetaData = new HashMap();
        String temp;
        temp = myElements.attr("name");
        myElements.toString();

        Iterator metaIterator = myElements.iterator();

        // lookup metadata elements to fetch descriptions and keywords
        while (metaIterator.hasNext()) {
            Element e = (Element) metaIterator.next();
            String nameVal = e.attr("name");
            if (nameVal.equals("description")) {
                pageMetaData.put("name", e.attr("content"));
            } else if (nameVal.equals("keywords")) {
                pageMetaData.put("keywords", e.attr("content"));
            }
        }
        return pageMetaData;
    }

    public static ArrayList extractAllFiles() {
        return null;
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /**
     * Creates a list of final keywords extracted form a particular file, checks
     * their existence in the city map and updates the finalList
     *
     *
     * @param path
     * @param content
     * @param cityMap
     */
    public static void analyzeAndUpdateFinal(String fileName, HashMap content) {

        // content passed here is actually the metadata
        // create a String array after delimiting description and keywords
        ArrayList finalKeywords = new ArrayList();
        ArrayList keywordsList1 = new ArrayList();
        ArrayList keywordsList2 = new ArrayList();

        if (content.containsKey("name")) {

            System.out.println("description is " + ((String) content.get("name")));
            String[] temp = ((String) content.get("name")).split(" ");
            keywordsList1 = new ArrayList(Arrays.asList(temp));
        }

        if (content.containsKey("keywords")) {
            String[] temp = ((String) content.get("keywords")).split(",");

            ArrayList keywordsList2Temp = new ArrayList(Arrays.asList(temp));
            keywordsList2 = clean(keywordsList2Temp);
        }

        finalKeywords.addAll(keywordsList1);
        finalKeywords.addAll(keywordsList2);

        System.out.println(finalKeywords.toString());

        Iterator itt = finalKeywords.iterator();

//        while (itt.hasNext()) {
//            System.out.println(itt.next());
//        }
        Iterator finalKeywordsIterator = finalKeywords.iterator();

        while (finalKeywordsIterator.hasNext()) {

            String nextString = finalKeywordsIterator.next().toString();
            nextString = nextString.toLowerCase();
            Object myO = cityData.get(nextString);
            if (myO == null) {
                //    System.out.println(nextString + " does not exist");
            } else {
                finalResult.put(fileName, nextString);
                System.out.println(" Found " + nextString +  "! Appending final result for page " + fileName );
            }
        }

        // check each word in the string array for existance in the citymap
        // if the word is found, update the word in the finalResult using the path
    }

    public static ArrayList clean(ArrayList toBeCleaned) {

        ArrayList cleanedArrayList = new ArrayList();

        Iterator toBeCleanedIterator = toBeCleaned.iterator();

        while (toBeCleanedIterator.hasNext()) {
            String cleaned = toBeCleanedIterator.next().toString();
            String cleanedTemp[];
            if (cleaned.contains("-")) {
                cleanedTemp = cleaned.split("-");
                ArrayList cleanedArrayListTemp = new ArrayList(Arrays.asList(cleanedTemp));
                cleanedArrayList.addAll(cleanedArrayListTemp);
            } else {
                cleanedArrayList.add(cleaned);
            }

        }

        return cleanedArrayList;
    }

    public static void createCityMap() throws IOException {

        CSVReader reader = new CSVReader(new FileReader(citycsvPath));
        String[] nextLine;
        int count = 0;
        while ((nextLine = reader.readNext()) != null) {

            String tempc = (nextLine[0].replace("united states	", "")).trim();

            //  String tempc = (nextLine[0].split("  "))[1];
            cityData.put(tempc, true);
            //    System.out.println(tempc);
            //      System.out.println("indexing" + count);
            count++;
        }

    }

    public static void getFilesAndExtract() throws IOException {
        String[] finalList = (new File(directory)).list();

        // every folder in the root directory
        for (String each : finalList) {
            System.out.println("checking " + each);
            // if the entry is a folder
            if ((new File(directory + "/" + each)).isDirectory()) {
                // lookup all files and extract from them 

                File[] allFiles = (new File(directory + "/" + each)).listFiles();

                for (File myFile : allFiles) {

                    // for a single file path, extract data and update master list
                    currentpath = myFile.getName();
                    ;
                    analyzeAndUpdateFinal(myFile.getName(), extractFromFile(myFile.getPath()));
                }

            } else {
                // if the entry is a file
                // extract from this file 
                analyzeAndUpdateFinal(each, extractFromFile(each));
            }

        }

    }

    private static void createFrame() {

        //1. Create the frame.
        JFrame frame = new JFrame("FrameDemo");

//2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//3. Create components and put them in the frame.
//...create emptyLabel...
//frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
//4. Size the frame.
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));

        final JTextField textField = new JTextField();
        textField.setText("Enter HTML File path here");

        JButton myButton = new JButton("Update");
        myButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // update the directory path
                directory = textField.getText();

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        myButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        myPanel.add(textField);
        myPanel.add(myButton);

        frame.add(myPanel);
        frame.pack();

        //frame.pack();
//frame.add(myButton);
//5. Show it.
        frame.setVisible(true);
    }

}
