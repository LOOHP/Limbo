package com.loohp.limbo.consolegui;

import com.loohp.limbo.Limbo;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class SystemInfo {

    public static void printInfo() {
        if (!Limbo.noGui) {
            while (true) {
                Runtime runtime = Runtime.getRuntime();

                NumberFormat format = NumberFormat.getInstance();

                StringBuilder sb = new StringBuilder();
                long maxMemory = runtime.maxMemory();
                long allocatedMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();

                sb.append("Free Memory: " + format.format(freeMemory / 1024 / 1024) + " MB\n");
                sb.append("Allocated Memory: " + format.format(allocatedMemory / 1024 / 1024) + " MB\n");
                sb.append("Max Memory: " + format.format(maxMemory / 1024 / 1024) + " MB\n");
                sb.append("Memory Usage: " + format.format((allocatedMemory - freeMemory) / 1024 / 1024) + "/" + format.format(maxMemory / 1024 / 1024) + " MB (" + Math.round((double) (allocatedMemory - freeMemory) / (double) (maxMemory) * 100) + "%)\n");
                sb.append("\n");

                try {
                    @SuppressWarnings("restriction")
                    com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    @SuppressWarnings("restriction")
                    double processLoad = operatingSystemMXBean.getProcessCpuLoad();
                    @SuppressWarnings("restriction")
                    double systemLoad = operatingSystemMXBean.getSystemCpuLoad();
                    int processors = runtime.availableProcessors();

                    sb.append("Available Processors: " + processors + "\n");
                    sb.append("Process CPU Load: " + Math.round(processLoad * 100) + "%\n");
                    sb.append("System CPU Load: " + Math.round(systemLoad * 100) + "%\n");
                    GUI.sysText.setText(sb.toString());
                } catch (Exception ignore) {
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}
