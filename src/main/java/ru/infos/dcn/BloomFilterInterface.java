package ru.infos.dcn;

import ru.infos.dcn.exception.FilterFullException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface BloomFilterInterface {
//    put element into filter
    public void put(Object object) throws NoSuchAlgorithmException, FilterFullException;
//    does element isExist in filter
    public boolean isExist(Object object) throws NoSuchAlgorithmException;
}
