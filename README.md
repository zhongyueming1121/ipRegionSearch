# ipRegionSearch
一个高性能的低内存消耗的离线IP溯源工具

生成文件小：280万条IP数据,生成db.gz文件仅15.8MB

现只有内存模式，后续增加内存+磁盘模式，B+Tree索引，压缩内存使用

github: [ipRegionSearch](https://github.com/zhongyueming1121/ipRegionSearch "ipRegionSearch")

提供一个包含大量IP地理位置的db文件

功能&执行步骤：
 - MakeMain.java ---生成db数据
 - IPSearcher.java ---查找IP

项目中使用：
 - 将search_v1.2.0.db.gz放入resources文件夹下
 - 将代码整合到项目，或者将本项目打成jar包使用

280万条IP数据内存占用和耗时:
```$xslt
ipRegions humanSizeOf:11 MB
dataRegion humanSizeOf:1.7 MB
ipRegions humanSizeOf:11 MB
ipRegionLen humanSizeOf:5.5 MB
+---Done, search complished
+---Statistics, Error count = 0, Total line = 2882675, Fail ratio = 0.0%
+---Cost time: 2967ms
```

**索引示意图：**

[![索引示意图](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/indexing3.png "索引示意图")](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/indexing3.png "索引示意图")



**结构示意图：**

[![结构示意图](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/ipsearch.png "结构示意图")](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/ipsearch.png "结构示意图")


pom依赖: 
```$xslt
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.10</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>2.11.0</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.62</version>
    </dependency>
 </dependencies>
```
