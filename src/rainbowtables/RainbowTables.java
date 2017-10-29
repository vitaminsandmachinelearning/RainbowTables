
package rainbowtables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.util.HashMap;
import java.util.Random;


public class RainbowTables {

    static char[] charset = {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static Random random = new Random();
    
    static int tablesize = 10;
    static int chainlength = 1000;
    static int stringlength = 1;
    
    static HashMap<String, String> rainbow = new HashMap<>();
    
    static FileOps fo = new FileOps("10000x10000l6");
    
    static int count = 0;
    
    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException  
    {
        //rainbow = fo.load();
        generateTable();
        fo.save(rainbow);
        bruteforce(charset, charset.length, stringlength, "");
        System.out.println("Cracked " + count);
    }
    
    static int bruteforce(char[] set, int n, int length, String p) throws NoSuchAlgorithmException, FileNotFoundException
    {
        if(length == 0)
        {
            String sha = sha1(p);
            System.out.println("Checking " + p + " hash " + sha);
            if(!crack(sha).equals("NOT FOUND"))
            {
                System.out.println("Cracked " + p + " with hash: " + sha);
                count++;
            }
            return 0;
        }
        for(int i = 0; i < n; i++)
        {
            String np = p + set[i];
            bruteforce(set, n, length - 1, np);
        }
        return 0;
    }
    
    static String crack(String hash) throws FileNotFoundException, NoSuchAlgorithmException
    {
        hash = hash.toLowerCase();
        String phash = hash;
        
        for(int c = 0; c < chainlength; c++)
        {
            for(String key : rainbow.keySet())
            {

                if(key.equals(hash))
                {
                    String h = key;
                    String tp = rainbow.get(key);
                    String p = tp;
                    int l = tp.length();
                    for(int i = 0; i < chainlength; i++)
                    {
                        h = sha1(tp);
                        if(h.equals(phash))
                            return tp;
                        tp = reduce(h, l, i);
                    }
                }
                else
                {
                    hash = reduce(hash, stringlength, chainlength - 1);
                    hash = sha1(hash);
                }
            }
        }
        return "NOT FOUND";
    }
    
    static void generateTable() throws NoSuchAlgorithmException, IOException
    {
        for(int i = 0; i < tablesize; i++)
        {
            System.out.println("Generated " + i + " chains.");
            for(int j = 1; j < stringlength + 1; j++)
                generateChain(getString(charset, j));
        }
    }
    
    static void outputTable()
    {
        rainbow.keySet().forEach((key) -> {
            System.out.println("Plaintext: " + rainbow.get(key) + " Hash: " + key);
        });
        System.out.println("Total keys: " + rainbow.keySet().size());
    }
    
    static String reduce(String hash, int length, int step)
    {
        String reduced = "";
        int x = 0;
        while(true)
            for(int i = 0; i < hash.length(); i++)
            {
                if(reduced.length() == length)
                    return reduced;
                else
                {
                    for(int c = 0; c < charset.length; c++)
                        if(hash.charAt(i) == charset[c])
                        {
                            x = (c * step + step * length + i * step) / (step + charset.length);
                            break;
                        }
                    x %= charset.length;
                    x = x < 0 ? x + charset.length : x;
                    reduced += charset[x];
                }
            }
    }
    
    static String sha1(String password) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] bytes = md.digest(password.getBytes());
        String toReturn = "";
        for(int i = 0; i < bytes.length; i++)
            toReturn += Integer.toString((bytes[i]&0xff) + 0x100, 16).substring(1);
        return toReturn;
    }
    
    static void generateChain(String start) throws NoSuchAlgorithmException
    {
        String h = "";
        String tp = start;
        for(int i = 0; i < chainlength; i++)
        {
            h = sha1(tp);
            tp = reduce(h, stringlength, i);
        }
        rainbow.put(h, start);
    }
    
    static String getString(char[] set, int maxlength)
    {
        String s = "";
        for(int i = 0; i < maxlength; i++)
            s += set[random.nextInt(set.length)];
        return s;
    }
}