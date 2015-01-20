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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class managing all the available descriptors for an alternative.
 * 
 * @author Sébastien Aupetit
 */
class Alternative {
  /** The library name. */
  private transient final String libName;

  /** The descriptors of the alternative. */
  private transient Set<DescriptorPropertyFile> candidates = new HashSet<DescriptorPropertyFile>();

  private transient boolean invalidRemoved = false;
  private transient boolean libraryLoaded = false;

  /**
   * The constructor.
   * 
   * @param libName
   *          the library name
   */
  public Alternative(final String libName) {
    this.libName = libName;
  }

  /**
   * Add a descriptor to the alternatives
   * 
   * @param dpf
   *          the descriptor
   */
  public void addDescriptorPropertyFile(final DescriptorPropertyFile dpf) {
    if (libraryLoaded) {
      throw new IllegalStateException("Alternative " + libName
          + " is already loaded. No descriptors can be added anymore");
    }
    candidates.add(dpf);
  }

  /**
   * Test if the provided descriptor is already known for the alternative
   * 
   * @param dpf
   *          the descriptor
   * @return true is already known
   */
  public boolean contains(final DescriptorPropertyFile dpf) {
    for (final DescriptorPropertyFile candidate : candidates) {
      if (candidate.getUUID().equals(dpf.getUUID())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the descriptors.
   * 
   * @return the descriptors
   */
  public Set<DescriptorPropertyFile> getDescriptors() {
    return candidates;
  }

  public Map<String, File> getExtractedFiles() {
    load();
    for (final DescriptorPropertyFile dpf : candidates) {
      return dpf.getExtractedFiles();
    }
    return null;
  }

  /**
   * Test if the alternative is already loaded
   * 
   * @return true if already loaded
   */
  public boolean isLoaded() {
    return libraryLoaded;
  }

  /**
   * Select the most suited native library associated with the alternative and
   * its dependencies, then load the library.
   * 
   * @return the absolute path to the library
   */
  public String load() {
    if (!libraryLoaded) {
      Loggers.getLoaderLogger().info("Trying to load {}", libName);

      // Step 1: discard descriptors
      removeInvalidDescriptors();

      // Step 2: compute the set of valid dependency branches
      // final List<Hashtable<String, Descriptor>> results = new
      // ArrayList<Hashtable<String, Descriptor>>();
      // computeDependencyGraphes(results);
      final Set<Selection> results = selectBasedOnDependencies();

      // Step 3: Check if at least one dependency branch has been found
      if (results.isEmpty()) {
        Loggers.getLoaderLogger().error(
            "None of the alternatives is satisfied. Can not load the alternative: {}", libName);

        throw new UnsatisfiedLinkError(
            "None of the alternatives is satisfied. Can not load the alternative: " + libName);
      }

      // Step 4: Check if more than one dependency branch has been found and if
      // it is the case, choose the best branch
      Selection bestSelection = null;
      if (results.size() > 1) {
        Loggers.getLoaderLogger().warn("Multiple selections are available.");
        Loggers.getLoaderLogger().warn("Computing priority weights...");

        int bestPriority = Integer.MIN_VALUE;
        int bestSecondPriority = Integer.MIN_VALUE;
        for (final Selection selection : results) {
          final int priority = selection.getPriority();
          final int priority2 = selection.getRequirementsPriority();
          if (Loggers.getLoaderLogger().isWarnEnabled()) {
            Loggers.getLoaderLogger().warn("One candidate is: {}", selection);
          }
          if ((priority > bestPriority)
              || ((priority == bestPriority) && (priority2 > bestSecondPriority))) {
            bestSelection = selection;
            bestPriority = priority;
            bestSecondPriority = priority2;
          }
        }
        Loggers.getLoaderLogger().warn("Selected: {}", bestSelection);

      } else {
        for (final Selection selection : results) {
          bestSelection = selection;
        }
      }

      // Step 5: make the choice for each alternative of the branch by resolving
      // the alternatives
      bestSelection.resolve();

      // Step 6 : load
      if (candidates.size() == 1) {
        for (final DescriptorPropertyFile dpf : candidates) {

          final String path = dpf.load();
          Loggers.getLoaderLogger().info("Alternatives {} successfully loaded", libName);
          libraryLoaded = true;
          return path;
        }
        throw new IllegalStateException(
            "Programmatic error: one and only one candidate is possible here");
      } else {
        throw new IllegalStateException(
            "Programmatic error: one and only one candidate is possible here");
      }
    } else {
      for (final DescriptorPropertyFile dpf : candidates) {
        final String path = dpf.load();
        Loggers.getLoaderLogger().info("Alternatives {} successfully loaded", libName);
        libraryLoaded = true;
        return path;
      }
      throw new IllegalStateException(
          "Programmatic error: one and only one candidate is possible here");
    }
  }

  /**
   * Remove invalid descriptors that do not satisfy their requirements.
   */
  public void removeInvalidDescriptors() {
    if (!invalidRemoved) {
      invalidRemoved = true;

      // Step 1: discard from requirements
      final Set<DescriptorPropertyFile> retained = new HashSet<DescriptorPropertyFile>();

      for (final DescriptorPropertyFile candidate : candidates) {
        if (!candidate.shouldBeDiscardedFromRequirements()) {
          retained.add(candidate);
        }
      }
      candidates = retained;
    }
  }

  /**
   * Discard all descriptors except the provided one.
   * 
   * @param dpf
   *          the descriptor to preserve
   */
  public void resolve(final DescriptorPropertyFile dpf) {
    if (!libraryLoaded) {

      if (candidates.size() > 1) {
        candidates.clear();
        candidates.add(dpf);
        Loggers.getLoaderLogger().debug("Resolving {} to {}", libName, dpf);
      } else {
        if (candidates.size() == 1) {
          for (final DescriptorPropertyFile cur : candidates) {
            if (cur != dpf) {
              Loggers.getLoaderLogger().error(
                  "Programmatic error: only one candidate must possible here: {} != {}", cur, dpf);
              throw new IllegalStateException(
                  "Programmatic error: only one candidate must possible here");
            }
          }
        } else {
          throw new IllegalStateException(
              "Programmatic error: only one candidate must be possible here");
        }
      }
    }
  }

  /**
   * Discard all non selected descriptors if one descriptor has such UUID.
   * 
   * @param uuid
   *          the UUID of the descriptor to preserve
   */
  public void resolve(final String uuid) {
    if (libraryLoaded) {
      throw new IllegalStateException("Alternative " + libName
          + " is already loaded. Can not perform action.");
    }

    DescriptorPropertyFile found = null;
    for (final DescriptorPropertyFile dpf : candidates) {
      if (dpf.getUUID().equals(uuid)) {
        found = dpf;
        break;
      }
    }
    if (found == null) {
      Loggers.getLoaderLogger().warn("Can not resolve {} with UUID {}. Ignoring...", libName, uuid);
    } else {
      candidates.clear();
      candidates.add(found);
      Loggers.getLoaderLogger().debug("Resolving {} to {}", libName, found);
    }
  }

  /**
   * Compute selectable alternatives with dependencies
   * 
   * @return a set of selections
   */
  private Set<Selection> selectBasedOnDependencies() {
    removeInvalidDescriptors();

    // construct all the possible choice for the alternative
    final Set<Selection> results = new HashSet<Selection>();

    // OR operation
    for (final DescriptorPropertyFile dpf : getDescriptors()) {
      results.addAll(dpf.selectBasedOnDependencies());
    }

    return results;
  }
}
