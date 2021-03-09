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
package org.osgi.test.cases.webcontainer.tw5.servlet;

import static org.osgi.test.cases.webcontainer.util.ConstantsUtil.OSGIBUNDLECONTEXT;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * @version $Rev$ $Date$
 *
 *          Servlet implementation class BundleContextTestServlet
 */
public class BundleContextTestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public BundleContextTestServlet() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        printContext(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    private void printContext(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        ServletContext sc = getServletContext();
        BundleContext bc = (BundleContext) sc.getAttribute(OSGIBUNDLECONTEXT);
		Bundle self = bc.getBundle();

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>BundleContextTestServlet</title>");
        out.println("</head>");
        out.println("<body>");
        String p = request.getParameter("log");
        if (p == null || p.equals("1")) {
			out.println(Constants.BUNDLE_SYMBOLICNAME + ": "
					+ self.getSymbolicName() + "<br/>");
        } else if (p.equals("2")) {
				out.println("Bundle-Id: " + self.getBundleId() + "<br/>");
        } else if (p.equals("3")) {
					out.println("Bundle-LastModified: "
							+ self.getLastModified() + "<br/>");
        } else if (p.equals("4")) {
						out.println(Constants.BUNDLE_VERSION + ": "
								+ self.getVersion().toString() + "<br/>");
        }
        out.println("</body>");
        out.println("</html>");
    }
}
