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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * A class to manage a property file.
 **/
class PropertyFile {

  /** The uri. */
  private final transient URL url;

  /** The properties. */
  private final transient Properties properties;

  /** The name. */
  private final transient String name;

  /** The uuid. */
  private final transient String uuid;

  /** The properties hash code. */
  private final transient int propertiesHashCode;

  /**
   * Instantiates a new property file.
   * 
   * @param url
   *          the url
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws MissingPropertyException
   *           the name property is missing
   */
  public PropertyFile(final URL url) throws IOException, MissingPropertyException {
    this.url = url;

    properties = new Properties();
    InputStream propStream;
    propStream = url.openStream();
    try {
      properties.load(propStream);
    } finally {
      propStream.close();
    }

    name = properties.getProperty("name").trim();
    propertiesHashCode = properties.hashCode();
    uuid = Integer.toHexString(propertiesHashCode);

    if ((name == null) || "".equals(name)) {
      Loggers.getPropertyFileLogger().error("name property undefined for {}", url);
      throw new MissingPropertyException("name property undefined for file " + url);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final PropertyFile other = (PropertyFile) obj;
    if (properties.stringPropertyNames().equals(other.properties.stringPropertyNames())) {
      for (final String pname : properties.stringPropertyNames()) {
        final String value = properties.getProperty(pname);
        if (value == null && other.properties.getProperty(pname) != null) {
          return false;
        }
        if (value != null && !value.equals(other.properties.getProperty(pname))) {
          return false;
        }
      }
    }
    if (url == null) {
      if (other.url != null) {
        return false;
      }
    } else if (!url.equals(other.url)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the properties.
   * 
   * @return the properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * Gets the properties whose name starts with the specified string.
   * 
   * @param start
   *          the start
   * @return the properties starting with
   */
  public List<String> getPropertiesStartingWith(final String start) {
    final List<String> results = new ArrayList<String>();
    for (final String key : properties.stringPropertyNames()) {
      if (key.startsWith(start)) {
        results.add(key);
      }
    }
    return results;
  }

  /**
   * Gets the property.
   * 
   * @param key
   *          the key
   * @return the property value
   */
  public String getProperty(final String key) {
    return properties.getProperty(key);
  }

  /**
   * Gets the property.
   * 
   * @param key
   *          the key
   * @param defaultValue
   *          the default value to return if the property is not defined
   * @return the property value
   */
  public String getProperty(final String key, final String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  /**
   * Gets the names of the properties.
   * 
   * @return the names
   */
  public Set<String> getPropertyNames() {
    return properties.stringPropertyNames();
  }

  /**
   * Gets the URL.
   * 
   * @return the URL
   */
  public URL getURL() {
    return url;
  }

  /**
   * Get the UUID of the property file.
   * 
   * @return the UUID
   */
  public String getUUID() {
    return uuid;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return propertiesHashCode + url.hashCode();
  }

  /**
   * A toString implementation for the class.
   * 
   * @return the string representation of the descriptor
   */
  @Override
  public String toString() {
    return name + " (" + uuid + " " + url + ")";
  }

}
