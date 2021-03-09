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

package org.osgi.test.cases.transaction.util;

import javax.transaction.UserTransaction;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.sleep.Sleep;

/**
 * @version $Rev$ $Date$
 */
public class UserTransactionFactory {

	private static ServiceReference<UserTransaction>	_userTxServiceRef;
    private static UserTransaction _userTx;
    private static BundleContext _context;

    public static void setBundleContext(BundleContext context) {
        _context = context;
    }

    public static UserTransaction getUserTransaction() {
        return getUserTransaction(0);
    }

    public static UserTransaction getUserTransaction(int waitTime) {
        if (_userTx != null) {
            return _userTx;
        }
        if (waitTime == 0) {
            // get UserTransaction from Service Reference
            _userTxServiceRef = _context
					.getServiceReference(UserTransaction.class);
        }

        if (waitTime > 0) {
            boolean done = false;
            int count = 0;
            while (!done) {
                // get UserTransaction from Service Reference
                _userTxServiceRef = _context
						.getServiceReference(UserTransaction.class);

                // check if we are able to get a valid _tmRef. If not, wait a
                // second
                // and try again
                if (_userTxServiceRef == null) {
                    try {
						Sleep.sleep(1000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    count++;
                    if (count == waitTime) {
                        System.out.println("cannot get UserTransaction after "
                                + count + " seconds");
                        done = true;
                    }

                } else {
                    System.out.println("able to get UserTransaction after "
                            + count + " seconds");
                    done = true;
                }
            }
        }

        _userTx = _context.getService(_userTxServiceRef);
        return _userTx;
    }

}
