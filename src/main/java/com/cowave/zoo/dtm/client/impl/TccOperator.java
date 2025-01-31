package com.cowave.zoo.dtm.client.impl;

/**
 *
 * @author shanhuiming
 *
 */
@FunctionalInterface
public interface TccOperator<T extends Dtm> {

    void accept(T t) throws Exception;
}
