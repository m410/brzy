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
package org.brzy.fab.build


import org.brzy.fab.print.Conversation
import java.beans.ConstructorProperties
import java.io.File
import collection.mutable.{HashMap, ListBuffer}

@ConstructorProperties(Array("environment","line","workingDir","targetDir","sourceDir","testDir","webappDir"))
case class BuildContext(
		environment:String,
		line:Conversation,
		workingDir:File = new File(System.getProperty("user.dir")),
		targetDir:File = new File(new File(System.getProperty("user.dir")),"target"),
		sourceDir:File = new File(new File(System.getProperty("user.dir")),"src"),
		testDir:File = new File(new File(System.getProperty("user.dir")),"test"),
		cacheDir:File = new File(new File(System.getProperty("user.dir")),".brzy"),
		webappDir:File = new File(new File(System.getProperty("user.dir")),"webapp"),
    properties:HashMap[String,AnyRef] = HashMap[String,AnyRef]())