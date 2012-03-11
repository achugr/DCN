package ru.infos.dcn;
/*
 *  Date: 25.02.12
 *   Time: 00:11
 *   Author: 
 *      Artemij Chugreev 
 *      artemij.chugreev@gmail.com
 */

import org.apache.log4j.Logger;
import ru.infos.dcn.exception.FilterFullException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 class implements bloom filter (http://en.wikipedia.org/wiki/Bloom_filter)
 */
public class BloomFilter {
//    if set true - you will get exception when you put element, but filter is full
    private boolean safeFilter;
//  byte filter
    private byte [] filter;
//    size of filter
    private int filterSize;
//    elements counter
    private int elementCounter = 0;
//    estimated number of elements in filter
    private int estimatedNumberOfElements;
//    random keys for generating hash-functions
    private HashSet<String> randomKeys;
//    number of hash-functions
    private int hashFunctionsNumber;
//    digest - hash-function
    private MessageDigest messageDigest;
//    ln(2)
    private static final double LN_2 = 0.6931;
//    logger
    private static final Logger log = Logger.getLogger(BloomFilter.class);

    /**
     Constructor for bloom filter. Size of filter and number of hash-functions evaluated automatically.
     @param numOfElements estimated number of elements, which will be added in bloom filter
     @param errorProbability probability of malfunction
     @param messageDigest hash function, you want to use
     @param safeFilter true - if you will get exception when you put element, but filter is full, false - otherwise
     @throws NoSuchAlgorithmException
     */
    public BloomFilter(int numOfElements, double errorProbability, MessageDigest messageDigest, boolean safeFilter
            ) throws NoSuchAlgorithmException {
        estimatedNumberOfElements = numOfElements;
        this.safeFilter = safeFilter;
//        number of hash functions to provide probability of malfunction
        hashFunctionsNumber = (int) (Math.log(1/errorProbability)/LN_2) + 1;
        log.info("number of hash-functions: " + hashFunctionsNumber);
//      size of filter to provide probability of malfunction
        filterSize = (int)((numOfElements * hashFunctionsNumber)/LN_2) + 1;
        log.info("size of filter: " + filterSize);

        filter = new byte[filterSize];
        randomKeys = new HashSet<String>();

//        use MD5 as default hash-function
        if(messageDigest == null){
            this.messageDigest = MessageDigest.getInstance("MD5");
        } else {
            this.messageDigest = messageDigest;
        }
//        set random keys
        this.setRandomKeys();
    }

    /**
     fill hash-set of random keys
     */
    private void setRandomKeys(){
        Random random =  new Random();
        for(int i=0; i<hashFunctionsNumber; i++){
            Integer r1 = random.nextInt();
            randomKeys.add(r1.toString());
        }
    }

    /**
     evaluate position of object in bloom filter, that provide this hash function
     @param object
     @param keyWord
     @return
     */
    private int hash(Object object, String keyWord) {
        byte [] hash = messageDigest.digest((object.toString() + keyWord).getBytes());
        int sum = 0;
        for(int i=0; i<hash.length; i++){
            sum+=hash[i];
        }
        return Math.abs(sum) % filterSize;
    }

    /**
     put object in bloom filter
     @param object you want add to filter
     @throws NoSuchAlgorithmException
     */
    public void put(Object object) throws NoSuchAlgorithmException, FilterFullException {
//        go on all hash-functions
        for (String keyWord : randomKeys){
//            set 1 in correct position
            filter [hash(object, keyWord)] = 1;
        }
        if(elementCounter > estimatedNumberOfElements){
            throw new FilterFullException();
        }
        elementCounter++;
    }

    /**
     verify, is this object in bloom-filter
     @param object object, you want to verify
     @return true - exist, false - otherwise
     @throws NoSuchAlgorithmException
     */
    public boolean exist(Object object) throws NoSuchAlgorithmException {
        for(String keyWord : randomKeys){
            if (filter[hash(object, keyWord)] == 0){
                return false;
            }
        }
        return true;
    }

    /**
     print current state of bloom filter in log
     */
    public void print(){
        log.info("filter : " + Arrays.toString(filter));
    }

    public static void main(String [] args) throws NoSuchAlgorithmException {
//        use MD5 as hash-function
        MessageDigest md = MessageDigest.getInstance("MD5");
        BloomFilter bloomFilter = new BloomFilter(2, 0.01, md, true);
        String [] strings = {"hello, world", "let it be", "When I find myself in times of trouble", "60 revolutions"};
        try {
//            put strings in bloom filter
            for(String str : strings){
                bloomFilter.put(str);
                bloomFilter.print();
            }
            for(String str : strings){
                System.out.println("does string " +str+ " exist ? :" + bloomFilter.exist(str));
            }
            System.out.println("does string exist ? :" + bloomFilter.exist("i've just seen a face"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FilterFullException e){
            log.warn("you put in filter more elements, than filter can save with allowable error", e);
        }
    }
}                         
