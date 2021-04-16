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

                sb.append("Free Memory: ").append(format.format(freeMemory / 1024 / 1024)).append(" MB\n");
                sb.append("Allocated Memory: ").append(format.format(allocatedMemory / 1024 / 1024)).append(" MB\n");
                sb.append("Max Memory: ").append(format.format(maxMemory / 1024 / 1024)).append(" MB\n");
                sb.append("Memory Usage: ").append(format.format((allocatedMemory - freeMemory) / 1024 / 1024)).append("/").append(format.format(maxMemory / 1024 / 1024)).append(" MB (").append(Math.round((double) (allocatedMemory - freeMemory) / (double) (maxMemory) * 100)).append("%)\n");
                sb.append("\n");

                try {
                    @SuppressWarnings("restriction")
                    com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    @SuppressWarnings("restriction")
                    double processLoad = operatingSystemMXBean.getProcessCpuLoad();
                    @SuppressWarnings("restriction")
                    double systemLoad = operatingSystemMXBean.getSystemCpuLoad();
                    int processors = runtime.availableProcessors();

                    sb.append("Available Processors: ").append(processors).append("\n");
                    sb.append("Process CPU Load: ").append(Math.round(processLoad * 100)).append("%\n");
                    sb.append("System CPU Load: ").append(Math.round(systemLoad * 100)).append("%\n");
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
