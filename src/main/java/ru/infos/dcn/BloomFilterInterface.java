package ru.infos.dcn;

import ru.infos.dcn.exception.FilterFullException;

import java.security.NoSuchAlgorithmException;

public interface BloomFilterInterface {
//    put element into filter
    public void put(Object object) throws NoSuchAlgorithmException, FilterFullException;
//    does element exist in filter
    public boolean exist(Object object) throws NoSuchAlgorithmException ;
}
