package com.cowave.zoo.dtm.client.impl;

/**
 *
 * @author shanhuiming
 *
 */
@FunctionalInterface
public interface BarrierOperator<T extends Dtm> {

    boolean accept(T t) throws Exception;
}
