package com.frankcooper.rookie.processor.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

public class FileProcessor {


    public static void main(String[] args) {
        FileProcessor processor = new FileProcessor();
//        processor.removeDuplicateFile();
        processor.removeDuplicateFileV2();

    }

    ///Users/frankcooper/Data/08huawei_mate10/1Pictures/.DS_Store
    //.DS_Store
    //file.getPath()
    ///Users/frankcooper/Data/08huawei_mate10/1Pictures/.DS_Store

    private void removeDuplicateFile() {
//        String path = "/Users/frankcooper/Data/08huawei_mate10/1Pictures";
        String path = "/Users/frankcooper/Data/08huawei_mate10";
        Map<String, String> fileMap = new TreeMap<>();
        Collection<File> files = FileUtils.listFiles(new File(path), null, true);
        int moveFileCount = 0;
        for (File sourceFile : files) {
            if (sourceFile.getName().endsWith("DS_Store")) {
                continue;
            }
            String parentPath = sourceFile.getParent();
            String sourceFileName = sourceFile.getName();
            if (fileMap.containsKey(sourceFileName)) {
                String targetFileName = "DELETE_" + sourceFileName;
                File targetFile = new File(parentPath + File.separator + targetFileName);
                try {
//                    FileUtils.moveFile(sourceFile, targetFile);
                    FileUtils.deleteQuietly(sourceFile);
                    moveFileCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                    moveFileCount--;
                }
            }
            fileMap.put(sourceFileName, sourceFile.getAbsolutePath());
//            System.out.printf("%s\n", sourceFile.getName());
        }
        List<FileStatistics> fileStatisticsList = new ArrayList<>();
        fileStatisticsList.add(FileStatistics.builder().fileType("MOVE_FILE").count(moveFileCount).build());
        fileStatisticsList.add(FileStatistics.builder().fileType("KEEP_FILE").count(files.size() - moveFileCount).build());
        fileStatisticsList.add(FileStatistics.builder().fileType("TOT_FILE").count(files.size()).build());
        String result = JSON.toJSONString(fileStatisticsList,
//                SerializerFeature.WriteMapNullValue,
//                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.PrettyFormat);
        System.out.printf("%s\n", result);

    }


///Volumes/NO NAME/personalFiles

    private void removeDuplicateFileV2() {
//        String path = "/Volumes/NO NAME/personalFiles";

        List<String> pathList = Arrays.asList("/Users/frankcooper/Data",
//                "/Volumes/NO NAME/personalFiles",
                "/Volumes/2046/ImportantFiles",
                "/Users/frankcooper/Pictures");
        int moveFileCount = 0;
        for (String path : pathList) {
            Map<String, String> fileMap = new TreeMap<>();
            System.out.printf("=======path->%s\n", path);
            Collection<File> files = FileUtils.listFiles(new File(path), null, true);
            for (File sourceFile : files) {
                if (sourceFile.getName().endsWith("DS_Store")) {
                    continue;
                }
                String parentPath = sourceFile.getParent();
                String sourceFileName = sourceFile.getName();
                if (fileMap.containsKey(sourceFileName)) {
                    String targetFileName = "DELETE_" + sourceFileName;
                    File targetFile = new File(parentPath + File.separator + targetFileName);
                    try {
//                    FileUtils.moveFile(sourceFile, targetFile);
                        System.out.printf("DELETE->%s\n", parentPath + File.separator + sourceFile.getName());
                        FileUtils.deleteQuietly(sourceFile);
                        moveFileCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        moveFileCount--;
                    }
                }
                fileMap.put(sourceFileName, sourceFile.getAbsolutePath());
//            System.out.printf("%s\n", sourceFile.getName());
            }
        }

//
//        List<FileStatistics> fileStatisticsList = new ArrayList<>();
//        fileStatisticsList.add(FileStatistics.builder().fileType("MOVE_FILE").count(moveFileCount).build());
//        fileStatisticsList.add(FileStatistics.builder().fileType("KEEP_FILE").count(files.size() - moveFileCount).build());
//        fileStatisticsList.add(FileStatistics.builder().fileType("TOT_FILE").count(files.size()).build());
//        String result = JSON.toJSONString(fileStatisticsList,
////                SerializerFeature.WriteMapNullValue,
////                SerializerFeature.WriteNullListAsEmpty,
//                SerializerFeature.PrettyFormat);
//        System.out.printf("%s\n", result);
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class FileStatistics {
        private String fileType;
        private Integer count;
    }


}
