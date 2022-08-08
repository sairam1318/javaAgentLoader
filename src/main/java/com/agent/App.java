package com.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.util.List;

/**
 * compile app using javac src/main/java/com/agent/App.java
 * run using java src/main/java/com/agent/App "agentPath" "jvmAppName"
 */
public class App 
{
    public static void main( String[] args ){

        // pass agent jar with dependencies.
        String agentFilePath = args[0];
        // target application jar fie name eg: "target\\demo-0.0.1-SNAPSHOT.jar";
        String jvmAppName = args[1];
        String jvmPid = null;
        List<VirtualMachineDescriptor> jvms = VirtualMachine.list();
        for (VirtualMachineDescriptor jvm : jvms) {
            System.out.println("Running JVM: " + jvm.id() + " - " + jvm.displayName());
            if (jvm.displayName().equals(jvmAppName)) {
                jvmPid = jvm.id();
            }
        }
        if (jvmPid != null) {
            File agentFile = new File(agentFilePath);
            if (agentFile.isFile()) {
                String agentFileName = agentFile.getName();
                String agentFileExtension = agentFileName.substring(agentFileName.lastIndexOf(".") + 1);
                if (agentFileExtension.equalsIgnoreCase("jar")) {
                    try {
                        System.out.println("Attaching to target JVM with PID: " + jvmPid);
                        VirtualMachine jvm = VirtualMachine.attach(jvmPid);
                        jvm.loadAgent(agentFile.getAbsolutePath(), "sairam");
                        jvm.detach();
                        System.out.println("Attached to target JVM and loaded Java agent successfully");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            System.out.println("Target JVM running demo Java application not found");
        }
    }
}
