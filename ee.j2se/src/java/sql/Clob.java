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

package java.sql;
public interface Clob {
	void free() throws java.sql.SQLException;
	java.io.InputStream getAsciiStream() throws java.sql.SQLException;
	java.io.Reader getCharacterStream() throws java.sql.SQLException;
	java.io.Reader getCharacterStream(long var0, long var1) throws java.sql.SQLException;
	java.lang.String getSubString(long var0, int var1) throws java.sql.SQLException;
	long length() throws java.sql.SQLException;
	long position(java.lang.String var0, long var1) throws java.sql.SQLException;
	long position(java.sql.Clob var0, long var1) throws java.sql.SQLException;
	java.io.OutputStream setAsciiStream(long var0) throws java.sql.SQLException;
	java.io.Writer setCharacterStream(long var0) throws java.sql.SQLException;
	int setString(long var0, java.lang.String var1) throws java.sql.SQLException;
	int setString(long var0, java.lang.String var1, int var2, int var3) throws java.sql.SQLException;
	void truncate(long var0) throws java.sql.SQLException;
}
