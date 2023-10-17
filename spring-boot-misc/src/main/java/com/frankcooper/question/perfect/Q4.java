package com.frankcooper.question.perfect;


import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class Q4 {

    static LinkedHashMap<String, Set<Long>> map = new LinkedHashMap<>();


    public static void main(String[] args) {
        process();
    }

    public static void process() {
        Q3.process();
        for (Q3.LoginInfo e : Q3.results) {
            String key = String.format("%s_%s", e.getLoginTime(), e.getProvince());
            Set<Long> set = map.getOrDefault(key, new HashSet<>());
            set.add(e.getAccountId());
            map.put(key, set);
        }
        printSamples();
    }

    public static void printSamples() {
        int i = 0;
        for (String key : map.keySet()) {
//            if (i++ > 10) {
//                break;
//            }
            String[] arr = key.split("_");
            System.out.printf("%s,%s,%d\n", arr[0], arr[1], map.get(key).size());
        }
    }
}
