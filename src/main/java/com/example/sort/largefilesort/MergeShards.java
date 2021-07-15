package com.example.sort.largefilesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class MergeShards {
    
    public static void main(String[] args) 
        throws IOException {
        if(args.length < 1){
            System.err.println("Please provide path to shards folder");
            return;
        }

        // read all the files
        List<Path> paths = Files.walk(Path.of(args[0]), 1).skip(1).collect(Collectors.toList());
        List<BufferedReader> bufferedReaders = new ArrayList<>(paths.size());
        try{
            paths.stream()
                .forEach(
                    p -> {
                        try {
                            bufferedReaders.add(Files.newBufferedReader(p));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                );

            if(bufferedReaders.isEmpty())
                    System.err.println("No readers created");
            
            PriorityQueue<WordEntry> priorityQueue = new PriorityQueue<>();
            for (BufferedReader bufferedReader : bufferedReaders) {
                String word = null;
                // read single line
                if((word = bufferedReader.readLine()) != null){
                    priorityQueue.add(new WordEntry(word, bufferedReader));
                }
            }

            // open the output writer and write the words in PQ into the file
            // PQ alreadys sorts the words across files when they are inserted
            try(BufferedWriter writer = Files.newBufferedWriter(Path.of(args[1]))){
                while(!priorityQueue.isEmpty()){
                    WordEntry entry = priorityQueue.poll();
                    writer.write(entry.word);
                    writer.write(System.lineSeparator());
                    String word = entry.bufferedReader.readLine();
                    if(word != null)
                        priorityQueue.add(new WordEntry(word, entry.bufferedReader));
                }
            }
        }
        finally{
            for(BufferedReader reader : bufferedReaders){
                reader.close();
            }
        }
    }

    private static final class WordEntry implements Comparable<WordEntry> {

        private final String word;
        private final BufferedReader bufferedReader;

        private WordEntry(String word, BufferedReader reader){
            this.word = Objects.requireNonNull(word);
            this.bufferedReader = Objects.requireNonNull(reader);
        }

        @Override
        public int compareTo(WordEntry o) {
            return word.compareTo(o.word);
        }
        
    }
}
