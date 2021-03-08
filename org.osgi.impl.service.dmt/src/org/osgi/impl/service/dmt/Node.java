/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.dmt;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.dmt.DmtException;

// OPTIMIZE implement operations to work on either path or URI, depending on which is available.
public class Node {
    /* 
     * Permitted characters in a segment of a relative URI (RFC 2396):
     * - letters and digits: a-z, A-Z, 0-9
     * - mark symbols: "-", "_", ".", "!", "~", "*", "'", "(", ")"
     * - escaped characters: '% hex hex' where 'hex' is 0-9, a-f, A-F
     * - other symbols: ";", "@", "&", "=", "+", "$", ","
     * Additionally, ":" is allowed in all but the first segment of a URI
     * 
     * The API allows any characters, but "/" and "\" characters must be escaped
     * (preceded with "\").
     */
    
    static final Node ROOT_NODE = new Node(".");
    
    /**
     * Checks the node name and returns the canonical form.
     * <p>
     * This method ensures that the name is non-<code>null</code> and
     * non-empty, that it is not equal to "..", and that the length of the
     * canonical form is not more than the maximum segment size.
     * <p>
     * The canonicalization consists of removing all occurrances of the escape 
     * character <code>'\'</code> that precede characters other than
     * <code>'/'</code> and <code>'\'</code>.
     * 
     * @param nodeName the node name to check and canonicalize
     * @return the canonicalized form of the given node name
     * @throws DmtException with the code <code>URI_TOO_LONG</code> if the node
     *         name is too long, or <code>INVALID_URI</code> if it does not meet
     *         one of the other constraints described above
     */
    static String validateAndNormalizeNodeName(String nodeName) 
            throws DmtException {
        if(nodeName == null || nodeName.length() == 0)
            throw new DmtException(nodeName, DmtException.INVALID_URI,
                    "Node name is null or empty.");
        
        StringBuffer sb = new StringBuffer(nodeName);
        int i = 0;
        while(i < sb.length()) { // length can decrease during the loop!
            if(sb.charAt(i) == '\\') {
                if(i == sb.length() - 1) // last character cannot be a '\'
                    throw new DmtException(nodeName, DmtException.INVALID_URI,
                            "Node name ends with the escape character.");
                
                char nextCh = sb.charAt(i+1);
                if(nextCh != '\\' && nextCh != '/')
                    sb.deleteCharAt(i); // remove the extra '\'
                else
                    i++;
            } else if(sb.charAt(i) == '/')
                throw new DmtException(nodeName, DmtException.INVALID_URI,
                        "Node name contains an unescaped '/' character.");
            
            i++;
        }

        if(sb.toString().equals(".."))
            throw new DmtException(nodeName, DmtException.INVALID_URI, 
                    "Node name must not be \"..\".");
        
        return sb.toString();
    }

    /**
     * Checks the URI and canonicalizes it, returning the result as a node
     * object.
     * <p>
     * This method ensures that the URI is non-<code>null</code> and does not
     * end with the <code>'/'</code> character, and that all segments of the URI
     * are valid (see conditions in {@link #validateAndNormalizeNodeName}).
     * <p>
     * The canonicalization consists of removing all occurrances of the escape 
     * character <code>'\'</code> that precede characters other than
     * <code>'/'</code> and <code>'\'</code>.
     *
     * @param uri the URI string to check and canonicalize
     * @return a node object representing the canonicalized URI
     * @throws DmtException with the code <code>URI_TOO_LONG</code> if any
     *         segment of the URI is too long, or <code>INVALID_URI</code> if 
     *         the URI does not meet one of the other constraints described
     *         above
     */
    static Node validateAndNormalizeUri(String uri) throws DmtException {
        if (uri == null)
            throw new DmtException(uri, DmtException.INVALID_URI,
                    "The URI parameter is null.");
        
        if (uri.length() == 0) // empty relative URI
            return new Node(uri);

        StringBuffer sb = new StringBuffer();
        int len = uri.length();
        int start = 0;
		@SuppressWarnings("unused")
		int numSegments = 0;
        for(int i = 0; i < len; i++) {
            if(uri.charAt(i) == '/' && (i == 0 || uri.charAt(i-1) != '\\')) {
                if(i == len-1) // last character cannot be an unescaped '/'
                    throw new DmtException(uri, DmtException.INVALID_URI,
                            "The URI string ends with the '/' character: " + uri);
                // BUG: 2236 throw Exception on empty segment
                if ( i - start == 0 )
                    throw new DmtException(uri, DmtException.INVALID_URI,
                    "The URI has empty segments: " + uri);
                appendName(sb, uri, start, i);
                numSegments++;
                start = i+1;
            }
        }
        
        appendName(sb, uri, start, len);
        numSegments++;
        
        return new Node(sb.toString());
    }

    
    
    private static void appendName(StringBuffer sb, String uri, 
            int start, int end) throws DmtException {
        String segment = uri.substring(start, end);
        if(sb.length() != 0) { // this is not the first segment
            if(segment.equals("."))
                throw new DmtException(uri, DmtException.INVALID_URI,
                        "The URI contains the \".\" node name at a position " +
                        "other than the beginning of the URI.");
            sb.append('/');
        }
        sb.append(validateAndNormalizeNodeName(segment));
    }
    
