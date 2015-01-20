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

/**
 * This exception is thrown when a property is missing
 * 
 * @author Sébastien Aupetit <aupetit@univ-tours.fr>
 * 
 */
public class MissingPropertyException extends Exception {

  private static final long serialVersionUID = -1376047799183361808L;

  /**
   * The constructor.
   * 
   * @param message
   *          the message
   */
  public MissingPropertyException(final String message) {
    super(message);
  }
}
