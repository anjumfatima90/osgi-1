/*
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package java.security;
public class DigestInputStream extends java.io.FilterInputStream {
	protected java.security.MessageDigest digest;
	public DigestInputStream(java.io.InputStream var0, java.security.MessageDigest var1)  { super((java.io.InputStream) null); } 
	public java.security.MessageDigest getMessageDigest() { return null; }
	public void on(boolean var0) { }
	public void setMessageDigest(java.security.MessageDigest var0) { }
}