    static String[] getUriArray(Node[] nodes) {
        String[] uris = new String[nodes.length];
        for(int i = 0; i < uris.length; i++)
            uris[i] = nodes[i].getUri();
        return uris;
    }
    
    // only exported as a convenience method for plugins
    static String convertPathToUri(String[] path) {
        if(path.length == 0)
            return "";
        
        StringBuffer sb = new StringBuffer(path[0]);
        for(int i = 1; i < path.length; i++)
            sb.append('/').append(path[i]);
        return sb.toString();
    }
    
    // convenience method for plugins
    static boolean isEqualPath(String[] path1, String[] path2) {
        if(path1.length != path2.length)
            return false;
        
        for(int i = 0; i < path1.length; i++)
            if(!path1[i].equals(path2[i]))
                return false;
            
        return true;
    }

    
    private String uri;
    private String[] path;
    
    // precondition: URI is validated and normalized
    private Node(String uri) {
        this.uri = uri;
        path = null;
    }
    
    // precondition: path is valid (originates from a previous Node object)
    Node(String[] path) {
        this.path = path;
        uri = null;
    }
    
    String[] getPath() {
        if(path == null)
            path = convertUriToPath(uri);
        return path;
    }
    
    String getUri() {
        if(uri == null)
            uri = convertPathToUri(path);
        return uri;
    }
    
    String getLastSegment() {
		@SuppressWarnings("hiding")
		String[] path = getPath();
        return path.length == 0 ? null : path[path.length-1];
    }
    
    boolean isAbsolute() {
		@SuppressWarnings("hiding")
		String[] path = getPath();
        if(path.length == 0)
            return false;
        return ".".equals(path[0]);
    }
    
    boolean isRoot() {
        return isAbsolute() && getPath().length == 1;
    }
    
    boolean isEmpty() {
        return getPath().length == 0;
    }
    
    // precondition: both nodes are absolute
    boolean isParentOf(Node other) {
        return isAncestorOf(other) && 
                getPath().length == other.getPath().length - 1;
    }
    
    // precondition: both nodes are absolute
    boolean isAncestorOf(Node other) {
        return isAncestorOf(other, false);
    }
    
    // precondition: both nodes are absolute
    boolean isAncestorOf(Node other, boolean strict) {
        String[] otherPath = other.getPath();
		@SuppressWarnings("hiding")
		String[] path = getPath();
        
        if(otherPath.length < path.length)
            return false;
        
        if(strict && otherPath.length == path.length)
            return false;
        
        for(int i = 0; i < path.length; i++)
            if(!path[i].equals(otherPath[i]))
                return false;
        
        return true;
    }
    
    // precondition: both nodes are absolute
    boolean isOnSameBranch(Node other) {
        return isAncestorOf(other) || other.isAncestorOf(this);
    }
    
    Node getParent() {
        if(isEmpty() || isRoot())
            return null;

		@SuppressWarnings("hiding")
		String[] path = getPath();
        String[] parent = new String[path.length-1];
        System.arraycopy(path, 0, parent, 0, path.length-1);
        return new Node(parent);
    }
    
    // precondition: parameter node is not absolute
    Node appendRelativeNode(Node relativeNode) {
        if(relativeNode.isAbsolute())
            throw new IllegalStateException(
                    "Cannot append an absolute node to another node.");

        if(relativeNode.isEmpty())
            return this;
        
        return new Node(getUri() + '/' + relativeNode.getUri());
    }

    // precondition: segment parameter is validated and normalized
    Node appendSegment(String segment) {
        return new Node(getUri() + '/' + segment);
    }
    
    // precondition: both nodes are absolute
    Node getRelativeNode(Node descendentNode) {
        if (!isAncestorOf(descendentNode))
            return null;
		@SuppressWarnings("hiding")
		String uri = getUri();
        String descendentUri = descendentNode.getUri();
        
        if (uri.length() == descendentUri.length())
            return new Node("");
        return new Node(descendentUri.substring(uri.length() + 1));
    }
    
    @Override
	public boolean equals(Object other) {
        if(!(other instanceof Node))
            return false;
        
        return getUri().equals(((Node) other).getUri());
    }
    
    @Override
	public int hashCode() {
        return getUri().hashCode();
    }
    
    @Override
	public String toString() {
        return getUri();
    }
    
    private static String[] convertUriToPath(String uri) {
        if(uri.length() == 0)
            return new String[] {};
        
		List<String> segments = new ArrayList<>();
        StringBuffer segment = new StringBuffer();
        
        boolean escape = false;
        for(int i = 0; i < uri.length(); i++) {
            char ch = uri.charAt(i);
            
            if(escape) {
                segment.append(ch);
                escape = false;
            } else if(ch == '/') {
                segments.add(segment.toString());
                segment = new StringBuffer();
            } else if(ch == '\\') {
                segment.append(ch);
                escape = true;
            } else
                segment.append(ch);
        }
        segments.add(segment.toString());
    
		return segments.toArray(new String[0]);
    }
    
}
