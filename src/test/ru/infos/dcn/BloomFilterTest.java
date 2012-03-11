package ru.infos.dcn;
/*
 *  Date: 10.03.12
 *   Time: 16:55
 *   Author: 
 *      Artemij Chugreev 
 *      artemij.chugreev@gmail.com
 */

import org.junit.Assert;
import org.junit.Test;
import ru.infos.dcn.exception.FilterFullException;

import java.security.NoSuchAlgorithmException;

public class BloomFilterTest extends Assert {

    @Test
    public void addElementTest() throws NoSuchAlgorithmException, FilterFullException {
        {
            BloomFilter bloomFilter = new BloomFilter(20, 0.01, null, true);
            bloomFilter.put("hello");
            bloomFilter.put("world");
        }
    }

    @Test
    public void existElementTest() throws NoSuchAlgorithmException, FilterFullException {
        {
            BloomFilter bloomFilter = new BloomFilter(3, 0.01, null, true);
            bloomFilter.put("hello");
            bloomFilter.put("world");
            assertEquals(bloomFilter.exist("hello"), true);
            assertEquals(bloomFilter.exist("world"), true);
            assertEquals(bloomFilter.exist("bye"), false);
        }
        {
            BloomFilter bloomFilter = new BloomFilter(3, 0.01, null, true);
            bloomFilter.put(1);
            bloomFilter.put(2);
            assertEquals(bloomFilter.exist(1), true);
            assertEquals(bloomFilter.exist(2), true);
            assertEquals(bloomFilter.exist(5), false);
        }
        {
            BloomFilter bloomFilter = new BloomFilter(3, 0.01, null, true);
            Object object = new Object();
            bloomFilter.put(object);
            assertEquals(bloomFilter.exist(object), true);
            assertEquals(bloomFilter.exist(new Object()), false);
        }
    }

//    this test might be failed, because probability of error is 0.5
    @Test
    public void existElementFailedTest() throws NoSuchAlgorithmException, FilterFullException {
        {
            BloomFilter bloomFilter = new BloomFilter(2, 0.5, null, true);
            bloomFilter.put(1);
            bloomFilter.put(2);
            assertEquals(bloomFilter.exist(1), true);
            assertEquals(bloomFilter.exist(2), true);
            assertEquals(bloomFilter.exist(5), false);
        }
    }

}
