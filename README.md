# ipRegionSearch
一个高性能的低内存消耗的离线IP溯源工具

生成文件小：280万条IP数据,生成db.gz文件仅18.3MB

现只有内存模式，后续增加内存+磁盘模式，B+Tree索引，压缩内存使用

github: [ipRegionSearch](https://github.com/zhongyueming1121/ipRegionSearch "ipRegionSearch")

版权原因，不提供完整ip库

功能&执行步骤：
 - Adapter.java ---转换数据
 - DatMaker.java ---生成db数据
 - Searcher.java ---查找IP

280万条IP数据:
```$xslt
ipRegions humanSizeOf:11 MB
dataRegion humanSizeOf:1.7 MB
ipRegions humanSizeOf:11 MB
ipRegionLen humanSizeOf:5.5 MB
+---Done, search complished
+---Statistics, Error count = 0, Total line = 2882675, Fail ratio = 0.0%
+---Cost time: 2967ms
```

索引示意图：

[![索引示意图](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/indexing.png "索引示意图")](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/indexing.png "索引示意图")


结构示意图：

[![结构示意图](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/ipsearch.png "结构示意图")](https://github.com/zhongyueming1121/ipRegionSearch/blob/master/doc/ipsearch.png "结构示意图")


pom依赖: 
```$xslt
 <dependencies>
         <!--for log-->
         <dependency>
             <groupId>org.projectlombok</groupId>
             <artifactId>lombok</artifactId>
             <version>1.18.10</version>
             <scope>provided</scope>
         </dependency>
         <!--for log-->
         <dependency>
             <groupId>org.apache.logging.log4j</groupId>
             <artifactId>log4j-slf4j-impl</artifactId>
             <version>2.11.0</version>
         </dependency>
         <!--for testing-->
         <dependency>
             <groupId>org.apache.lucene</groupId>
             <artifactId>lucene-core</artifactId>
             <version>8.3.0</version>
         </dependency>
         <!--required-->
         <dependency>
             <groupId>com.alibaba</groupId>
             <artifactId>fastjson</artifactId>
             <version>1.2.62</version>
         </dependency>
     </dependencies>
```
