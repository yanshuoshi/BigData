package com.yss.service.commons;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.File;

public interface ConfigurationPersistenceService {


  /**
   * 获取文件列表
   */
  public void persistenceEntrance();

  /**
   * 解析单个文件
   *
   * @param file 文件名
   * @param zkClient zkclient
   */
  public void persistenceFile(File file, ZooKeeper zkClient)
      throws KeeperException, InterruptedException;

  /**
   * 初始化global配置
   */
  public void persistenceGlobal(String path, JSONObject jsonGlobal, ZooKeeper zkClient);

  /**
   * 初始化其他配置
   *
   * @param path 节点路径
   * @param zkClient zkclient
   */
  public void persistenceData(String path, String model, String value, ZooKeeper zkClient);

}
