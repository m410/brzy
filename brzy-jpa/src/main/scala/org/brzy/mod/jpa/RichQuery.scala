/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.mod.jpa

import javax.persistence.Query
import collection.JavaConversions._

/**
 * This was taken from an example on the scala-tools site.  It allows you to convert
 * java.util.List to scala lists.
 * 
 * @author Michael Fortin
 */
class RichQuery(query : Query) {
  def getTypedList[T] = query.getResultList.toList.asInstanceOf[List[T]]
}

object RichQuery {
  implicit def richQuery(q : Query) = new RichQuery(q)
}