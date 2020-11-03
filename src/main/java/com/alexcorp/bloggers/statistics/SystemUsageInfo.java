package com.alexcorp.bloggers.statistics;

import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;

@Component
public class SystemUsageInfo {

    private DecimalFormat format = new DecimalFormat("##,#");
    private OperatingSystemMXBean OSBean;
    private Method getSystemCPUusage;
    private Method getProcessCpuUsage;

    private Method getSystemRAMfree;
    private Method getSystemTotalRAM;

    private Method getSystemRAMcachefree;
    private Method getSystemTotalRAMcache;
    private double RAM_TOTAL;

    public SystemUsageInfo() {
        OSBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : OSBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("getSystemCpuLoad") && Modifier.isPublic(method.getModifiers())) {
                getSystemCPUusage = method;
                continue;
            }
            if (method.getName().startsWith("getProcessCpuLoad") && Modifier.isPublic(method.getModifiers())) {
                getProcessCpuUsage = method;
                continue;
            }
            if (method.getName().startsWith("getTotalPhysicalMemorySize") && Modifier.isPublic(method.getModifiers())) {
                getSystemTotalRAM = method;
                continue;
            }
            if (method.getName().startsWith("getFreePhysicalMemorySize") && Modifier.isPublic(method.getModifiers())) {
                getSystemRAMfree = method;
            }
            if (method.getName().startsWith("getTotalSwapSpaceSize") && Modifier.isPublic(method.getModifiers())) {
                getSystemTotalRAMcache = method;
                continue;
            }
            if (method.getName().startsWith("getFreeSwapSpaceSize") && Modifier.isPublic(method.getModifiers())) {
                getSystemRAMcachefree = method;
            }
        }

        RAM_TOTAL = getRAMtotal();
    }

    public double getTotalCPUusage(){
        try {
            return normalisePerCents((double)getSystemCPUusage.invoke(OSBean));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public double getProcessCPUUsage(){
        try {
            return normalisePerCents((double)getProcessCpuUsage.invoke(OSBean));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public double getRAMusage(){
        double perCents = (RAM_TOTAL - getRAMfree()) / RAM_TOTAL;
        return normalisePerCents(perCents);
    }

    private double getRAMfree(){
        try {
            return (long)getSystemRAMfree.invoke(OSBean);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private double getRAMtotal(){
        try {
            return (long)getSystemTotalRAM.invoke(OSBean);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public double getRAMcacheUsage(){
        double perCents = (RAM_TOTAL - getRAMcacheFree()) / RAM_TOTAL;
        return normalisePerCents(perCents);
    }

    private double getRAMcacheFree(){
        try {
            return (long)getSystemRAMcachefree.invoke(OSBean);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private double getRAMcacheTotal(){
        try {
            return (long)getSystemTotalRAMcache.invoke(OSBean);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static double normalisePerCents(double perCents){
        return Precision.round(perCents * 100, 1);
    }
}
