package net.catrainbow.dolemgo.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadStore {

    public static Map<String,Object> store = new ConcurrentHashMap<>();
}
