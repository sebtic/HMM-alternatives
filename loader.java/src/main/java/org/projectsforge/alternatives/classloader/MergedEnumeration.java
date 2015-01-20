/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SHS.
 * 
 * SHS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SHS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SHS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.alternatives.classloader;

import java.util.Enumeration;
import java.util.NoSuchElementException;

// TODO: Auto-generated Javadoc
/**
 * The Class MergedEnumeration.
 * 
 * @param <T>
 *          the generic type
 */
class MergedEnumeration<T> implements Enumeration<T> {

  /** The enums. */
  private Enumeration<T>[] enums = null;

  /** The index. */
  private int index = 0;

  /**
   * Instantiates a new merged enumeration.
   * 
   * @param enums
   *          the enums
   */
  public MergedEnumeration(final Enumeration<T>[] enums) {
    index = 0;
    this.enums = enums;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Enumeration#hasMoreElements()
   */
  @Override
  public boolean hasMoreElements() {
    return next();
  }

  /**
   * Next.
   * 
   * @return true, if successful
   */
  private boolean next() {
    for (; index < enums.length; index++) {
      if (enums[index] != null && enums[index].hasMoreElements()) {
        return true;
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * @see java.util.Enumeration#nextElement()
   */
  @Override
  public T nextElement() {
    if (!next()) {
      throw new NoSuchElementException();
    } else {
      return enums[index].nextElement();
    }
  }
}
