
package rainbowtables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.*;


public class RainbowTables {
    //large prime used for reduce function to allow for even distribution of characters
    static BigInteger prime = new BigInteger("102280479593");
    //set up empty list of hashes. add hashes in main()
    static ArrayList<String> hashes = new ArrayList<String>();
    
    //the character set used to generate plaintexts 
    static char[] charset = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    static Random random = new Random();
    
    static int tablesize = 2000;    //the amount of chains to generate
    static int chainlength = 2000;  //the length of chains to generate
    static int stringlength = 6;    //the length of plaintexts used
    
    //used to store generated or loaded rainbow table
    static HashMap<String, String> rainbow = new HashMap<>();
    //used to read and write files. THE NAME OF THE FILE TO SAVE AND LOAD SHOULD BE THE PARAMETER
    static FileOps fo = new FileOps("test6");
    //get timestamp of program start
    static long starttime = System.currentTimeMillis();
    
    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException  
    {
        //HOW TO ADD HASHES TO THE LIST TO BE CRACKED
        //hashes.add("bb888922700565814d8437467cf8898365dd3938"); // use this if you want to add a hash
        //hashes.add(sha1("786347")); // use this if you want a specific plaintext (debugging and testing)

        //ENABLE THESE LINES TO GENERATE A NEW TABLE AND SAVE IT TO A FILE
        generateTable(stringlength);
        fo.save(rainbow);
        
        //ENABLE THIS LINE TO LOAD A TABLE. DO NOT LOAD AND GENERATE IN ONE
        //INSTANCE OF THE PROGRAM. THE GENERATED TABLE AND LOADED TABLE WILL
        //COMBINE AND MAY PRODUCE UNEXPECTED RESULTS
        rainbow = fo.load();
        
        //set up the utility class cracked to hold any cracked hashes and their timestamps
        cracked.setup(hashes.size());
        //start the cracking algorithm using the list of provided hashes
        crack(hashes);
        //output cracked hashes to the user
        cracked.printAll();
    }  
    
static void crack(ArrayList<String> hashes) throws FileNotFoundException, NoSuchAlgorithmException
{
    for(String hash : hashes)
        hash = hash.toLowerCase(); //make sure the hashes are in lower case for compatability reasons
    ArrayList<String> originalHashes = new ArrayList<>(hashes); //copy the list of hashes
    for(int k = chainlength; k > 0; k--) 
    {
        //call function to check if hashes are any of the final hash values in the table
        checkForHash(hashes, originalHashes); 
        ArrayList<String> temporaryHashes = new ArrayList<>(hashes);
        for(int i = k; i <= chainlength; i++) //for every iteration, loop from the current k to the end of the chain
        {
            //loop through each hash in the list, reducing and rehashing it to get the next step in the chain
            for(int h = 0; h < temporaryHashes.size(); h++)
                temporaryHashes.set(h, sha1(reduce(temporaryHashes.get(h), stringlength, i))); 
            //check for the new hash in the list of final hashes
            checkForHash(temporaryHashes, originalHashes);
        }
    }       
} 
    static void checkForHash(ArrayList<String> hashes, ArrayList<String> originalHashes) throws NoSuchAlgorithmException
    {
        for(String key : rainbow.keySet()) //loop through each final hash of the chains in the table
        {
            for(int h = 0; h < hashes.size(); h++)  //loop through the hashes to be cracked
                if(hashes.get(h).equals(key))  //if the hash to be cracked and final hash match, loop
                {                              //through the chain to find the inital plaintext
                    String plaintext = rainbow.get(key);
                    hashes.set(h, sha1(plaintext));
                    for(int i = 1; i < chainlength + 1; i++)
                    {
                        if(hashes.get(h).equals(originalHashes.get(h)))
                        {
                            if(!cracked.check(originalHashes.get(h)))
                                cracked.add(System.currentTimeMillis() - starttime, originalHashes.get(h), plaintext); //store the plaintext and timestamp for 
                        }                                                                                              //cracked hashes in the cracked class
                        plaintext = reduce(hashes.get(h), stringlength, i);
                        hashes.set(h, sha1(plaintext));
                    }
                }
        }
    }
    //generate chains up to table size
    static void generateTable(int slength) throws NoSuchAlgorithmException, IOException
    {
        for(int i = 0; i < tablesize; i++)
        {
            System.out.println("Generating chain " + i + "...");
            generateChain(getString(charset, slength));
        }
    }
    
    //repeatedly hash and reduce a plaintext until chainlength
    //then store the final hash and initial plaintext in the table
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
    
    //get a new random string of length maxlength using characters from set
    static String getString(char[] set, int maxlength)
    {
        String s = "";
        for(int i = 0; i < maxlength; i++)
            s += set[random.nextInt(set.length)];
        return s;
    }
    
    //reduce hash to a plaintext string
    static String reduce(String hash, int length, int step)
    {
        BigInteger bi = new BigInteger(hash, 16);   //turn hash into BI to allow maths to be applied
        bi = bi.multiply(BigInteger.valueOf(step)); //multiply by the step value. this is done so that the reduce function can produce more varied results along the chain
        bi = bi.mod(prime);                         //mod by the large prime to get an even distribution of values
        hash = bi.toString();                       //return the BI to a string to allow us to take characters from it
        String reduced = "";
        int x = 0;
        while(true)
            for(int i = 0; i < hash.length(); i++)
                if(reduced.length() == length)      //once a string of sufficient length is produced, return it
                    return reduced;
                else
                {
                    step %= hash.length();                                  //make sure that step is within the bounds of the BI string
                    step = step < 0 ? step + hash.length() : step;          //if step is negative, make it positive
                    x = hash.charAt(i) + hash.charAt(step % hash.length()); //use characters from the BI string to pick an index in the character set
                    x %= charset.length;                                    //make sure the index is within the character set bounds
                    reduced += charset[x];                                  //add a character from the character set to the new plaintext
                }
    }
    
    //used to hash plaintexts to sha1
    static String sha1(String password) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] bytes = md.digest(password.getBytes()); //apply sha1 to the password provided and store the output in a byte array
        String toReturn = "";
        for(int i = 0; i < bytes.length; i++)
            toReturn += Integer.toString((bytes[i]&0xff) + 0x100, 16).substring(1); //output to string as hex instead of decimal 
        return toReturn;
    }
}