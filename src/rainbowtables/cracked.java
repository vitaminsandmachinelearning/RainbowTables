/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rainbowtables;

/**
 *
 * @author Jake
 */
public class cracked {
    static long[] times;
    static String[] hashes;
    static String[] plaintexts;
    static int index = 0;
    public static void setup(int count)
    {
        times = new long[count];
        hashes = new String[count];
        plaintexts = new String[count];
    }
    
    public static boolean check(String hash)
    {
        for(int i = 0; i < hashes.length; i++)
            if(hash.equals(hashes[i]))
                return true;
        return false;
    }
    
    public static void add(long time, String hash, String plaintext)
    {
        times[index] = time;
        hashes[index] = hash;
        plaintexts[index] = plaintext;
        index++;
    }
    
    public static void printAll()
    {
        for(int i = 0; i < index; i++)
            System.out.println(String.format("%s is %s. Found in %dms.",hashes[i],plaintexts[i],times[i]));
    }
}
