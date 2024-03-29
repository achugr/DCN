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
            BloomFilter bloomFilter = new BloomFilter(20, 0.01, null, true);
            bloomFilter.put("hello");
            bloomFilter.put("world");
            bloomFilter.print();
    }

    @Test
    public void existElementTest() throws NoSuchAlgorithmException, FilterFullException {
        {
            BloomFilter bloomFilter = new BloomFilter(3, 0.01, null, true);
            bloomFilter.put("hello");
            bloomFilter.put("world");
            assertEquals(bloomFilter.isExist("hello"), true);
            assertEquals(bloomFilter.isExist("world"), true);
            assertEquals(bloomFilter.isExist("bye"), false);
        }
        {
            BloomFilter bloomFilter = new BloomFilter(3, 0.01, null, true);
            bloomFilter.put(1);
            bloomFilter.put(2);
            assertEquals(bloomFilter.isExist(1), true);
            assertEquals(bloomFilter.isExist(2), true);
            assertEquals(bloomFilter.isExist(5), false);
        }
        {
            BloomFilter bloomFilter = new BloomFilter(3, 0.01, null, true);
            Object object = new Object();
            bloomFilter.put(object);
            assertEquals(bloomFilter.isExist(object), true);
            assertEquals(bloomFilter.isExist(new Object()), false);
        }
    }

    @Test
    public void filterFullExceptionTest() throws NoSuchAlgorithmException {
        BloomFilter bloomFilter = new BloomFilter(3, 0.01, null, true);
        boolean exceptionThrown = false;
        try {
            bloomFilter.put(1);
            bloomFilter.put(2);
            bloomFilter.put(3);
            bloomFilter.put(4);
        } catch (FilterFullException e) {
            exceptionThrown = true;
            e.printStackTrace(); 
        }
        assertEquals(exceptionThrown, true);
    }

}
