package com.frankcooper.mdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author: wangzhou(Frank Cooper)
 * @date: 2022/7/28 16:40
 * @description:
 */
public class SimpleMDCDemo {
    public static void main(String[] args) {
        // You can put values in the MDC at any time. Before anything else
        // we put the first name
        MDC.put("first", "Dorothy");

//    [ SNIP ]

        Logger logger = LoggerFactory.getLogger(SimpleMDCDemo.class);
        // We now put the last name
        MDC.put("last", "Parker");

        // The most beautiful two words in the English language according
        // to Dorothy Parker:
        logger.info("Check enclosed.");
        logger.debug("The most beautiful two words in English.");

        MDC.put("first", "Richard");
        MDC.put("last", "Nixon");
        logger.info("I am not a crook.");
        logger.info("Attributed to the former US president. 17 Nov 1973.");

    }
}
