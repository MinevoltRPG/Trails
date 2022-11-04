package me.ccrama.Trails.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ResourceUtils {

		  public static List<String> listFiles(Class<?> clazz, String path) throws IOException {
		    List<String> files = new ArrayList<String>();

		    URL url = clazz.getResource(path);
		    if (url == null) throw new IllegalArgumentException("list files failed for path '" + path + "'.");

		    if ("file".equals(url.getProtocol())) {
		      try {
		        File rootDir = new File(url.toURI());
		        findFilesRecursive(files, rootDir, rootDir);
		        return files;
		      }
		      catch (URISyntaxException e) {
		        throw new AssertionError(e); // should never happen
		      }
		    }

		    if ("jar".equals(url.getProtocol())) {
		      if (path.startsWith("/")) path = path.substring(1);
		      String jarFile = url.getPath().substring(5, url.getPath().indexOf("!"));
		      JarFile jar = new JarFile(jarFile);
		      Enumeration<JarEntry> entries = jar.entries();
		      while (entries.hasMoreElements()) {
		    	JarEntry entry = entries.nextElement();
		        if (!entry.isDirectory()) {
		          String name = entry.getName();
		          if (name.startsWith(path)) files.add(name.substring(path.length() + 1));
		        }
		      }
              jar.close();
		      return files;
		    }

		    throw new IllegalArgumentException("Unexpected protocol: " + url.getProtocol());
		  }

		  private static void findFilesRecursive(List<String> files, File rootDir, File currPath) {
		    if (currPath.isDirectory()) {
		      for (File path : currPath.listFiles()) {
		        findFilesRecursive(files, rootDir, path);
		      }
		    }
		    else {
		      files.add(currPath.getAbsolutePath().substring(rootDir.getAbsolutePath().length() + 1));
		    }
		  }
		}