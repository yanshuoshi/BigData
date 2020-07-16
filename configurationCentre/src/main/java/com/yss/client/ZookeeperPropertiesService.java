package com.yss.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yss.model.Result2Model;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ZookeeperPropertiesService
 * @Description TODO
 **/
@Slf4j
public class ZookeeperPropertiesService {

  private ZooKeeper zkClient;

  public ZooKeeper getZkClient() {
    return zkClient;
  }

  public void initializationZkClient(String zookeeperHost, Watcher watcher) {
    try {
      zkClient = new ZooKeeper(zookeeperHost, 2000, watcher);
    } catch (IOException e) {
      log.error("", e);
    }
  }

  public void closeZkClinet(ZooKeeper zkClient) {
    try {
      zkClient.close();
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }


  /**
   * 读取receiver配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getReceiverProperties(String basePath,
      ZooKeeper zkClient) {
    Result2Model<String, Map<String, Object>> result = null;

    String receiverZnode = basePath + "/receiver";

    List<String> receivers = ZooKeeperOperatingService.childrenZnode(receiverZnode, zkClient);
    for (String receiverOne : receivers) {
      Stat stat = ZooKeeperOperatingService.znodeStat(receiverZnode + "/" + receiverOne, zkClient);
      if (stat.getNumChildren() == 0) {
        Map<String, Object> kafkaProperties = readProperties(receiverZnode + "/" + receiverOne,
            zkClient);
        result = new Result2Model<String, Map<String, Object>>(receiverZnode + "/" + receiverOne,
            kafkaProperties);
        break;
      }
    }
    return result;
  }

  /**
   * 读取cleaner配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getCleanerProperties(String basePath,
      ZooKeeper zkClient) {
    String cleanerZnode = basePath + "/cleaner";
    Map<String, Object> cleanerProperties = readProperties(cleanerZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        cleanerZnode, cleanerProperties);
    return result;
  }

  /**
   * 读取threat配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getThreatProperties(String basePath,
      ZooKeeper zkClient) {
    String threatZnode = basePath + "/threat";
    Map<String, Object> threatProperties = readProperties(threatZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        threatZnode, threatProperties);
    return result;
  }

  /**
   * 读取transfer配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getTransferProperties(String basePath,
      ZooKeeper zkClient) {
    String transferZnode = basePath + "/transfer";
    Map<String, Object> transferProperties = readProperties(transferZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        transferZnode, transferProperties);
    return result;
  }

  /**
   * 读取dev-status配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getDevProperties(String basePath,
      ZooKeeper zkClient) {
    String devZnode = basePath + "/dev";
    Map<String, Object> devProperties = readProperties(devZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        devZnode, devProperties);
    return result;
  }

  /**
   * 读取webservice配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getAlarmServiceProperties(String basePath,
      ZooKeeper zkClient) {
    String webserviceZnode = basePath + "/alarmservice";
    Map<String, Object> webserviceProperties = readProperties(webserviceZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        webserviceZnode, webserviceProperties);
    return result;
  }

  /**
   * 读取webservice配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getWebserviceProperties(String basePath,
      ZooKeeper zkClient) {
    String webserviceZnode = basePath + "/webservice";
    Map<String, Object> webserviceProperties = readProperties(webserviceZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        webserviceZnode, webserviceProperties);
    return result;
  }

  /**
   * 读取hunter配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getHunterProperties(String basePath,
      ZooKeeper zkClient) {
    String hunterZnode = basePath + "/hunter";
    Map<String, Object> hunterProperties = readProperties(hunterZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        hunterZnode, hunterProperties);
    return result;
  }

  /**
   * 读取kafka配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getKafkaProperties(String basePath,
      ZooKeeper zkClient) {
    String kafkaZnode = basePath + "/global/kafka";
    Map<String, Object> kafkaProperties = readProperties(kafkaZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        kafkaZnode, kafkaProperties);
    return result;
  }

  /**
   * 读取database 配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getDatabaseProperties(String basePath,
      ZooKeeper zkClient) {
    String databaseZnode = basePath + "/global/database";
    Map<String, Object> databaseProperties = readProperties(databaseZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        databaseZnode, databaseProperties);
    return result;
  }

  /**
   * 读取hbase配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getHbaseProperties(String basePath,
      ZooKeeper zkClient) {
    String hbaseZnode = basePath + "/global/hbase";
    Map<String, Object> hbaseProperties = readProperties(hbaseZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        hbaseZnode, hbaseProperties);
    return result;
  }

  /**
   * 读取elasticsearch配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getElasticsearchProperties(String basePath,
      ZooKeeper zkClient) {
    String esZnode = basePath + "/global/elasticsearch";
    Map<String, Object> esProperties = readProperties(esZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        esZnode, esProperties);
    return result;
  }

  /**
   * 读取redis配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getRedisProperties(String basePath,
      ZooKeeper zkClient) {
    String redisZnode = basePath + "/global/redis";
    Map<String, Object> redisProperties = readProperties(redisZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        redisZnode, redisProperties);
    return result;
  }

  /**
   * 读取hdfs配置
   *
   * @param basePath 根目录
   * @param zkClient zkclient
   */
  public Result2Model<String, Map<String, Object>> getHdfsProperties(String basePath,
      ZooKeeper zkClient) {
    String hdfsZnode = basePath + "/global/hdfs";
    Map<String, Object> hdfsProperties = readProperties(hdfsZnode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model<String, Map<String, Object>>(
        hdfsZnode, hdfsProperties);
    return result;
  }


  /**
   * 读取节点内容
   *
   * @param kafkaZnode 节点路径
   * @param zkClient zkclient
   */
  private Map<String, Object> readProperties(String kafkaZnode, ZooKeeper zkClient) {

    Map<String, Object> mapResult = new HashMap<>();
    if (ZooKeeperOperatingService.existsZnode(kafkaZnode, zkClient)) {
      byte[] dataZnode = ZooKeeperOperatingService.getDataZnode(kafkaZnode, zkClient);
      if (dataZnode.length > 0) {
        String pro = new String(dataZnode);
        Map<String, Object> stringObjectMap = JSON
            .parseObject(pro, new TypeReference<Map<String, Object>>() {
            });
        mapResult.putAll(stringObjectMap);
      }
    } else {
      log.error("not find znode [{}]", kafkaZnode);
    }
    return mapResult;
  }


  /**
   * 创建链式子节点
   *
   * @param znode node内容
   * @param data 数据内容
   * @param zkClient zkclient
   */
  public void createEphemeralSequentialZnode(String znode, String data, ZooKeeper zkClient) {
    try {
      zkClient.create(znode, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    } catch (KeeperException e) {
      log.error("", e);
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  public Result2Model<String, Map<String, Object>> getGlobalProperties(String basePath, ZooKeeper zkClient, String pluginName) {
    String zkNode = Paths.get(basePath, "global", pluginName).toString();
    Map<String, Object> pluginProperties = this.readProperties(zkNode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model(zkNode, pluginProperties);
    return result;
  }

  public Result2Model<String, Map<String, Object>> getSingleModelProperties(String basePath, ZooKeeper zkClient, String modelName) {
    String zkNode = Paths.get(basePath, modelName).toString();
    Map<String, Object> modelProperties = this.readProperties(zkNode, zkClient);
    Result2Model<String, Map<String, Object>> result = new Result2Model(zkNode, modelProperties);
    return result;
  }
}
