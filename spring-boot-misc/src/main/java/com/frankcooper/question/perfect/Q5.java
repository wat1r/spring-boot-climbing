package com.frankcooper.question.perfect;


import java.util.*;

public class Q5 {

    static Map<String, List<Q3.LoginInfo>> map = new HashMap<>();

    public static void main(String[] args) {
        process();
    }

    public static void process() {
        Q3.process();
        List<Q3.LoginInfo> results = Q3.results;
        for (Q3.LoginInfo result : results) {
            List<Q3.LoginInfo> list = map.getOrDefault(result.getProvince(), new ArrayList<>());
            list.add(result);
            map.put(result.getProvince(), list);
        }
        for (String province : map.keySet()) {
            List<Q3.LoginInfo> candidates = map.get(province);
            candidates.sort(Comparator.comparing(Q3.LoginInfo::getLoginTime));
            System.out.printf("%s,", province);
            candidates.stream().limit(3).forEach(s -> System.out.printf("%d,%s", s.getAccountId(), s.getLoginTime()));
            System.out.println();
        }
    }
}
