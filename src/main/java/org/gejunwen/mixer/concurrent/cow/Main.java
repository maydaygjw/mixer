package org.gejunwen.mixer.concurrent.cow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static void main(String[] args) {

        RouterTable routers = new RouterTable();
        routers.add(new Router("192.168.1.1", 8080, "user-service"));
        routers.add(new Router("192.168.1.2", 8080, "user-service"));
        routers.add(new Router("192.168.1.1", 8081, "auth-service"));
        routers.add(new Router("192.168.1.2", 8081, "auth-service"));

        System.out.println(routers);
    }
}

class RouterTable {

    //key:接口名
    //value:路由表名称
    private Map<String, CustomCopyOnWriteSet<Router>> routerTable = new ConcurrentHashMap<>();

    //根据接口名称获取路由表
    public CustomCopyOnWriteSet<Router> get(String iface) {
        return routerTable.get(iface);
    }

    //删除路由
    public void remove(Router router) {
        CustomCopyOnWriteSet<Router> set = routerTable.get(router.getIface());
        if(null != set) {
            set.remove(router);
        }
    }

    //添加路由
    public void add(Router router) {
        CustomCopyOnWriteSet<Router> set = routerTable.get(router.getIface());
        if(set == null) {
            set = new CustomCopyOnWriteSet<>();
        }

        set.add(router);
        routerTable.put(router.getIface(), set);
    }

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for(Map.Entry<String, CustomCopyOnWriteSet<Router>> entry: routerTable.entrySet()) {
            sb.append("Router table for interface: " + entry.getKey());
            CustomCopyOnWriteSet<Router> set = entry.getValue();
            for(Router r: set) {
                sb.append("++ip:" + r.getIp() + "|port:" + r.getPort());
            }
        }

        return sb.toString();
    }
}

class Router {
    private String ip;
    private int port;
    private String iface;

    public Router(String ip, int port, String iface) {
        this.ip = ip;
        this.port = port;
        this.iface = iface;
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Router)) {
            return false;
        }

        Router other = (Router)obj;
        return other.getIface().equals(this.getIface()) && other.getIp().equals(this.getIp()) && other.getPort() == this.getPort();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIface() {
        return iface;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }
}
