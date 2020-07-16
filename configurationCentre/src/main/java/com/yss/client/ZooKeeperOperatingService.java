package com.yss.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ZkOperating
 * @Description TODO
 **/
@Slf4j
public class ZooKeeperOperatingService {

  /**
   * 判断znode是否存在
   * @param znode znode
   * @param zookeeper zkclient
   */
  public static boolean existsZnode(String znode, ZooKeeper zookeeper) {
    boolean result = true;
    Stat exists = znodeStat(znode, zookeeper);
    if (null == exists) {
      result = false;
    } else {
      result = true;
    }
    return result;
  }


  /**
   * 创建znode
   *
   * @param znode znode
   * @param bytes 内容
   * @param zooKeeper zkclient
   */
  public static void createZnodePersistent(String znode, byte[] bytes, ZooKeeper zooKeeper) {
    try {
      zooKeeper.create(znode, bytes, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
      log.info("create znode = [{}]", znode);
    } catch (KeeperException | InterruptedException e) {
      log.error("", e);
    }
  }

  /**
   * 更新znode中的值
   *
   * @param znode znode
   * @param bytes 内容
   * @param zookeeper zkclient
   */
  public static void setDataToZnode(String znode, byte[] bytes, ZooKeeper zookeeper) {
    try {
      zookeeper.setData(znode, bytes, -1);
    } catch (KeeperException e) {
      log.error("", e);
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  public static List<String> childrenZnode(String znode, ZooKeeper zooKeeper) {

    List<String> listResult = new ArrayList<>();
    try {
      List<String> children = zooKeeper.getChildren(znode, false);
      listResult.addAll(children);
    } catch (KeeperException e) {
      log.error("", e);
    } catch (InterruptedException e) {
      log.error("", e);
    }
    return listResult;
  }

  public static void deleteZnode(String znode, ZooKeeper zooKeeper) {
    try {
      Stat exists = zooKeeper.exists(znode, false);
      if (exists.getNumChildren() == 0) {
        zooKeeper.delete(znode, -1);
      } else {
        List<String> children = zooKeeper.getChildren(znode, false);
        for (String child : children) {
          deleteZnode(znode + "/" + child, zooKeeper);
        }
        zooKeeper.delete(znode, -1);
      }
    } catch (KeeperException e) {
      log.error("", e);
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  public static Stat znodeStat(String znode, ZooKeeper zooKeeper) {
    Stat stat = null;
    try {
      stat = zooKeeper.exists(znode, false);
    } catch (KeeperException e) {
      log.error("", e);
    } catch (InterruptedException e) {
      log.error("", e);
    }
    return stat;
  }

  /**
   * 得到znode的值
   */
  public static byte[] getDataZnode(String znode, ZooKeeper zkClient) {
    try {
      return zkClient.getData(znode, false, null);
    } catch (KeeperException e) {
      log.error("", e);
    } catch (InterruptedException e) {
      log.error("", e);
    }
    return new byte[0];
  }
}
