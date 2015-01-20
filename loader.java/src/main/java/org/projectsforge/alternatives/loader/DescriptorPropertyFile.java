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
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Specialized property file representing a descriptor for an alternatives
 * 
 * @author Sébastien Aupetit <aupetit@univ-tours.fr>
 */
class DescriptorPropertyFile extends PropertyFile {

  /** Store extracted file path */
  private final Map<String, File> extracted = new HashMap<String, File>();

  /** The hash map describing the requirements of the descriptor. */
  private transient Map<String, List<String>> requirements;
  /** The hash map describing the dependencies of the descriptor. */
  private transient Map<String, Map<String, List<String>>> dependencies;

  private transient String absolutePath = null;

  /**
   * A boolean indicating if the library files have been already extracted and
   * loaded.
   */
  private transient boolean libraryLoaded = false;

  /**
   * The constructor.
   * 
   * @param url
   *          the URL of the descriptor
   * @throws IOException
   *           if there is an error while reading the file
   * @throws MissingPropertyException
   *           if the descriptor does not have a "name" property
   */
  public DescriptorPropertyFile(final URL url) throws IOException, MissingPropertyException {
    super(url);
  }

  /**
   * Check if the descriptor is compatible with the dependent descriptor.
   * 
   * @param dependentDescriptorPropertyFile
   *          the dependent descriptor which could depend on this instance
   * @param dependencyCheckers
   *          the dependency descriptions to satisfy
   * @return true if the descriptor is compatible
   */
  private boolean canSatisfyDependencies(
      final DescriptorPropertyFile dependentDescriptorPropertyFile,
      final Map<String, List<String>> dependencyCheckers) {

    for (final Entry<String, List<String>> checker : dependencyCheckers.entrySet()) {
      final String checkerClassName = checker.getKey();
      final List<String> checkerParameters = checker.getValue();

      // checks dependencies
      for (final String checkerParameter : checkerParameters) {
        try {
          final DependencyChecker checkerInstance = DependencyCheckerManager.getManager()
              .getChecker(checkerClassName);
          if ((checkerInstance == null)
              || !checkerInstance.checkForSource(checkerParameter,
                  dependentDescriptorPropertyFile.getProperties(), getProperties())) {
            return false;
          }
        } catch (final Exception e) {
          Loggers.getPropertyFileLogger().warn(
              "Error while instantiating class " + checkerClassName, e);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Build the dependencies list from the properties.
   * 
   * @return the dependencies of the descriptor
   */
  private Map<String, Map<String, List<String>>> getDependencies() {
    if (dependencies != null) {
      return dependencies;
    }

    dependencies = new Hashtable<String, Map<String, List<String>>>();

    final String dependencyRequire = getProperty("dependency.require", "");

    // Step 1: the list of library on which we depend
    final StringTokenizer tokenizer = new StringTokenizer(dependencyRequire, " ");
    while (tokenizer.hasMoreTokens()) {
      final String dependencyName = tokenizer.nextToken();
      if ((dependencyName != null) && (!dependencies.containsKey(dependencyName))) {
        dependencies.put(dependencyName, new Hashtable<String, List<String>>());
      }
    }

    for (final String key : getPropertyNames()) {
      if (key.startsWith("dependency.")) {
        final String basekey = key.substring("dependency.".length());
        if (!"require".equals(basekey)) {
          // FORM :
          // dependency.libName.dependencycheckerclass#optionalNumber=parameters

          final int index = basekey.indexOf('.');
          if (index == -1) {
            continue;
          }
          final String libName = basekey.substring(0, index).trim();

          int sharp = basekey.lastIndexOf('#');
          if (sharp == -1) {
            sharp = basekey.length();
          }
          final String className = basekey.substring(index + 1, sharp).trim();
          List<String> parameters = dependencies.get(libName).get(className);
          if (parameters == null) {
            parameters = new ArrayList<String>();
            dependencies.get(libName).put(className, parameters);
          }

          parameters.add(getProperty(key));
        }
      }
    }

    for (final Entry<String, Map<String, List<String>>> entry : dependencies.entrySet()) {
      if (entry.getValue().isEmpty()) {
        Loggers
            .getPropertyFileLogger()
            .warn(
                "No dependency requirements defined for {} in {} : adding org.projectsforge.alternatives.dependencies.Compiler, org.projectsforge.alternatives.dependencies.Linker and org.projectsforge.alternatives.dependencies.Mode",
                entry.getKey(), getName());

        final List<String> compilerValue = new ArrayList<String>();
        compilerValue.add("strict");
        entry.getValue().put("org.projectsforge.alternatives.dependencies.Compiler", compilerValue);

        final List<String> linkerValue = new ArrayList<String>();
        linkerValue.add("strict");
        entry.getValue().put("org.projectsforge.alternatives.dependencies.Linker", linkerValue);

        final List<String> modeValue = new ArrayList<String>();
        modeValue.add("strict");
        entry.getValue().put("org.projectsforge.alternatives.dependencies.Mode", modeValue);

      }
    }

    return dependencies;
  }

  public Map<String, File> getExtractedFiles() {
    return extracted;
  }

  /**
   * Get the priority of the descriptor.
   * 
   * @return the priority
   */
  public int getPriority() {
    return Integer.valueOf(getProperty("priority", "0"));
  }

  /**
   * Build the requirements list from the properties.
   * 
   * @return the requirements of the descriptor
   */
  private Map<String, List<String>> getRequirements() {
    if (requirements == null) {
      requirements = new Hashtable<String, List<String>>();
      for (final String key : getPropertyNames()) {
        if (key.startsWith("require.")) {
          String className = key.substring("require.".length());
          int sharp = className.lastIndexOf('#');
          if (sharp == -1) {
            sharp = className.length();
          }
          className = className.substring(0, sharp).trim();
          List<String> parameters = requirements.get(className);
          if (parameters == null) {
            parameters = new ArrayList<String>();
            requirements.put(className, parameters);
          }
          parameters.add(getProperty(key));
        }
      }

      if (requirements.isEmpty()) {
        Loggers.getPropertyFileLogger().warn("No requirements defined for {}", getName());
      }
    }
    return requirements;
  }

  /**
   * Gets the requirements priority of the alternatives.
   * 
   * @return the requirements priority
   */
  public int getRequirementsPriority() {
    int sum = 0;

    for (final Entry<String, List<String>> entry : getRequirements().entrySet()) {
      RequirementChecker checker;
      try {
        checker = RequirementCheckerManager.getChecker(entry.getKey());

        for (final String parameter : entry.getValue()) {
          sum += checker.getPriority(parameter, getProperties());
        }
      } catch (final Exception e) {
        // ignore it
      }
    }
    return sum;
  }

  /**
   * Extract and loads the descriptor library.
   * 
   * @return the absolute path to the library
   */
  public String load() {
    if (!libraryLoaded) {

      // load the dependencies by order
      final StringTokenizer tokenizer = new StringTokenizer(getProperty("dependency.require", ""),
          " ");
      while (tokenizer.hasMoreTokens()) {
        Loader.load(tokenizer.nextToken());
      }

      // extract the files and load the library
      try {
        if (getProperty("provided") == null) {
          // Step 1: extract necessary files
          for (final String key : getPropertiesStartingWith("extractfile")) {
            extracted.put(key.substring("extractfile.".length()),
                Utils.extractFile(getURL(), getProperty(key)));
          }

          // Step 2: extract extra dependencies
          for (final String key : getPropertiesStartingWith("extractdep")) {
            final File file = Utils.extractFile(getURL(), getProperty(key));
            if (key.equals("extractdep"))
            	extracted.put(key, file);
            else
            	extracted.put(key.substring("extractdep.".length()), file);
            LibraryPathManager.prepareLibraryLoading(file.getAbsolutePath(),
                Loggers.getPropertyFileLogger());
          }

          // Step 3: extract the library and load it
          final String libName = getProperty("library");
          if (libName != null) {
            Loggers.getPropertyFileLogger().debug("Trying to load library {} for {} (with {})",
                new Object[] { libName, getName(), getURL() });
            final File library = Utils.extractFile(getURL(), libName);
            extracted.put("", library);
            LibraryPathManager.prepareLibraryLoading(library.getAbsolutePath(),
                Loggers.getPropertyFileLogger());
            Loggers.getPropertyFileLogger().info("Loading native library: {}", library);
            try {
              try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                  @Override
                  public Object run() {
                    System.load(library.getAbsolutePath());
                    return null;
                  }
                });
              } catch (final PrivilegedActionException e) {
                throw e.getException();
              }
              Loggers.getPropertyFileLogger()
                  .info("Native library {} successfully loaded", library);
              libraryLoaded = true;
              absolutePath = library.getAbsolutePath();
              return absolutePath;
            } catch (final UnsatisfiedLinkError e) {
              Loggers.getPropertyFileLogger().error(
                  "Native library " + library + " failed to load", e);
              throw e;
            }
          } else {
            libraryLoaded = true;
            absolutePath = null;
            return absolutePath;
          }
        } else {
          // it's a provided library
          final String libName = getProperty("library");
          if (libName != null) {
            Loggers.getPropertyFileLogger().debug("Trying to load provided library {} for {}",
                libName, getName());
            try {
              try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                  @Override
                  public Object run() {
                    System.loadLibrary(libName);
                    return null;
                  }
                });
              } catch (final PrivilegedActionException e) {
                throw e.getException();
              }
              Loggers.getPropertyFileLogger()
                  .info("Native library {} successfully loaded", libName);
              extracted.put("", new File(libName));
              libraryLoaded = true;
              absolutePath = libName;
              return absolutePath;

            } catch (final UnsatisfiedLinkError e) {
              Loggers.getPropertyFileLogger().error(
                  "Failed to load provided native library " + libName, e);
              throw e;
            }
          } else {
            libraryLoaded = true;
            absolutePath = null;
            return absolutePath;
          }
        }

      } catch (final Exception e) {
        Loggers.getPropertyFileLogger().error("An error occurred while extracting file", e);
        final UnsatisfiedLinkError ule = new UnsatisfiedLinkError(
            "An error occurred while extracting file");
        ule.initCause(e);
        throw ule;
      }
    } else {
      return absolutePath;
    }
  }

  /**
   * Compute selectable alternatives with dependencies from this descriptor
   * 
   * @return a set of selections
   */
  public Set<Selection> selectBasedOnDependencies() {
    Set<Selection> results = new HashSet<Selection>();
    results.add(new Selection(this));

    for (final Entry<String, Map<String, List<String>>> entry : getDependencies().entrySet()) {
      final String dependentOnLibName = entry.getKey();

      final Alternative alternative = Loader.getAlternatives().get(dependentOnLibName);

      if (alternative != null) {
        alternative.removeInvalidDescriptors();

        // construct all the possible choice for the alternative
        final Set<Selection> alternativeResults = new HashSet<Selection>();

        // OR operation: each descriptor on which we depend is a possibility
        for (final DescriptorPropertyFile dependOnDescriptor : alternative.getDescriptors()) {
          // can the candidate satisfy the dependency constraints ?
          if (dependOnDescriptor.canSatisfyDependencies(this, entry.getValue())) {
            final Set<Selection> temp = dependOnDescriptor.selectBasedOnDependencies();
            alternativeResults.addAll(temp);
          }
        }
        // AND operation: all dependencies must be simultaneously satisfied
        results = Selection.cartesianProduct(results, alternativeResults);
      } else {
        // no combination possible => fail
        results.clear();
      }
      // test if further combination are possible. If not, we cut here because
      // we already fail
      if (results.isEmpty()) {
        break;
      }
    }
    return results;
  }

  /**
   * Check if the requirements are satisfiable and if not mark the descriptor as
   * discarded.
   * 
   * @return true if should be discarded
   */
  public boolean shouldBeDiscardedFromRequirements() {
    boolean valid = true;

    for (final Entry<String, List<String>> entry : getRequirements().entrySet()) {
      RequirementChecker checker;
      try {
        checker = RequirementCheckerManager.getChecker(entry.getKey());

        if (checker == null) {
          valid = false;
          break;
        } else {
          for (final String parameter : entry.getValue()) {
            if ((checker == null) || !checker.check(parameter, getProperties())) {
              Loggers.getPropertyFileLogger().debug("Requirement for {}: {} with {} failed.",
                  new Object[] { this, entry.getKey(), parameter });
              valid = false;
              break;
            }
          }
        }
      } catch (final Exception e) {
        Loggers.getPropertyFileLogger().warn(
            "Unsatisfiable instantiation of requirement checker: " + entry.getKey(), e);
        Loggers.getPropertyFileLogger().debug("Alternatives {} discarded.", this);
        valid = false;
      }
      if (!valid) {
        break;
      }
    }
    return !valid;
  }
}
