
package rainbowtables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.*;


public class RainbowTables {

    static BigInteger prime = new BigInteger("102280479593");
    
    static ArrayList<String> hashes = new ArrayList<String>();
    
    static char[] charset = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static Random random = new Random();
    
    static int tablesize = 2000;
    static int chainlength = 2000;
    static int stringlength = 6;
    
    static HashMap<String, String> rainbow = new HashMap<>();
    
    static FileOps fo = new FileOps("test6");
    
    static long starttime = System.currentTimeMillis();
    
    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException  
    {
        //hashes.add("bb888922700565814d8437467cf8898365dd3938");
        hashes.add(sha1("786347"));
        hashes.add(sha1("124245"));
        hashes.add(sha1("656412"));
        hashes.add(sha1("67567"));
        hashes.add(sha1("124212"));
        hashes.add(sha1("223642"));
        hashes.add(sha1("624712"));
        hashes.add(sha1("364812"));
        hashes.add(sha1("137512"));
        hashes.add(sha1("125412"));
        hashes.add(sha1("158672"));
        hashes.add(sha1("129192"));
        hashes.add(sha1("120572"));
        hashes.add(sha1("894712"));
        hashes.add(sha1("975624"));
        hashes.add(sha1("624812"));
        hashes.add(sha1("349246"));
        hashes.add(sha1("745112"));

        generateTable(stringlength);
        fo.save(rainbow);
        
        //rainbow = fo.load();
        
        cracked.setup(hashes.size());
        crack(hashes);
        cracked.printAll();
    }  
    
static void crack(ArrayList<String> hashes) throws FileNotFoundException, NoSuchAlgorithmException
{
    for(String hash : hashes)
        hash = hash.toLowerCase();
    ArrayList<String> originalHashes = new ArrayList<>(hashes);
    for(int k = chainlength; k > 0; k--) 
    {
        System.out.println("Checking step " + k);
        checkForHash(hashes, originalHashes);
        ArrayList<String> temporaryHashes = new ArrayList<>(hashes);
        for(int i = k; i <= chainlength; i++)
        {
            for(int h = 0; h < temporaryHashes.size(); h++)
                temporaryHashes.set(h, sha1(reduce(temporaryHashes.get(h), stringlength, i))); 
            checkForHash(temporaryHashes, originalHashes);
        }
    }       
} 
    static void checkForHash(ArrayList<String> hashes, ArrayList<String> originalHashes) throws NoSuchAlgorithmException
    {
        for(String key : rainbow.keySet())
        {
            for(int h = 0; h < hashes.size(); h++)
                if(hashes.get(h).equals(key)) 
                {
                    String plaintext = rainbow.get(key);
                    hashes.set(h, sha1(plaintext));
                    for(int i = 1; i < chainlength + 1; i++)
                    {
                        if(hashes.get(h).equals(originalHashes.get(h)))
                        {
                            if(!cracked.check(originalHashes.get(h)))
                                cracked.add(System.currentTimeMillis() - starttime, originalHashes.get(h), plaintext);
                        }
                        plaintext = reduce(hashes.get(h), stringlength, i);
                        hashes.set(h, sha1(plaintext));
                    }
                }
        }
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
        for(int i = 1; i < chainlength + 1; i++)
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
    
    static String reduce(String hash, int length, int step)
    {
        BigInteger bi = new BigInteger(hash, 16);
        bi = bi.multiply(BigInteger.valueOf(step));
        bi = bi.mod(prime);
        hash = bi.toString();
        String reduced = "";
        int x = 0;
        while(true)
            for(int i = 0; i < hash.length(); i++)
                if(reduced.length() == length)
                    return reduced;
                else
                {
                    step %= hash.length();
                    step = step < 0 ? step + hash.length() : step;
                    x = hash.charAt(i) + hash.charAt(step % hash.length());
                    x %= charset.length;
                    reduced += charset[x];
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