package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reducer for the document similarity job.
 *
 * Input:  whatever your mapper emits, grouped by key by the shuffle/sort phase.
 *
 * Output: one line per pair of documents that share at least one word, in exactly this
 *         format (see README.md):
 *
 *             Doc01, Doc02 Similarity: 0.18
 *
 *         where the two IDs are in ascending String order (Doc01 before Doc02), and the
 *         Jaccard similarity  |A ∩ B| / |A ∪ B|  is printed with two decimals, e.g.
 *         String.format("%.2f", similarity). Note that "%.2f" uses the machine's locale;
 *         use  String.format(java.util.Locale.US, "%.2f", similarity)  to be safe.
 *
 * Hint: in the design suggested in README.md all documents reach a single reducer, one per
 *       reduce() call. You cannot compare documents until you have seen all of them, so
 *       reduce() only stores each document, and the pairwise comparison happens in
 *       cleanup(), which Hadoop calls once after the last reduce() call.
 */
public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    private Map<String, Set<String>> documents = new HashMap<>();
    @Override
    protected void reduce(org.w3c.dom.Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        for(org.w3c.dom.Text value : values){
            Set<String> words = new HashSet<>();
            String wordList = value.toString().trim();
            if(!wordList.isEmpty()){
                String[] tokens = wordList.split("\\s+");
                for(String word : tokens){
                    words.add(word);
                }
            }
            documents.put(key.toString(), words);
        }
        


    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> documentIds = new ArrayList<>(documents.keySet());
        for(int i = 0; i< documentIds.size(); i++){
            for(int j = i+1; j < documentIds.size();j++){
                String docA = documentIds.get(i);
                String docB = documentIds.get(j);
                Set<String> wordsA = documents.get(docA);
                Set<String> wordsB = documents.get(docB);

                Set<String> intersection = new HashSet<>(wordsA);
                intersection.retainAll(wordsB);
                if(intersection.isEmpty()){
                    continue;
                }
                Set<String> union = new HashSet<>(wordsA);
                intersection.addAll(wordsB);
                double similarity = (double) intersection.size()/union.size();
                String result = String.format(Locale.US,"%.2f",similarity);
                String first = docA;
                String second = docB;
                if (first.compareTo(second) > 0) {
                    String temp = first;
                    first = second;
                    second = temp;
                }
                String output = first + ", " + second + " Similarity: " + result;
                context.write(new Text(output), new Text(""));
            }
        }

    }
}
