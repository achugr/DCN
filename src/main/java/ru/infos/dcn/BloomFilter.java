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
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;

/**
 class implements bloom filter (http://en.wikipedia.org/wiki/Bloom_filter)
 */
public class BloomFilter {
//    if set true - you will get exception when you put element, but filter is full
    private boolean safeFilter;
//  byte filter
    private BitSet filter;
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
     This is minimal set of arguments, that allows configure bloom filter.
     @param numOfElements estimated number of elements, which will be added in bloom filter
     @param falsePositiveProbability probability of malfunction
     @param messageDigest hash function, you want to use
     @param safeFilter true - if you will get exception when you put element, but filter is full, false - otherwise
     @throws NoSuchAlgorithmException
     */
    public BloomFilter(int numOfElements, double falsePositiveProbability, MessageDigest messageDigest, boolean safeFilter
            ) throws NoSuchAlgorithmException {
        if(falsePositiveProbability < Double.MIN_VALUE){
            falsePositiveProbability = Double.MIN_VALUE;
        }
        estimatedNumberOfElements = numOfElements;
        this.safeFilter = safeFilter;
//        number of hash functions to provide probability of false positive
//        2^(-k) - probability of false-positive, k - number of hash-functions
//        => k = (int)(ln(1/probability of FP)/ln2) + 1
        hashFunctionsNumber = (int) (Math.log(1/falsePositiveProbability)/LN_2) + 1;
        log.info("number of hash-functions: " + hashFunctionsNumber);
//      size of filter to provide probability of false positive
//      size of filter = (int) -(number of elements * ln(false positive probability))/(ln(2))^2 + 1
        filterSize = - (int)((numOfElements * Math.log(falsePositiveProbability)/(LN_2 * LN_2))) + 1;
        log.info("size of filter: " + filterSize);

        filter = new BitSet(filterSize);
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
     Constructor for jedi ;-) Be careful
     @param numOfElements estimated number of elements, which will be added in bloom filter
     @param hashFunctionNumber number of hash-function
     @param filterSize size of bloom filter
     @param messageDigest hash function, you want to use
     @param safeFilter true - if you will get exception when you put element, but filter is full, false - otherwise
     @throws NoSuchAlgorithmException
     */
    public BloomFilter(int numOfElements, int hashFunctionNumber,
            int filterSize, MessageDigest messageDigest,
            boolean safeFilter) throws NoSuchAlgorithmException {
        estimatedNumberOfElements = numOfElements;
        this.safeFilter = safeFilter;
        this.hashFunctionsNumber = hashFunctionNumber;
        log.info("number of hash-functions: " + hashFunctionsNumber);

        log.info("size of filter: " + filterSize);

        filter = new BitSet(filterSize);
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
            filter.set(hash(object, keyWord));
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
            if (!filter.get(hash(object, keyWord))){
                return false;
            }
        }
        return true;
    }

    /**
     print current state of bloom filter in log
     like so: {1, 4, 6, 10, ..., i, ..., n}
     where i - position of true value in BitSet
     */
    public void print(){
        log.info("filter : " + filter.toString());
    }
}                         
