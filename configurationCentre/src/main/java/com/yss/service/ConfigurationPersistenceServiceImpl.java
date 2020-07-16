package com.yss.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yss.client.ZooKeeperOperatingService;
import com.yss.client.ZookeeperPropertiesService;
import com.yss.service.commons.ConfigurationPersistenceService;
import com.yss.service.properties.UnifiedConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ConfigurationPersistenceService
 * @Description 配置参数持久化
 **/

@Slf4j
public class ConfigurationPersistenceServiceImpl implements ConfigurationPersistenceService {

  @Autowired
  @Qualifier(value = "zookeeperPropertiesService")
  private ZookeeperPropertiesService zookeeperPropertiesService;

  private final UnifiedConfigurationProperties unifiedConfigurationProperties;

  public ConfigurationPersistenceServiceImpl(
      UnifiedConfigurationProperties unifiedConfigurationProperties) {
    this.unifiedConfigurationProperties = unifiedConfigurationProperties;
  }

  @PostConstruct
  @Override
  public void persistenceEntrance() {
//    String filePath = System.getProperty("user.dir") + File.separator + "config" + File.separator;
    String filePath = System.getProperty("user.dir") + File.separator + "configurationCentre" + File.separator + "config" + File.separator;
    File[] files = new File(filePath).listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        if (pathname.getName().startsWith("yss-") && pathname.getName().endsWith(".json")) {
          return true;
        } else {
          return false;
        }
      }
    });

    if (files.length > 0) {
      unifiedConfigurationProperties.getZookeeperHost();
      zookeeperPropertiesService
          .initializationZkClient(unifiedConfigurationProperties.getZookeeperHost(), new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
              //do nothing
            }
          });

      if (!ZooKeeperOperatingService.existsZnode(unifiedConfigurationProperties.getBasePath(),
          zookeeperPropertiesService.getZkClient())) {
        ZooKeeperOperatingService
            .createZnodePersistent(unifiedConfigurationProperties.getBasePath(), new byte[0],
                zookeeperPropertiesService.getZkClient());
      }

      for (File file : files) {
        persistenceFile(file, zookeeperPropertiesService.getZkClient());
      }
      zookeeperPropertiesService.closeZkClinet(zookeeperPropertiesService.getZkClient());

    }
  }

  @Override
  public void persistenceFile(File file, ZooKeeper zkClient){
    String fileName = file.getName();
    String fileTmp = fileName.split("-")[1];
    String version = fileTmp.substring(0, fileTmp.length() - 5);
    String versionPath = unifiedConfigurationProperties.getBasePath() + "/" + version;

    if (!ZooKeeperOperatingService.existsZnode(versionPath, zkClient)) {
      ZooKeeperOperatingService.createZnodePersistent(versionPath, new byte[0], zkClient);
    }

    Long filelength = file.length();
    byte[] filecontent = new byte[filelength.intValue()];
    String content = "";

    FileInputStream in = null;
    try {
      in = new FileInputStream(file);
      in.read(filecontent);
      content = new String(filecontent, "UTF-8");

      log.info("content = [{}]", content);

      JSONObject jsonBody = JSONObject.parseObject(content);

      Set<String> keySet = jsonBody.keySet();

      for (String model : keySet) {
        if (model.equals("global")) {
          persistenceGlobal(versionPath, jsonBody.getJSONObject("global"), zkClient);
          log.info("persistence global properties success");
        } else if (model.equals("receiver")) {
          persistenceReceiver(versionPath, jsonBody.getJSONArray("receiver"), zkClient);
          log.info("persistence receiver properties success");
        } else {
          persistenceObject(versionPath, jsonBody.getJSONObject(model), zkClient, model);
          log.info("persistence {} properties success", model);
        }
      }
    } catch (FileNotFoundException e) {
      log.error("", e);
    } catch (UnsupportedEncodingException e) {
      log.error("", e);
    } catch (IOException e) {
      log.error("", e);
    }finally {
      if(null != in){
        try{
          in.close();
        }catch (IOException e){
          log.error("", e);
        }
      }
    }

  }

  @Override
  public void persistenceGlobal(String path, JSONObject jsonGlobal, ZooKeeper zkClient) {
    String pathGlobal = path + "/global";

    if (!ZooKeeperOperatingService.existsZnode(pathGlobal, zkClient)) {
      ZooKeeperOperatingService.createZnodePersistent(pathGlobal, new byte[0], zkClient);
    }

    Set<String> setGlobalElements = jsonGlobal.keySet();
    for (String element : setGlobalElements) {
      //创建database目录
      if ("database".equals(element)) {
        persistenceDatabase(pathGlobal, jsonGlobal.getJSONObject(element), zkClient);
      } else {
        //创建其他目录
        String body = jsonGlobal.getJSONObject(element).toString();
        String model = element;
        persistenceData(pathGlobal, model, body, zkClient);
      }
    }
  }

  @Override
  public void persistenceData(String path, String model, String value, ZooKeeper zkClient) {
    String znode = path + "/" + model;
    if (ZooKeeperOperatingService.existsZnode(znode, zkClient)) {
      ZooKeeperOperatingService.setDataToZnode(znode, value.getBytes(), zkClient);
    } else {
      ZooKeeperOperatingService.createZnodePersistent(znode, value.getBytes(), zkClient);
    }
  }

  public void persistenceReceiver(String path, JSONArray jsonReceiver, ZooKeeper zkClient) {
    String znode = path + "/receiver";
    if (ZooKeeperOperatingService.existsZnode(znode, zkClient)) {

      Stat stat = ZooKeeperOperatingService.znodeStat(znode, zkClient);
      int numChildren = stat.getNumChildren();
      int numSave = jsonReceiver.size();

      if (numChildren > numSave) {
        List<String> znodes = ZooKeeperOperatingService.childrenZnode(znode, zkClient);
        znodes.sort(new Comparator<String>() {
          @Override
          public int compare(String o1, String o2) {
            return o1.compareTo(o2);
          }
        });

        int saveSize = jsonReceiver.size();

        for (int i = numChildren - 1; i >= saveSize; i--) {
          ZooKeeperOperatingService.deleteZnode(znode + "/" + znodes.get(i), zkClient);
        }
      }
    } else {
      ZooKeeperOperatingService.createZnodePersistent(znode, new byte[0], zkClient);
    }

    int size = jsonReceiver.size();
    for (int i = 0; i < size; i++) {
      String znodeElement = znode;
      persistenceData(znodeElement, "receiver-" + i, jsonReceiver.getJSONObject(i).toJSONString(),
          zkClient);
    }
  }

  private void persistenceDatabase(String path, JSONObject jsonDatabase, ZooKeeper zkClient) {
    JSONObject used = jsonDatabase.getJSONObject("used");
    String db_name = used.getString("db_name");
    String db_ip = used.getString("db_ip");
    String db_type = used.getString("db_type");
    String db_user = used.getString("db_user");
    String db_pasword = used.getString("db_password");
    String usedDbModel = jsonDatabase.getJSONObject(db_type).toJSONString();
    //替换对应位置的参数值
    String finalData = usedDbModel.replaceAll("<db_ip>", db_ip).replaceAll("<db_name>", db_name)
        .replaceAll("<db_user>", db_user).replaceAll("<db_password>", db_pasword);
    persistenceData(path, "database", finalData, zkClient);
  }

  private void persistenceObject(String path, JSONObject json, ZooKeeper zkClient, String model) {
    persistenceData(path, model, json.toJSONString(), zkClient);
  }
}
