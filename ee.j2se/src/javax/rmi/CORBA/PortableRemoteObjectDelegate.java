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

package javax.rmi.CORBA;
public interface PortableRemoteObjectDelegate {
	void connect(java.rmi.Remote var0, java.rmi.Remote var1) throws java.rmi.RemoteException;
	void exportObject(java.rmi.Remote var0) throws java.rmi.RemoteException;
	java.lang.Object narrow(java.lang.Object var0, java.lang.Class var1);
	java.rmi.Remote toStub(java.rmi.Remote var0) throws java.rmi.NoSuchObjectException;
	void unexportObject(java.rmi.Remote var0) throws java.rmi.NoSuchObjectException;
}
