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

package javax.swing.event;
public class InternalFrameEvent extends java.awt.AWTEvent {
	public final static int INTERNAL_FRAME_ACTIVATED = 25554;
	public final static int INTERNAL_FRAME_CLOSED = 25551;
	public final static int INTERNAL_FRAME_CLOSING = 25550;
	public final static int INTERNAL_FRAME_DEACTIVATED = 25555;
	public final static int INTERNAL_FRAME_DEICONIFIED = 25553;
	public final static int INTERNAL_FRAME_FIRST = 25549;
	public final static int INTERNAL_FRAME_ICONIFIED = 25552;
	public final static int INTERNAL_FRAME_LAST = 25555;
	public final static int INTERNAL_FRAME_OPENED = 25549;
	public InternalFrameEvent(javax.swing.JInternalFrame var0, int var1)  { super((java.lang.Object) null, 0); } 
	public javax.swing.JInternalFrame getInternalFrame() { return null; }
}
