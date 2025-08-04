package jartouml.core;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassExtractor {
    public ClassExtractor() {}

    public static List<Class<?>> extract(String jarFileName) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try (JarFile jarFile = new JarFile(jarFileName)) {
            File jar = new File(jarFileName);
            URL[] urls = { jar.toURI().toURL() };

            try (URLClassLoader classLoader = new URLClassLoader(urls)) {
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                        String className = entry.getName()
                                .replace("/", ".")
                                .replace(".class", "");

                        try {
                            Class<?> classObj = classLoader.loadClass(className);
                            classList.add(classObj);
                            //System.out.println("Loaded: " + classObj.getName());
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            System.out.println("Could not load: " + className);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }
}