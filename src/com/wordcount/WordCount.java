package com.wordcount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

/**
 * The WordCount program simply counts the words in each line and calculate the
 * counter of each word per set of files also, it calculates the median for a
 * set of numbers
 * 
 * @author Mostafa Hamed
 * @version 1.0
 * @since 2015-03-07
 */

public class WordCount {

	public static String inputDir;
	public static String outputDir;
	public ArrayList<String> filesNames = new ArrayList<String>();
	public ArrayList<String> filesContents = new ArrayList<String>();
	public StringBuilder filesContentsAsString = new StringBuilder();
	public Map<String, Integer> wordCount = new HashMap<String, Integer>();
	public ArrayList<Integer> wordCountPerLine = new ArrayList<Integer>();
	public ArrayList<Double> median = new ArrayList<Double>();

	/**
	 * This method will read all files in a specific directory and store file
	 * names in a arrayList of Strings ex. "filesNames"
	 * 
	 * @param folder
	 * @return
	 */
	private void loadFilesNames(File folder) {
		for (File fileEntry : folder.listFiles()) {
			filesNames.add(fileEntry.getName());
		}
		Collections.sort(filesNames);
	}

	/**
	 * This method will read all data from all files in specific directory
	 * sorted in one stream.
	 */
	private void readFileContent() {

		Vector<InputStream> inputStreams = new Vector<InputStream>();
		FileInputStream in = null;
		for (int i = 0; i < filesNames.size(); i++) {
			try {
				in = new FileInputStream(inputDir + "/" + filesNames.get(i));
				inputStreams.add(in);
			} catch (FileNotFoundException e1) {
			}
		}

		Enumeration<InputStream> e = inputStreams.elements();
		SequenceInputStream is = new SequenceInputStream(e);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while (((line = br.readLine()) != null)) {
				filesContents.add(line);
				filesContentsAsString.append(line);
			}
		} catch (IOException e1) {
			System.out.println("Error in reading");
		}

	}

	/**
	 * This method will count a specific word in a set of files and store this
	 * counter in a wc_result.txt
	 */
	private void getWordCount() {
		int count = 1;
		for (int i = 0; i < filesContents.size(); i++) {
			StringBuilder sb = new StringBuilder(filesContents.get(i)
					.toString().toLowerCase());
			sb.deleteCharAt(sb.length() - 1);
			StringTokenizer st = new StringTokenizer(sb.toString());
			while (st.hasMoreElements()) {
				String token = (String) st.nextElement();
				if (!wordCount.containsKey(token)) {
					wordCount.put(token, count);
				} else {
					wordCount.put(token, wordCount.get(token) + 1);
				}
			}

		}

		writeWordCount();
	}

	/**
	 * This method will call another 2 methods to calculate a median and store
	 * it in output file
	 */
	private void getMedian() {
		int wordsPerline;
		for (int i = 0; i < filesContents.size(); i++) {
			StringBuilder sb = new StringBuilder(filesContents.get(i).toString().toLowerCase());
			sb.deleteCharAt(sb.length() - 1);
			StringTokenizer st = new StringTokenizer(sb.toString());
			wordsPerline = st.countTokens();
			wordCountPerLine.add(wordsPerline);
			calculateMedian(wordCountPerLine);
		}
		writeMedian();
	}

	/**
	 * This method will calculate median of a specific word in a set of files
	 * 
	 * @param countList
	 */
	private void calculateMedian(ArrayList<Integer> countList) {
		int middle = countList.size() % 2;
		if (countList.size() == 1) { // first element
			median.add((double) countList.get(0));
		} else if (middle == 0) { // even set of numbers
			Collections.sort(countList);
			countList.get((countList.size() / 2) - 1);
			countList.get(countList.size() / 2);
			median.add((double) (countList.get((countList.size() / 2) - 1) + countList.get(countList.size() / 2)) / 2);
		} else { // odd set of numbers
			Collections.sort(countList);
			median.add((double) countList.get(middle));
		}
	}

	/**
	 * This method will write the calculated wordCount into an output file ex.
	 * wc_result.txt
	 */
	private void writeWordCount() {
		int counter = 0;
		FileOutputStream fop = null;
		File file = new File(outputDir + "/wc_result.txt");
		Map<String, Integer> sortedMap = new TreeMap<String, Integer>(wordCount);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			fop = new FileOutputStream(file);
			for (String key : sortedMap.keySet()) {
				counter++;
				String output = key + " " + sortedMap.get(key);
				if (counter != sortedMap.size()) {
					output += "\n";
				}

				fop.write(output.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fop.flush();
				fop.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * This method will write the calculated median into an output file 
	 * ex. med_result.txt
	 */
	private void writeMedian() {
		FileOutputStream fop = null;
		File file = new File(outputDir + "/med_result.txt");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			fop = new FileOutputStream(file);
			for (int i = 0; i < median.size(); i++) {
				String output = median.get(i) + "";
				if (i != median.size() - 1) {
					output += "\n";
				}
				fop.write(output.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fop.flush();
				fop.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Caller method
	 * 
	 * @param inputDir Directory that have all files that will be processed
	 */
	private void process(String inputDir) {
		File inputFolder = new File(inputDir);
		loadFilesNames(inputFolder);
		readFileContent();
	}

	public static void main(String[] args) {
		inputDir = args[0]; //input Directory
		outputDir = args[1]; //output Directory
		WordCount wc = new WordCount();
		wc.process(args[0]);
		wc.getWordCount();
		wc.getMedian();
	}

}
