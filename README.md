# lab1_bigdata
大数据实验一：Linux、HDFS Shell与MapReduce编程实验
本仓库存放大数据实验1的MapReduce Java源代码，实现3个MapReduce任务：数据去重、整数全局排序、祖孙关系挖掘。

## 仓库文件清单
| 文件名 | 功能说明 |
| ---- | ---- |
| Deduplicate.java | MapReduce实现文本行去重任务 |
| SortRank.java | MapReduce实现整数全局排序，并输出数值位次 |
| GrandRelation.java | MapReduce挖掘祖孙关系，输入父子表，输出(孙,祖) |
| README.md | 项目说明文档 |

## 环境依赖
- Hadoop 3.x
- JDK 1.8
- Hadoop MapReduce 依赖包

## 编译方法
```bash
# 配置hadoop classpath
export HADOOP_CP=`hadoop classpath`
# 编译java源码
javac -cp $HADOOP_CP *.java
# 打包jar
jar cf lab1.jar *.class
