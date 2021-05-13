# common-export
基于[easyexcel](https://www.yuque.com/easyexcel) 封装的一个通用的excel导出  
只需关心数据查询方法和导出的表头格式  
使用spring框架后增加导出类型无需修改原有代码  

## 非Spring Demo

````xml
<!--Step 1. Add the JitPack repository to your build file-->
        <repositories>
            <repository>
                <id>jitpack.io</id>
                <url>https://jitpack.io</url>
            </repository>
        </repositories>
<!--Step 2. Add the dependency-->
        <dependency>
            <groupId>com.github.q1sj</groupId>
            <artifactId>common-export-excel</artifactId>
            <version>3.0.2</version>
        </dependency>
````
[demo代码](https://github.com/q1sj/common-export/blob/master/src/test/java/com/qsj/export/ExportTest.java)
## Spring Boot Demo
````xml
<!--Step 1. Add the JitPack repository to your build file-->
        <repositories>
            <repository>
                <id>jitpack.io</id>
                <url>https://jitpack.io</url>
            </repository>
        </repositories>
<!--Step 2. Add the dependency-->
        <dependency>
    	    <groupId>com.github.q1sj</groupId>
    	    <artifactId>export-spring-boot-starter</artifactId>
    	    <version>3.0.2</version>
    	</dependency>
````
[demo代码](https://github.com/q1sj/common-export-excel-demo)
