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
package org.brzy.util

/**
 * This is not used. It's an implementation of the DynamicVariable class in the scala
 * library.
 * 
 * @author Michael Fortin
 */
@deprecated
class DynamicVar[T] {
  private val threadLocal = new ThreadLocal[T]
  def get: T = threadLocal.get

  def doWith[R](x: T)(func : => R) : R = {
    val original = get
    try {
      threadLocal.set(x)
      func
    } finally {
      threadLocal.set(original)
    }
  }
}