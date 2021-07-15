package com.example.sort.largefilesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MakeShards {
    private static final int SHARD_SIZE = 100;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
        System.out.println("Usage: MakeShards [input file] [output folder]");
        return;
        }

        Path input = Path.of(args[0]);
        Path outputFolder = Files.createDirectory(Path.of(args[1]));

        
        String line;
        int shardNum = 0;

        List<String> wordList = new ArrayList<>();

        try(BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)){    
            while((line = reader.readLine()) != null){
                wordList.add(line);
                if(wordList.size() == SHARD_SIZE){
                    // increment shard number and write into a file
                    shardNum++;
                    writeListToFile(wordList, shardNum, outputFolder);
                    // clear the list
                    wordList.clear();
                }
                
            }
        }

        // Read the unsorted words from the "input" Path, line by line. Write the input words to
        //       many shard files. Each shard file should contain at most SHARD_SIZE words, in sorted
        //       order. All the words should be accounted for in the output shard files; you should not
        //       skip any words. Write the shard files in the newly created "outputFolder", using the
        //       getOutputFileName(int) method to name the individual shard files.
    }

    private static void writeListToFile(List<String> wordList, int shardNum, Path outPath) 
        throws IOException {
        String outFileName = getOutputFileName(shardNum);
        // sorting in alphabetical order
        Collections.sort(wordList);
        try(BufferedWriter writer = Files.newBufferedWriter(Path.of(outPath.toString(), outFileName), StandardCharsets.UTF_8)){
            wordList.stream()
                    .forEach(
                        word -> {
                            try {
                                writer.write(word);
                                writer.write(System.lineSeparator());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    );
            writer.flush();
        }
    }

    private static String getOutputFileName(int shardNum) {
        return String.format("shard%02d.txt", shardNum);
    }
}
