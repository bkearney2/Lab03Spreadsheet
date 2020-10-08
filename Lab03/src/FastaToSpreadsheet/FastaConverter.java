package FastaToSpreadsheet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 

public class FastaConverter {

    public static String CHAR_LIST = "ACGT";

	static List<Integer> countChars(String sequence) 
	{
		List<Integer> char_count = new ArrayList<Integer>();

		// Repeat counting for each character
		for (int i = 0; i < CHAR_LIST.length(); i++) {
			char cha = CHAR_LIST.charAt(i);
			// Subtract string with character removed from total string length
			int total = sequence.length() - sequence.replace(String.valueOf(cha), "").length();
			char_count.add(total);
		}
		return char_count;
	}
	
	public static void main(String[] args) throws Exception 
	{
		
		BufferedReader reader = new BufferedReader(new FileReader(new File("fasta_input.txt")));
		
		String line = reader.readLine();

		List<String> descs = new ArrayList<String>();
		List<String> seqs = new ArrayList<String>();
		String nextSeq = "";
		int counter = 0;
		List<Integer> counters = new ArrayList<Integer>();
		
		while (line != null) {
			if (line.startsWith(">")) {
				// store headers
				counter = 0;
				// Refresh sequence if a header is found
				nextSeq = "";
				// Remove first two characters "> " from line, for neatness on spreadsheet
				descs.add(line.substring(2));
			} else {
				// store all non-header lines as sequences, store #lines per sequence as counter integer
				nextSeq = nextSeq+line;
//				System.out.println(nextSeq);
				counter++;
				seqs.add(nextSeq);
				counters.add(counter);
			}		
			line = reader.readLine();
		}
		
		reader.close();
		
		// Count how many lines there are per sequence
		counters.add(0);
		List<Integer> trimmed_counters = new ArrayList<Integer>();
		for (int i = 0; i<counters.size()-1;i++) {

			if (counters.get(i) >= counters.get(i+1)) {
				trimmed_counters.add(counters.get(i));
			}
		}
		
		// Combine sequence lines into one sequence using add_index to determine #lines needed per combination
		List<String> trimmed_seqs = new ArrayList<String>();

		int add_index = -1;
		for (int i = 0; i<trimmed_counters.size();i++) {
			
			add_index = trimmed_counters.get(i)+add_index;
			trimmed_seqs.add(seqs.get(add_index));
		}
		System.out.println(trimmed_seqs);
		
		// Create 2D array for storing every sequence's nucleotide counts
		int[][] counts = new int[trimmed_seqs.size()][CHAR_LIST.length()];
		for (int i = 0; i<trimmed_seqs.size();i++) {
			List<Integer> count_list = countChars(trimmed_seqs.get(i));
			Integer[] arr = count_list.toArray(new Integer[count_list.size()]);
			for (int j = 0;j<arr.length;j++) {
				counts[i][j] = arr[j];
			}
			
		}
		System.out.println("ACGT counts(array): "+Arrays.deepToString(counts));

		BufferedWriter writer = new BufferedWriter(new FileWriter("fasta_output.txt"));
		writer.write("sequenceID\tnumA\tnumC\tnumG\tnumT\tsequence");
		for (int i = 0; i<descs.size();i++) {
			writer.newLine();
			writer.write(descs.get(i));
			for (int j = 0;j<counts[0].length;j++) {
//				System.out.println(counts[i][j]);
				writer.write("\t"+counts[i][j]);
			}
			
			writer.write("\t"+trimmed_seqs.get(i));
		}
		
		writer.flush();
		writer.close();
	}

}
