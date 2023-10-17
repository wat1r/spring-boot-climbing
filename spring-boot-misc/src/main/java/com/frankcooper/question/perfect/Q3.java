package com.frankcooper.question.perfect;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Q3 {

    public static TreeMap<Long, String> tm = new TreeMap<>();
    public static List<LoginInfo> results = new ArrayList<>();
    private static final String COMMA = ",";

    public static void main(String[] args) {
        process();
        //3.打印
        printSamples();
    }

    public static void process() {
        //1.转换成treeMap，实际生产环境可以采用redis方式存储
        getIpChinaMap();
        //2.查询
        getIpLocation();
    }

    public static void getIpLocation() {
        try {
            List<String> loginDataList = FileUtils.readLines(new File("D:\\Dev\\Data\\login_data.csv"), Charset.defaultCharset());
            int i = 0;
            for (String line : loginDataList) {
                if (i++ == 0) {
                    continue;
                }
                String[] arr = line.split(COMMA);
                Long ipLong = ipToNumber(arr[2]);
                Map.Entry<Long, String> floor = tm.floorEntry(ipLong);//小于等于给定key的最大节点
                Map.Entry<Long, String> ceiling = tm.ceilingEntry(ipLong);//大于等于给定key的最小节点
                if (floor != null && ceiling != null && floor.getValue().equals(ceiling.getValue())) {
                    String[] vs = floor.getValue().split(COMMA);
                    results.add(new LoginInfo(arr[0], Long.parseLong(arr[1]), arr[2], vs[0], vs[1]));
                } else {
                    String[] vs = floor == null ? ceiling.getValue().split(COMMA) : floor.getValue().split(COMMA);
                    results.add(new LoginInfo(arr[0], Long.parseLong(arr[1]), arr[2], vs[0], vs[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getIpChinaMap() {
        try {
            List<String> ipChinaList = FileUtils.readLines(new File("D:\\Dev\\Data\\ip_china.csv"), Charset.defaultCharset());
            int i = 0;
            for (String line : ipChinaList) {
                if (i++ == 0) {
                    continue;
                }
                String[] arr = line.split(COMMA);
                Long ipStart = Long.parseLong(arr[2]);
                Long ipEnd = Long.parseLong(arr[3]);
                String addr = arr[4] + "," + (arr.length >= 6 ? arr[5] : "");
                tm.put(ipStart, addr);
                tm.put(ipEnd, addr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Long ipToNumber(String ip) {
        Long ips = 0L;
        String[] numbers = ip.split("\\.");
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(numbers[i]);
        }
        return ips;
    }

    public static void printSamples() {
        AtomicInteger i = new AtomicInteger();
        results.forEach(e -> {
            if (i.getAndIncrement() > 10) {
                return;
            }
            System.out.println(e);
        });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    static class LoginInfo {
        private String loginTime;
        private Long accountId;
        private String ip;
        private String country;
        private String province;
    }


}
