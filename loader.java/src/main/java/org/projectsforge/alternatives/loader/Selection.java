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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class representing a selection of compatible descriptors.
 * 
 * @author Sébastien Aupetit <aupetit@univ-tours.fr>
 */
final class Selection {

  /**
   * Compute the cartesian product of two sets of selections
   * 
   * @param first
   *          the first set
   * @param second
   *          the second set
   * @return the result set
   */
  public static Set<Selection> cartesianProduct(final Set<Selection> first,
      final Set<Selection> second) {
    final Set<Selection> results = new HashSet<Selection>();

    for (final Selection f : first) {
      for (final Selection s : second) {
        if (f.compatible(s)) {
          results.add(Selection.merge(f, s));
        }
      }
    }
    return results;
  }

  /**
   * Merge two selections.
   * 
   * @param first
   *          the first
   * @param second
   *          the second
   * @return the selection
   */
  private static Selection merge(final Selection first, final Selection second) {
    final Selection res = new Selection();
    res.add(first);
    res.add(second);
    return res;
  }

  /** The selected descriptors. */
  private final transient Map<String, DescriptorPropertyFile> selected = new HashMap<String, DescriptorPropertyFile>();

  /**
   * Instantiates a new selection.
   */
  public Selection() {
    // nothing to do
  }

  /**
   * Instantiates a new selection with a descriptor.
   * 
   * @param dpf
   *          the dpf
   */
  public Selection(final DescriptorPropertyFile dpf) {
    add(dpf);
  }

  /**
   * Instantiates a new selection copying a selection.
   * 
   * @param sel
   *          the sel
   */
  public Selection(final Selection sel) {
    selected.putAll(sel.selected);
  }

  /**
   * Adds a descritpor to the selection.
   * 
   * @param dpf
   *          the dpf
   */
  public void add(final DescriptorPropertyFile dpf) {
    selected.put(dpf.getName(), dpf);
  }

  /**
   * Adds the descriptors of a selection.
   * 
   * @param selection
   *          the selection
   */
  public void add(final Selection selection) {
    selected.putAll(selection.selected);
  }

  /**
   * Checks if the selections are compatible and do not introduce a
   * contradiction.
   * 
   * @param candidate
   *          the candidate selection
   * @return true, if they are compatible
   */
  private boolean compatible(final Selection candidate) {
    if (!selected.isEmpty()) {
      final Set<String> keys = new HashSet<String>(selected.keySet());
      keys.addAll(candidate.selected.keySet());

      for (final String key : keys) {
        if (selected.get(key) != null && candidate.selected.get(key) != null
            && selected.get(key) != candidate.selected.get(key)) {
          return false;
        }
      }
    }
    return true;
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
    final Selection other = (Selection) obj;
    if (selected == null) {
      if (other.selected != null) {
        return false;
      }
    } else if (!selected.equals(other.selected)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the priority.
   * 
   * @return the priority
   */
  public int getPriority() {
    int priority = 0;
    for (final String libName : selected.keySet()) {
      priority += selected.get(libName).getPriority();
    }
    return priority;
  }

  /**
   * Gets the requirements priority.
   * 
   * @return the requirements priority
   */
  public int getRequirementsPriority() {
    int priority = 0;

    for (final String libName : selected.keySet()) {
      priority += selected.get(libName).getRequirementsPriority();
    }
    return priority;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return ((selected == null) ? 0 : selected.hashCode());
  }

  /**
   * Resolve the descriptor of the selection.
   */
  public void resolve() {
    for (final String alternative : selected.keySet()) {
      Loader.getAlternatives().get(alternative).resolve(selected.get(alternative));
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder message = new StringBuilder();

    final List<String> libNames = new ArrayList<String>(selected.keySet());
    Collections.sort(libNames);

    for (final String libName : libNames) {
      final DescriptorPropertyFile dpf = selected.get(libName);
      message.append(dpf).append(" ");
    }
    message.append(" Priority: ").append(getPriority()).append('@')
        .append(getRequirementsPriority());
    return message.toString();
  }
}
