/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of ALTERnatives.
 * 
 * ALTERnatives is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * ALTERnatives is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ALTERnatives. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.alternatives.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class.
 * 
 * @author Sébastien Aupetit
 */
public final class Utils {

  /**
   * The size of the buffer for extracting InputStream content.
   */
  private static final int BUFFERSIZE = 16384;

  private static Map<File, URL> extractedFileCache = new HashMap<File, URL>();

  /**
   * Extract a file from an InputStream into an output file in the library path. <br>
   * <b>Warning:</b> The file will be deleted at the end of the program
   * 
   * @param context
   *          the URI of the context in where the file can be found
   * @param fileName
   *          the output file name
   * @return a File instance associated to to output file
   * @throws MalformedURLException
   *           the input file name can not be computed
   * @throws IOException
   *           if the file can not be extracted
   */
  public static File extractFile(final URI context, final String fileName)
      throws MalformedURLException, IOException {
    return Utils.extractFile(context.toURL(), fileName);
  }

  /**
   * Extract a file from an InputStream into an output file in the library path. <br>
   * <b>Warning:</b> The file will be deleted at the end of the program
   * 
   * @param context
   *          the URI of the context in where the file can be found
   * @param fileName
   *          the output file name
   * @return a File instance associated to to output file
   * @throws MalformedURLException
   *           the input file name can not be computed
   * @throws IOException
   *           if the file can not be extracted
   */
  public static File extractFile(final URL context, final String fileName)
      throws MalformedURLException, IOException {
    return Utils.extractFileFromInput(new URL(context, fileName), fileName);
  }

  /**
   * Extract a file from an url into an output file in the library path. <br>
   * <b>Warning:</b> The file will be deleted at the end of the program
   * 
   * @param input
   *          the URL of input file
   * @param fileName
   *          the output file name
   * @return a File instance associated to to output file
   * @throws MalformedURLException
   *           the input file name can not be computed
   * @throws IOException
   *           if the file can not be extracted
   */
  public static File extractFileFromInput(final URL input, final String fileName)
      throws MalformedURLException, IOException {
    synchronized (Utils.extractedFileCache) {
      final File outFile = new File(Loader.getLibraryPath(), fileName);
      final URL oldInput = Utils.extractedFileCache.get(outFile);
      if (oldInput != null) {
        if (input.equals(oldInput)) {
          return outFile;
        } else {
          throw new UnsatisfiedLinkError("Can not extract two different files to one location");
        }
      }

      Loggers.getUtilsLogger().debug("Extracting file {} to {}", input, outFile);
      try {
        return AccessController.doPrivileged(new PrivilegedExceptionAction<File>() {
          @Override
          public File run() throws Exception {
            final InputStream inStream = input.openStream();
            final FileOutputStream out = new FileOutputStream(outFile);

            try {
              final byte[] buffer = new byte[Utils.BUFFERSIZE];
              int readLength = 0;

              while (readLength != -1) {
                readLength = inStream.read(buffer);
                if (readLength >= 0) {
                  out.write(buffer, 0, readLength);
                }
              }
            } finally {
              out.close();
              inStream.close();
            }

            Utils.extractedFileCache.put(outFile, input);
            outFile.deleteOnExit();
            return outFile;
          }
        });
      } catch (final PrivilegedActionException e) {
        if (e.getException() instanceof MalformedURLException) {
          throw (MalformedURLException) e.getException();
        } else if (e.getException() instanceof IOException) {
          throw (IOException) e.getException();
        } else {
          throw new IllegalStateException("This code must not be reached", e);
        }
      }
    }
  }

  /**
   * Detect the current architecture.
   * 
   * @return the architecture
   */
  public static String getCurrentArch() {
    return AccessController.doPrivileged(new PrivilegedAction<String>() {
      @Override
      public String run() {
        return System.getProperty("os.arch");
      }
    });
  }

  /**
   * Detect the current distribution.
   * 
   * @return the distribution
   */
  public static String getCurrentDistribution() {

    return AccessController.doPrivileged(new PrivilegedAction<String>() {
      @Override
      public String run() {
        String distribution = "unknown";
        final String operatingSystem = Utils.getCurrentOS();
        if ("linux".equals(operatingSystem)) {
          if (new File("/etc/gentoo-release").exists()) {
            distribution = "gentoo";
          } else if (new File("/etc/debian_version").exists()) {
            // TODO : verify if all debian based distribution can be mixed ?
            distribution = "debian";
          }
        } else {
          // TODO : distinguish between multiple windows version or MacOSX ?
          distribution = "na";
        }
        return distribution;
      }
    });

  }

  /**
   * Detect the current OS.
   * 
   * @return the OS
   */
  public static String getCurrentOS() {
    String name = AccessController.doPrivileged(new PrivilegedAction<String>() {
      @Override
      public String run() {
        return System.getProperty("os.name");
      }
    }).toLowerCase(Locale.ENGLISH).replace(" ", "");
    if (name.contains("windows")) {
      name = "windows";
    } else if (name.contains("macosx")) {
      name = "maxosx";
    } else if (name.contains("linux")) {
      name = "linux";
    } else {
      name = "unknown";
    }
    return name;
  }

  /**
   * Detect the current OS version.
   * 
   * @return the OS version
   */
  public static String getCurrentOSVersion() {
    return AccessController.doPrivileged(new PrivilegedAction<String>() {
      @Override
      public String run() {
        return System.getProperty("os.version");
      }
    });
  }

  /**
   * A private dummy constructor.
   */
  private Utils() {
    // nothing to do
  }
}
