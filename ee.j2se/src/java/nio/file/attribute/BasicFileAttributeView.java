/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.nio.file.attribute;
public interface BasicFileAttributeView extends java.nio.file.attribute.FileAttributeView {
	java.lang.String name();
	java.nio.file.attribute.BasicFileAttributes readAttributes() throws java.io.IOException;
	void setTimes(java.nio.file.attribute.FileTime var0, java.nio.file.attribute.FileTime var1, java.nio.file.attribute.FileTime var2) throws java.io.IOException;
}
