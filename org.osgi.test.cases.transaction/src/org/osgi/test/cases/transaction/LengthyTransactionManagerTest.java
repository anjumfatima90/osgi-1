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

package org.osgi.test.cases.transaction;

import javax.transaction.RollbackException;

import org.osgi.test.support.sleep.Sleep;

/**
 * @version $Rev$ $Date$
 *
 * Test transaction manager timeout
 *
 *
 */

public class LengthyTransactionManagerTest extends TransactionTestBundleControl {
    private static final int TOTAL_TRANSACTION_LIFETIME_TIMEOUT = 10;
    private static final int SUITABLE_DELAY = 5;

    public void setUp() throws Exception {
		super.setUp();
        super.setUpTransactionManager();
    }

    public void testTM035() throws Exception {
        try
        {
            tm.setTransactionTimeout(TOTAL_TRANSACTION_LIFETIME_TIMEOUT);
            tm.begin();
			Sleep.sleep(1000 * (TOTAL_TRANSACTION_LIFETIME_TIMEOUT - SUITABLE_DELAY));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        try
        {
            tm.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

    public void testTM036() throws Exception {
        try
        {
            tm.setTransactionTimeout(TOTAL_TRANSACTION_LIFETIME_TIMEOUT);
            tm.begin();
			Sleep.sleep(1000 * (TOTAL_TRANSACTION_LIFETIME_TIMEOUT + SUITABLE_DELAY));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }

        try
        {
            tm.commit();
            fail();
        }
        catch (RollbackException e)
        {
            // As expected
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail();
        }
    }

}
