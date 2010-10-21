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
package org.brzy.persistence

/**
 * For the Crud controller to be able to implement all the actions in a generic fashion, it needs
 * to know what the primary key is.  This trait enables that discovery no matter what is the
 * underlying persistence api.
 * 
 * @author Michael Fortin
 */
trait Persistent[P] {
  def id:P
}