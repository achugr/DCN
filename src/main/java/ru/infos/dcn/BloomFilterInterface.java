package ru.infos.dcn;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface BloomFilterInterface {
//    put element into filter
    public void put(Object object);
//    does element exist in filter
    public boolean exist(Object object);
//    constructor for jedi ;-) be careful
    public void BloomFilter(int numOfElements, int hashFunctionNumber,  int filterSize, MessageDigest messageDigest,
            boolean safeFilter) throws NoSuchAlgorithmException;
//    simple constructor
    public void BloomFilter(int numOfElements, double falsePositiveProbability, MessageDigest messageDigest, boolean safeFilter
                           ) throws NoSuchAlgorithmException;

}
