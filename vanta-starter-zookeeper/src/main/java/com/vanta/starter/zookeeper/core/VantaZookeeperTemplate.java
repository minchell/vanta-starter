package com.vanta.starter.zookeeper.core;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Vanta Zookeeper 节点操作模板。
 *
 * <p>该模板替代旧项目中的静态 ZookeeperUtils。它通过构造函数注入 CuratorFramework，
 * 更容易测试、替换和扩展，也不会在类加载时强行从 Spring 容器取 Bean。</p>
 *
 * @param curatorFramework CuratorFramework 客户端。
 *                         <p>所有节点读写、监听操作都通过该客户端完成，业务方可以通过替换 Bean 接管连接和 ACL 策略。</p>
 */
public record VantaZookeeperTemplate(CuratorFramework curatorFramework) {

    /**
     * 创建 Zookeeper 节点操作模板。
     *
     * @param curatorFramework CuratorFramework 客户端
     */
    public VantaZookeeperTemplate {
    }

    /**
     * 读取底层 CuratorFramework 客户端。
     *
     * @return CuratorFramework 客户端
     */
    @Override
    public CuratorFramework curatorFramework() {
        return curatorFramework;
    }

    /**
     * 判断节点是否存在。
     *
     * @param path 节点路径
     * @return true 表示节点存在，false 表示不存在
     */
    public boolean exists(String path) {
        try {
            return curatorFramework.checkExists().forPath(path) != null;
        } catch (Exception ex) {
            throw new IllegalStateException("check zookeeper path failed: " + path, ex);
        }
    }

    /**
     * 创建持久节点。
     *
     * @param path 节点路径
     * @param data 节点内容；为空时创建空节点
     */
    public void createPersistent(String path, String data) {
        create(path, data, CreateMode.PERSISTENT);
    }

    /**
     * 创建临时节点。
     *
     * @param path 节点路径
     * @param data 节点内容；为空时创建空节点
     */
    public void createEphemeral(String path, String data) {
        create(path, data, CreateMode.EPHEMERAL);
    }

    /**
     * 设置节点内容。节点不存在时会创建持久节点。
     *
     * @param path 节点路径
     * @param data 节点内容
     */
    public void setData(String path, String data) {
        try {
            byte[] bytes = toBytes(data);
            if (!exists(path)) {
                createPersistent(path, data);
                return;
            }
            curatorFramework.setData().forPath(path, bytes);
        } catch (Exception ex) {
            throw new IllegalStateException("set zookeeper data failed: " + path, ex);
        }
    }

    /**
     * 读取节点内容。
     *
     * @param path 节点路径
     * @return UTF-8 字符串内容；节点不存在时返回空字符串
     */
    public String getData(String path) {
        try {
            byte[] bytes = curatorFramework.getData().forPath(path);
            return bytes == null ? "" : new String(bytes, StandardCharsets.UTF_8);
        } catch (KeeperException.NoNodeException ex) {
            return "";
        } catch (Exception ex) {
            throw new IllegalStateException("get zookeeper data failed: " + path, ex);
        }
    }

    /**
     * 查询子节点名称。
     *
     * @param path 节点路径
     * @return 子节点名称列表；节点不存在时返回空列表
     */
    public List<String> getChildren(String path) {
        try {
            return curatorFramework.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException ex) {
            return Collections.emptyList();
        } catch (Exception ex) {
            throw new IllegalStateException("get zookeeper children failed: " + path, ex);
        }
    }

    /**
     * 注册节点监听器。
     *
     * @param path     监听路径
     * @param listener CuratorCache 监听器
     * @return 已启动的 CuratorCache，调用方需要在不再使用时关闭
     */
    public CuratorCache addListener(String path, CuratorCacheListener listener) {
        CuratorCache cache = CuratorCache.build(curatorFramework, path);
        cache.listenable().addListener(listener);
        cache.start();
        return cache;
    }

    /**
     * 按指定模式创建 Zookeeper 节点。
     *
     * @param path       节点路径
     * @param data       节点内容，空值会创建空节点
     * @param createMode 节点创建模式
     */
    private void create(String path, String data, CreateMode createMode) {
        try {
            byte[] bytes = toBytes(data);
            if (bytes.length == 0) {
                curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path);
            } else {
                curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, bytes);
            }
        } catch (KeeperException.NodeExistsException ignored) {
            // 节点已存在属于幂等创建场景，不需要抛出异常。
        } catch (Exception ex) {
            throw new IllegalStateException("create zookeeper path failed: " + path, ex);
        }
    }

    /**
     * 将节点字符串内容转换为 UTF-8 字节数组。
     *
     * @param data 节点字符串内容
     * @return UTF-8 字节数组，空内容返回空数组
     */
    private byte[] toBytes(String data) {
        return data == null || data.isEmpty() ? new byte[0] : data.getBytes(StandardCharsets.UTF_8);
    }
}
