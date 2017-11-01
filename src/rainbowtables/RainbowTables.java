
package rainbowtables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.*;


public class RainbowTables {

    static BigInteger prime = new BigInteger("102280479593");
    
    static char[] charset = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static Random random = new Random();
    
    static int tablesize = 15000;
    static int chainlength = 15000;
    static int stringlength = 8;
    
    static HashMap<String, String> rainbow = new HashMap<>();
    
    static FileOps fo = new FileOps("10000x10000l6");
    
    static int count = 0;
    
    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException  
    {
        //rainbow = fo.load();
        generateTable(stringlength);
        fo.save(rainbow);
        System.out.println("Chains generated: " + rainbow.keySet().size());
        //System.out.println("\n\n");
        //bruteforce(charset, charset.length, stringlength, "");
        //System.out.println("\n\nTOTAL CRACKED: " + count);
        //System.out.println(crack(sha1("36705452")));
        //System.out.println(crack(sha1("38847313")));
        //System.out.println(crack(sha1("40056752")));
        //System.out.println("Cracked " + count);
        //String a = "";
        //ArrayList<String> st = new ArrayList<>();
        //for(int i = 0; i < charset.length; i++)
        //{
        //    a+=charset[i];
            //System.out.println("Letter: " + a);
        //    String t = "";
        //    for(int j = 0; j < charset.length; j++)
        //    {
        //        t = reduce(sha1(a), stringlength, j);
        //        if(!st.contains(t))
        //            st.add(t);
        //    }
        //    a = "";
        //    System.out.println();
        //}
        //for(String s : st)
        //    System.out.print("'" + s + "' ");
        //System.out.print("\nTotal: " + st.size() + "\n");
    }
    
    static void bruteforce(char[] set, int n, int length, String p) throws NoSuchAlgorithmException, FileNotFoundException
    {
        if(length == 0)
        {
            System.out.println("\n\nCRACKING " + p);
            String sha = sha1(p);
            //System.out.println("Checking " + p + " with hash " + sha);
            if(!crack(sha).equals("NOT FOUND"))
            {
                System.out.println("Cracked " + p + " with hash: " + sha);
                count++;
            }
            return;
        }
        for(int i = 0; i < n; i++)
        {
            String np = p + set[i];
            bruteforce(set, n, length - 1, np);
        }
        return;
    }
    
    static String crack(String hash) throws FileNotFoundException, NoSuchAlgorithmException
    {
        hash = hash.toLowerCase();
        String phash = hash;
        for(int o = 1; o < chainlength; o++)
        {
            hash = phash;
            System.out.println(o);
            for(int c = 0; c < chainlength; c++)
            {
                //System.out.println("Checking hash: " + hash);
                for(String key : rainbow.keySet())
                {
                    //System.out.println("Against key " + key);
                    if(key.equals(hash))
                    {
                        //System.out.println("EQUAL");
                        String h;
                        String tp = rainbow.get(key);
                        //System.out.println("\tTP: " + rainbow.get(key));
                        String p = tp;
                        int l = tp.length();
                        for(int i = 1; i < chainlength + 1; i++)
                        {
                            h = sha1(tp);
                            //System.out.println("\tHashed to " + h);
                            //System.out.println("\tPHASH: " + phash);
                            if(h.equals(phash))
                                return tp;
                            tp = reduce(h, l, i);
                            //System.out.println("\tReduced to " + tp);
                        }
                    }
                }
                hash = reduce(hash, stringlength, c + o);
                hash = sha1(hash);
            }
        }
        return "NOT FOUND";
    }
    
    static void generateTable(int slength) throws NoSuchAlgorithmException, IOException
    {
        for(int i = 0; i < tablesize; i++)
        {
            System.out.println("Generating chain " + i + "...");
            generateChain(getString(charset, slength));
        }
    }
    
     static void generateChain(String start) throws NoSuchAlgorithmException
    {
        String h = "";
        String tp = start;
        //System.out.println("Start: " + start);
        for(int i = 1; i < chainlength + 1; i++)
        {
            //System.out.println("TP: " + tp);
            h = sha1(tp);
            //System.out.println("TP hashed: " + h);
            tp = reduce(h, stringlength, i);
            //System.out.println("H reduced: " + tp);
        }
        rainbow.put(h, start);
    }
    
    static String getString(char[] set, int maxlength)
    {
        String s = "";
        for(int i = 0; i < maxlength; i++)
            s += set[random.nextInt(set.length)];
        //System.out.println("String generated: " + s);
        return s;
    }
    
    static String reduce(String hash, int length, int step)
    {
        //System.out.println("STEP " + step);
        //System.out.print("Hash: " + hash + " ");
        BigInteger bi = new BigInteger(hash, 16);
        bi = bi.multiply(BigInteger.valueOf(step));
        bi = bi.mod(prime);
        hash = bi.toString();
        //System.out.println("As BI: " + hash + " ");
        String reduced = "";
        int x = 0;
        while(true)
            for(int i = 0; i < hash.length(); i++)
            {
                if(reduced.length() == length)
                {
                    return reduced;
                }
                else
                {
                    step %= hash.length();
                    step = step < 0 ? step + hash.length() : step;
                    //System.out.println("STEP2: " + step);
                    x = hash.charAt(i) + hash.charAt(step % hash.length());
                    //System.out.println("x: " + x);
                    x %= charset.length;
                    //System.out.println("xm: " + x);
                    //System.out.println("xmf: " + x);
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
    
   
}