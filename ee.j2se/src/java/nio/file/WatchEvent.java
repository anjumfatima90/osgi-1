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

package java.nio.file;
public interface WatchEvent<T> {
	public interface Kind<T> {
		java.lang.String name();
		java.lang.Class<T> type();
	}
	public interface Modifier {
		java.lang.String name();
	}
	T context();
	int count();
	java.nio.file.WatchEvent.Kind<T> kind();
}
