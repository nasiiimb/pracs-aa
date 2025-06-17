package model;

import main.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;


/**
 * Handler of the different models available.
 */
public class Model implements Notify
{
    private final Controller controller;
    private String fileName;
    private HuffmanEncoder encoder;

    private double compressionPercentage;
    private long executionTimeMillis;
    private double meanCodeLength;
    private long originalFileSize;
    private long compressedFileSize;


    public Model(Controller controller)
    {
        this.controller = controller;
        this.fileName = null;
        this.encoder = null;

        this.compressionPercentage = 0.0;
        this.executionTimeMillis = 0;
        this.meanCodeLength = 0.0;
        this.originalFileSize = 0;
        this.compressedFileSize = 0;
    }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public Map<Character, String> getEncoder()
    {
        if (encoder == null)
            return null;
        return encoder.getEncodeTable();
    }

    public double getCompressionPercentage() { return compressionPercentage; }

    public String getFormattedExecutionTime()
    {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(executionTimeMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(executionTimeMillis) % 60;
        long millis = executionTimeMillis % 1000;
        return String.format("%d min, %d sec, %d ms", minutes, seconds, millis);
    }

    public double getMeanCodeLength() {return meanCodeLength; }

    public long getOriginalFileSize() { return originalFileSize; }

    public long getCompressedFileSize() { return compressedFileSize; }


    private void start() {
        if (fileName.endsWith(".comp"))
        {
            decompress(fileName);
            return;
        }

        this.compressionPercentage = 0.0;
        this.executionTimeMillis = 0;
        this.meanCodeLength = 0.0;
        this.originalFileSize = 0;
        this.compressedFileSize = 0;

        long startTime = System.currentTimeMillis();

        Map<Character, Integer> freq = new HashMap<>();
        FileReader fIn;
        BufferedReader br;

        File originalFile = new File(fileName);
        if (originalFile.exists())
            originalFileSize = originalFile.length();
        else
        {
            System.err.println("Error: Original file not found - " + fileName);
            controller.notify(Notify.PROCESS_FINISHED);
            return;
        }

        try
        {
            fIn = new FileReader(fileName);
            br = new BufferedReader(fIn);
            try
            {
                int c = br.read();
                while (c != -1)
                {
                    freq.put((char)c, freq.getOrDefault((char)c, 0) + 1);
                    c = br.read();
                }
            }
            catch (IOException ex) { System.err.println("Error reading file: " + ex.getMessage()); }
            finally
            {
                try
                {
                    br.close();
                    fIn.close();
                } catch (IOException ex) { System.err.println("Error closing input stream: " + ex.getMessage()); }
            }
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("Error opening file reader: " + ex.getMessage());
            controller.notify(Notify.PROCESS_FINISHED);
            return;
        }

        if (freq.isEmpty())
        {
            System.err.println("Error: Input file is empty or could not be read.");
            executionTimeMillis = System.currentTimeMillis() - startTime;
            controller.notify(Notify.PROCESS_FINISHED);
            return;
        }

        HuffmanTree tree = new HuffmanTree(freq);
        encoder = new HuffmanEncoder(tree);

        calculateMeanCodeLength(freq, encoder.getEncodeTable());
        controller.notify(Notify.PAINT);

        String fileOut = compress(freq, encoder, fileName);

        File compressedFile = new File(fileOut);
        if (compressedFile.exists())
            compressedFileSize = compressedFile.length();

        if (originalFileSize > 0)
            compressionPercentage = (1.0 - (double) compressedFileSize / originalFileSize) * 100.0;
        else
            compressionPercentage = 0.0;

        executionTimeMillis = System.currentTimeMillis() - startTime;
        controller.notify(Notify.PROCESS_FINISHED);
        controller.notify(Notify.PAINT);
    }

    private void calculateMeanCodeLength(Map<Character, Integer> frequencies, Map<Character, String> encodeTable)
    {
        if (encodeTable == null || frequencies == null || frequencies.isEmpty())
        {
            this.meanCodeLength = 0.0;
            return;
        }

        long totalBits = 0;
        long totalChars = 0;

        for (Map.Entry<Character, Integer> entry : frequencies.entrySet())
        {
            char character = entry.getKey();
            int frequency = entry.getValue();
            String code = encodeTable.get(character);

            if (code != null)
            {
                totalBits += (long) frequency * code.length();
                totalChars += frequency;
            }
            else
                System.err.println("Warning: No code found for character '" + character + "' (ASCII: " + (int)character + ")");
        }

        if (totalChars > 0)
            this.meanCodeLength = (double) totalBits / totalChars;
        else
            this.meanCodeLength = 0.0;
    }


    public String compress(Map<Character, Integer> freq, HuffmanEncoder encoder, String ficheroIn)
    {
        String ficheroOut = ficheroIn.concat(".comp");
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;

        FileReader fileReader;
        BufferedReader bufferedReader;
        FileOutputStream fileOutputStream;
        BufferedOutputStream bufferedOutputStream;

        RandomAccessFile raf;

        try
        {
            fileWriter = new FileWriter(ficheroOut, false);
            bufferedWriter = new BufferedWriter(fileWriter);
            int bytes = 0;
            try
            {
                bufferedWriter.write(new char[]{0, 0, 0, 0});
                bufferedWriter.newLine();
                bufferedWriter.write(0);
                bufferedWriter.newLine();
                bytes += 7;

                for (char c : freq.keySet())
                {
                    Stack<Integer> stack = new Stack<>();
                    int output;
                    String key = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
                    while (key.length() > 8)
                    {
                        output = Integer.valueOf(key.substring(key.length() - 8), 2);
                        stack.push(output);
                        key = key.substring(0, key.length() - 8);
                    }
                    output = Integer.valueOf(String.format("%8s", key).replace(' ', '0'), 2);
                    stack.push(output);

                    while (!stack.isEmpty())
                    {
                        output = stack.pop();
                        String s = String.format("%2s", Integer.toHexString(output)).replace(' ', '0');
                        bufferedWriter.write(s);
                        bytes += 2;
                    }
                    bufferedWriter.newLine();
                    bytes++;


                    String val = encoder.encode(String.valueOf(c));
                    bytes += val.length();
                    bufferedWriter.write(val);
                    bufferedWriter.newLine();
                    bytes++;
                }
            }
            catch (IOException ex) { System.err.println(ex.getMessage()); }
            finally
            {
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            }


            int offset = 0;
            fileReader = new FileReader(ficheroIn);
            bufferedReader = new BufferedReader(fileReader);
            fileOutputStream = new FileOutputStream(ficheroOut, true);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            try
            {
                String bitsAux, byteCreator = "";
                int c = bufferedReader.read();
                while (c != -1)
                {
                    byteCreator += encoder.encode(String.valueOf((char)c));
                    while (byteCreator.length() >= 8)
                    {
                        bitsAux = byteCreator.substring(0, 8);
                        int b = Integer.parseInt(bitsAux, 2);
                        bufferedOutputStream.write(b);
                        byteCreator = byteCreator.substring(8);
                    }

                    c = bufferedReader.read();
                }

                if (!(byteCreator.isEmpty()) && (byteCreator.length() < 8))
                {
                    bitsAux = String.format("%-8s", byteCreator).replace(' ', '0');
                    bufferedOutputStream.write(Integer.parseInt(bitsAux));
                    offset += 8 - byteCreator.length();
                }
            }
            catch (IOException ex) { System.err.println(ex.getMessage()); }
            finally
            {
                bufferedReader.close();
                fileReader.close();
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                fileOutputStream.close();
            }



            raf = new RandomAccessFile(ficheroOut, "rw");
            try
            {
                raf.seek(0);
                raf.writeInt(bytes);
                raf.writeByte(10);

                raf.write(offset);
                raf.writeByte(10);
            }
            catch (IOException ex) { System.err.println(ex.getMessage()); }
            finally
            {
                raf.close();
            }
        }
        catch (IOException ex) { System.err.println(ex.getMessage()); }

        return ficheroOut;
    }


    public void decompress(String fileIn)
    {
        Map<String, Character> decodeTable = new HashMap<>();
        String fileOut = fileIn.substring(0, fileIn.length()-5);
        int lastDot = fileOut.lastIndexOf('.');

        String baseName;
        String extension;
        if (lastDot != -1)
        {
            baseName = fileOut.substring(0, lastDot);
            extension = fileOut.substring(lastDot);
        }
        else
        {
            baseName = fileOut;
            extension = "";
        }

        File outFile;
        int counter = 1;
        String currentOutName = baseName + "_decoded" + extension;
        outFile = new File(currentOutName);
        while (outFile.exists())
        {
            currentOutName = baseName + "_decoded_" + counter + extension;
            outFile = new File(currentOutName);
            counter++;
        }


        RandomAccessFile raf;
        FileReader fileReader;
        BufferedReader bufferedReader;

        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;

        try
        {
            int bytes = 0, offset = 0;
            raf = new RandomAccessFile(fileIn, "r");
            try
            {
                bytes = raf.readInt();
                raf.skipBytes(1);
                offset = raf.readByte();
            }
            catch (IOException ex) { System.err.println(ex.getMessage()); }
            finally { raf.close(); }

            fileReader = new FileReader(fileIn);
            bufferedReader = new BufferedReader(fileReader);
            try
            {
                int bytesRead = 0;
                String line = bufferedReader.readLine();
                bytesRead += line.length()+1;
                line = bufferedReader.readLine();
                bytesRead += line.length()+1;

                line = bufferedReader.readLine();
                while ((line != null) && (bytesRead < bytes))
                {
                    int key;
                    bytesRead += line.length()+1;
                    key = Integer.valueOf(line, 16);
                    System.out.print((char)key + " : ");

                    line = bufferedReader.readLine();
                    bytesRead += line.length()+1;
                    System.out.println(line);

                    decodeTable.put(line, (char)key);
                    line = bufferedReader.readLine();
                }
            }
            catch (IOException ex) { System.err.println(ex.getMessage()); }
            finally
            {
                bufferedReader.close();
                fileReader.close();
                raf.close();
            }

            HuffmanEncoder encoder = new HuffmanEncoder(decodeTable);

            fileInputStream = new FileInputStream(fileIn);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            fileWriter = new FileWriter(outFile, false);
            bufferedWriter = new BufferedWriter(fileWriter);
            try
            {
                String bitsAux[], byteCreator = "";
                bufferedInputStream.skipNBytes(bytes);
                int c = bufferedInputStream.read();
                int cNext = bufferedInputStream.read();
                while (cNext != -1)
                {
                    byteCreator += String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
                    bitsAux = encoder.decode(byteCreator);
                    if (!bitsAux[0].isEmpty())
                    {
                        bufferedWriter.write(bitsAux[0]);
                    }
                    byteCreator = bitsAux[1];

                    c = cNext;
                    cNext = bufferedInputStream.read();
                }

                byteCreator += String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0').substring(0, 8-offset);
                bitsAux = encoder.decode(byteCreator);
                if (!bitsAux[0].isEmpty())
                {
                    bufferedWriter.write(bitsAux[0]);
                }

            }
            catch (IOException ex) { System.err.println(ex.getMessage()); }
            finally
            {
                bufferedInputStream.close();
                fileInputStream.close();
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            }
        }
        catch (IOException ex) { System.err.println(ex.getMessage()); }
    }


    @Override
    public void notify(String s)
    {
        switch (s)
        {
            case START -> start();

            case STOP -> {}
        }
    }
}