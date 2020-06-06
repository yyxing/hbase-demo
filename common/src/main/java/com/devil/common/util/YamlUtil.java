package com.devil.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Devil
 * @version 1.0
 * @date 2020/6/6 10:48
 * @Desc 读取Yaml文件配置
 */
@Slf4j
public class YamlUtil {

    /**
     * 配置默认分隔符
     */
    private final static String SEPARATOR = ".";

    /**
     * 默认配置文件
     */
    public final static String DEFAULT_YAML = "classpath:application.yml";

    /**
     * 默认bootstrap配置文件
     */
    public final static String DEFAULT_BOOTSTRAP_YAML = "classpath:bootstrap.yml";

    /**
     * @param filePath   文件路径
     * @param prefixKeys 匹配前缀 若不为空则获取指定前缀的配置 反之则获取全部的配置
     * @return 配置的K-V集合
     */
    public static Map<String, Object> getPropertiesByYaml(String filePath, String... prefixKeys) {
        // 输入流
        InputStream in = null;
        // 记录前缀匹配数量
        int preCount = 0;
        // 结果K-V Map
        Map<String, Object> properties = new HashMap<>();
        try {
            // 获取配置文件
            File file = ResourceUtils.getFile(filePath);
            if (!file.exists()) {
                return properties;
            }
            // 读取配置文件到流中
            in = new BufferedInputStream(new FileInputStream(file));
            // yaml文件类
            Yaml yaml = new Yaml();
            // 读取文件流到指定类型中
            Map<String, Object> params = yaml.loadAs(in, Map.class);
            if (ObjectUtils.isEmpty(params)) {
                return properties;
            }
            // 递归获取配置
            recursiveYaml(params, properties, preCount, "", prefixKeys);
            return properties;
        } catch (IOException e) {
            log.error("读取Yaml配置文件时异常，异常信息为：", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("读取Yaml配置文件时，关闭流异常，异常信息为：", e);
                }
            }
        }
        return properties;
    }


    /**
     * 递归获取yaml配置
     *
     * @param params     配置map
     * @param properties 结果map
     * @param preCount   前缀索引
     * @param preKey     前缀key值
     * @param prefixKeys 根据前缀key获取配置
     */
    private static void recursiveYaml(Map<String, Object> params, Map<String, Object> properties,
                                      int preCount, String preKey, String... prefixKeys) {
        // 遍历Map集合
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            // 获取当前配置key
            String key = entry.getKey();
            // 获取当前配置value 可能为值 可能为一个新的Map 新的Map为二级配置
            Object val = entry.getValue();
            // 若配置了读取指定前缀则进行匹配 若不满足则跳过该条配置
            if (!ObjectUtils.isEmpty(prefixKeys)
                    && prefixKeys.length > preCount
                    && !prefixKeys[preCount].equals(key)) {
                continue;
            }
            // 生成新key 若前缀为org则拼接成org.key
            String newKey = "";
            if (StringUtils.isEmpty(preKey)) {
                newKey = key;
            } else {
                newKey = preKey + SEPARATOR + key;
            }
            // 若val为Map则继续递归获取配置
            if (val instanceof Map) {
                recursiveYaml((Map<String, Object>) val, properties, ++preCount, newKey, prefixKeys);
                preCount--;
            } else {
                // 若不为Map表示这是一个配置 直接添加到结果集中
                properties.put(newKey, val);
            }
        }
    }

}
