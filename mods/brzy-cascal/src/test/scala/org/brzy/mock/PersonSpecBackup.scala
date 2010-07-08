package org.brzy.mock

import org.specs.SpecificationWithJUnit

/**
 * Note that this doesn't work in maven for some reason.
 * http://tardigra.de/?p=66
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
object PersonSpecBackup extends SpecificationWithJUnit {

  "Demo" should {

    "simple " in {
      1 + 1 == 2
    }
  }
}