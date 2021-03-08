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

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.transaction.SystemException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.osgi.test.support.sleep.Sleep;

/**
 * @version $Rev$ $Date$
 */
public class XAResourceImpl implements XAResource, Serializable {
    static final long serialVersionUID = -2141508727147091254L;

	private static Hashtable<Integer,XAResourceData>	_resources			= new Hashtable<>();

    private static int prepareCount = 0;
    private static int commitCount = 0;
    private static int[] prepareOrder = new int[20];
    private static int[] commitOrder = new int[20];

	private static ArrayList<XAEvent>					_XAEvents			= new ArrayList<>();

    protected Integer _key;
    private static transient int _nextKey = 0;

    public static final int RUNTIME_EXCEPTION = -1000;
    public static final int SLEEP_COMMIT = -3000;
    public static final int SLEEP_ROLLBACK = -4000;

    public static final int NOT_STARTED = 1;
    public static final int COMMITTED = 2;
    public static final int ENDED = 4;
    public static final int STARTED = 8;
    public static final int PREPARED = 16;
    public static final int ROLLEDBACK = 32;
    public static final int FORGOTTEN = 64;
    public static final int RECOVERED = 128;

    private static final String STATE_FILE = "XAResourceData.dat";

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof XAResourceImpl) {
            if (_key.equals(((XAResourceImpl) o)._key)) {
                return true;
            }
        }

        return false;
    }

    public int hashCode() {
        return _key.hashCode();
    }

    public class XAResourceData implements Serializable {
        private static final long serialVersionUID = -253646295143893358L;

		int							key;
        private UID RM; // use UID as UUID is only avail in JDK 1.5
        private int prepareAction = XAResource.XA_OK;
        private int rollbackAction = XAResource.XA_OK;
        private int commitAction = XAResource.XA_OK;
        private int endAction = XAResource.XA_OK;
        private int startAction = XAResource.XA_OK;
        private int forgetAction = XAResource.XA_OK;
        private int recoverAction = XAResource.XA_OK;
        private int commitRepeatCount;
        private int rollbackRepeatCount;
        private int forgetRepeatCount;
        private int recoverRepeatCount;
        private int statusDuringCommit;
        private int statusDuringRollback;
        private int statusDuringPrepare;
        private int rollbackCount;
        private int forgetCount;
        private boolean heuristic;
        private int _sleepTime;
        private Xid _xid;
        private int _state = NOT_STARTED;

        public void setSleepTime(int sleepTime) {
            _sleepTime = sleepTime;
        }

        public int getSleepTime() {
            return _sleepTime;
        }

        public Xid getXid() {
            return _xid;
        }

        public void setXid(Xid xid) {
            _xid = xid;
        }

        public int getState() {
            return _state;
        }

        public void setState(int state) {
            _state |= state;
        }

        public XAResourceData(int i) {
            key = i;
            RM = new UID();
        }

        public int getCommitAction() {
            return commitAction;
        }

        public void setCommitAction(int commitAction) {
            this.commitAction = commitAction;
        }

        public int getPrepareAction() {
            return prepareAction;
        }

        public void setPrepareAction(int prepareAction) {
            this.prepareAction = prepareAction;
        }

        public UID getRM() {
            return RM;
        }

        public void setRM(UID rm) {
            RM = rm;
        }

        public int getRollbackAction() {
            return rollbackAction;
        }

        public void setRollbackAction(int rollbackAction) {
            this.rollbackAction = rollbackAction;
        }

        public int getRecoverAction() {
            return recoverAction;
        }

        public void setRecoverAction(int recoverAction) {
            this.recoverAction = recoverAction;
        }

        public int getStatusDuringCommit() {
            return statusDuringCommit;
        }

        public void setStatusDuringCommit(int statusDuringCommit) {
            this.statusDuringCommit = statusDuringCommit;
        }

        public int getStatusDuringRollback() {
            return statusDuringRollback;
        }

        public void setStatusDuringRollback(int statusDuringRollback) {
            this.statusDuringRollback = statusDuringRollback;
        }

        public int getStatusDuringPrepare() {
            return statusDuringPrepare;
        }

        public void setStatusDuringPrepare(int statusDuringPrepare) {
            this.statusDuringPrepare = statusDuringPrepare;
        }

        public boolean isForgotten() {
            return (_state & FORGOTTEN) != 0;
        }

        public boolean isHeuristic() {
            return heuristic;
        }

        public void setHeuristic(boolean heuristic) {
            this.heuristic = heuristic;
        }

        public int getEndAction() {
            return endAction;
        }

        public void setEndAction(int endAction) {
            this.endAction = endAction;
        }

        public int getCommitRepeatCount() {
            return commitRepeatCount;
        }

        public void setCommitRepeatCount(int commitRepeatCount) {
            this.commitRepeatCount = commitRepeatCount;
        }

        public int getRecoverRepeatCount() {
            return recoverRepeatCount;
        }

        public void setRecoverRepeatCount(int recoverRepeatCount) {
            this.recoverRepeatCount = recoverRepeatCount;
        }

        public int getForgetRepeatCount() {
            return forgetRepeatCount;
        }

        public void setForgetRepeatCount(int forgetRepeatCount) {
            this.forgetRepeatCount = forgetRepeatCount;
        }

        public int getForgetAction() {
            return forgetAction;
        }

        public void setForgetAction(int forgetAction) {
            this.forgetAction = forgetAction;
        }

        public int getRollbackCount() {
            return rollbackCount;
        }

        public void setRollbackCount(int rollbackCount) {
            this.rollbackCount = rollbackCount;
        }

        public int getRollbackRepeatCount() {
            return rollbackRepeatCount;
        }

        public void setRollbackRepeatCount(int rollbackRepeatCount) {
            this.rollbackRepeatCount = rollbackRepeatCount;
        }

        public int getStartAction() {
            return startAction;
        }

        public void setStartAction(int startAction) {
            this.startAction = startAction;
        }

        public int getForgetCount() {
            return forgetCount;
        }

        public void setForgetCount(int forgetCount) {
            this.forgetCount = forgetCount;
        }

        public boolean inState(int state) {
            return (_state & state) != 0;
        }
    }

    public XAResourceImpl() {
        _key = Integer.valueOf(_nextKey++);
        _resources.put(_key, new XAResourceData(_key.intValue()));
    }

    public XAResourceImpl(int i) {
        if (_resources.containsKey(Integer.valueOf(i))) {
            _key = Integer.valueOf(i);
        } else if (i < _nextKey) {
            throw new RuntimeException(i + " < " + _nextKey);
        } else {
            _nextKey = i + 1;
            _key = Integer.valueOf(i);

            _resources.put(_key, new XAResourceData(_key.intValue()));
        }
    }

    public static XAResourceImpl getXAResourceImpl(int key) {
        return new XAResourceImpl(key);
    }

    public void commit(Xid xid, boolean onePhase) throws XAException {
        System.out.println("commit(" + _key + ", " + xid + ", " + onePhase
                + ")");

        commitOrder[commitCount++] = _key.intValue();
        _XAEvents
                .add(new XAEvent(TransactionConstants.COMMIT, _key.intValue()));

        try {
            self().setStatusDuringCommit(
                    TransactionManagerFactory.getTransactionManager()
                            .getStatus());
        } catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final int commitAction = self().getCommitAction();
        if (commitAction != XAResource.XA_OK) {
            final int repeatCount = self().getCommitRepeatCount();
            self().setCommitRepeatCount(repeatCount - 1);
            if (repeatCount >= 0) {
                switch (commitAction) {
                case RUNTIME_EXCEPTION:
                    throw new RuntimeException();

                case SLEEP_COMMIT:
                    try {
							Sleep.sleep(self().getSleepTime());
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                default:
                    throw new XAException(commitAction);
                }
            }
        }

        setState(COMMITTED);
    }

    public void end(Xid xid, int flags) throws XAException {
        System.out.println("end(" + _key + ", " + xid + ", " + flags + ")");

        if (prepareCount != 0) {
            prepareCount = 0;
            for (int i = 0; i < prepareOrder.length; i++)
                prepareOrder[i] = 0;
        }
        if (commitCount != 0) {
            commitCount = 0;
            for (int i = 0; i < commitOrder.length; i++)
                commitOrder[i] = 0;
        }
        _XAEvents.add(new XAEvent(TransactionConstants.END, _key.intValue()));

        final int endAction = self().getEndAction();
        if (endAction != XAResource.XA_OK) {
            switch (endAction) {
            case RUNTIME_EXCEPTION:
                throw new RuntimeException();

            default:
                throw new XAException(endAction);
            }
        }

        setState(ENDED);
    }

    public void forget(Xid xid) throws XAException {
        System.out.println("forget(" + _key + ", " + xid + ")");

        _XAEvents
                .add(new XAEvent(TransactionConstants.FORGET, _key.intValue()));

        final int forgetAction = self().getForgetAction();
        if (forgetAction != XAResource.XA_OK) {
            final int repeatCount = self().getForgetRepeatCount();
            self().setForgetRepeatCount(repeatCount - 1);
            if (repeatCount >= 0) {
                switch (forgetAction) {
                case RUNTIME_EXCEPTION:
                    throw new RuntimeException();

                default:
                    throw new XAException(forgetAction);
                }
            }
        }

        setState(FORGOTTEN);
        self().setForgetCount(self().getForgetCount() + 1);
    }

    public int getTransactionTimeout() throws XAException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isSameRM(XAResource xares) throws XAException {
        if (xares instanceof XAResourceImpl) {
            return (self().getRM() == (((XAResourceImpl) xares).getRM()));
        }

        return false;
    }

    public int prepare(Xid xid) throws XAException {
        System.out.println("prepare(" + _key + ", " + xid + ") = "
                + self().getPrepareAction());

        prepareOrder[prepareCount++] = _key.intValue();
        _XAEvents
                .add(new XAEvent(TransactionConstants.PREPARE, _key.intValue()));

        try {
            self().setStatusDuringPrepare(
                    TransactionManagerFactory.getTransactionManager()
                            .getStatus());
        } catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        switch (self().getPrepareAction()) {
        case RUNTIME_EXCEPTION:
            throw new RuntimeException();

        case XAResource.XA_OK:
        case XAResource.XA_RDONLY:
            setState(PREPARED);
            return self().getPrepareAction();

        case SLEEP_COMMIT:
            try {
					Sleep.sleep(self().getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

            setState(PREPARED);
            return XAResource.XA_OK;

        case SLEEP_ROLLBACK:
            try {
					Sleep.sleep(self().getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

            setState(ROLLEDBACK);
            throw new XAException(XAException.XA_RBROLLBACK);

        default:
            throw new XAException(self().getPrepareAction());
        }
    }

    public Xid[] recover(int flag) throws XAException {
        System.out.println("recover(" + _key + ", " + flag + ")");

        if (commitCount != 0) {
            commitCount = 0;
            for (int i = 0; i < commitOrder.length; i++)
                commitOrder[i] = 0;
        }
        _XAEvents
                .add(new XAEvent(TransactionConstants.RECOVER, _key.intValue()));

        final int recoverAction = self().getRecoverAction();
        if (recoverAction != XAResource.XA_OK) {
            final int repeatCount = self().getRecoverRepeatCount();
            self().setRecoverRepeatCount(repeatCount - 1);
            if (repeatCount >= 0) {
                switch (recoverAction) {
                case RUNTIME_EXCEPTION:
                    throw new RuntimeException();

                default:
                    throw new XAException(recoverAction);
                }
            }
        }

        setState(RECOVERED);

        if (self().inState(PREPARED) && !self().inState(COMMITTED)
                && !self().inState(ROLLEDBACK)) {
            return new Xid[] { getXid() };
        }

        return null;
    }

    private Xid getXid() {
        return self().getXid();
    }

    public void rollback(Xid xid) throws XAException {
        System.out.println("rollback(" + _key + ", " + xid + ")");

        commitOrder[commitCount++] = _key.intValue();
        _XAEvents.add(new XAEvent(TransactionConstants.ROLLBACK, _key
                .intValue()));

        try {
            self().setStatusDuringRollback(
                    TransactionManagerFactory.getTransactionManager()
                            .getStatus());
        } catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        self().setRollbackCount(self().getRollbackCount() + 1);

        if (self().getRollbackAction() != XAResource.XA_OK) {
            final int repeatCount = self().getRollbackRepeatCount();
            self().setRollbackRepeatCount(repeatCount - 1);
            if (repeatCount >= 0) {
                final int rollbackAction = self().getRollbackAction();

                switch (rollbackAction) {
                case RUNTIME_EXCEPTION:
                    throw new RuntimeException();

                case SLEEP_ROLLBACK:
                    try {
							Sleep.sleep(self().getSleepTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    throw new XAException(rollbackAction);
                }
            }
        }

        setState(ROLLEDBACK);
    }

    public boolean setTransactionTimeout(int seconds) throws XAException {
        // TODO Auto-generated method stub
        return false;
    }

    public void start(Xid xid, int flags) throws XAException {
        System.out.println("start(" + _key + ", " + xid + ", " + flags + ")");
        _XAEvents.add(new XAEvent(TransactionConstants.START, _key.intValue()));

        setState(STARTED);
        self().setXid(xid);

        final int startAction = self().getStartAction();
        if (startAction != XAResource.XA_OK) {
            switch (startAction) {
            case RUNTIME_EXCEPTION:
                throw new RuntimeException();

            default:
                throw new XAException(startAction);
            }
        }
    }

    private void setState(int state) {
        self().setState(state);
    }

    public XAResourceImpl setPrepareAction(int action) {
        self().setPrepareAction(action);

        switch (action) {
        case XAException.XA_HEURCOM:
        case XAException.XA_HEURHAZ:
        case XAException.XA_HEURMIX:
        case XAException.XA_HEURRB:
            self().setHeuristic(true);
            break;

        default:
            break;
        }

        return this;
    }

    public XAResourceImpl setRollbackAction(int action) {
        self().setRollbackAction(action);

        switch (action) {
        case XAException.XA_HEURCOM:
        case XAException.XA_HEURHAZ:
        case XAException.XA_HEURMIX:
        case XAException.XA_HEURRB:
            self().setHeuristic(true);
            break;

        default:
            break;
        }

        return this;
    }

    public XAResourceImpl setEndAction(int action) {
        self().setEndAction(action);

        return this;
    }

    public XAResourceImpl setStartAction(int action) {
        self().setStartAction(action);

        return this;
    }

    public XAResourceImpl setForgetAction(int action) {
        self().setForgetAction(action);

        return this;
    }

    public XAResourceImpl setRecoverAction(int action) {
        self().setRecoverAction(action);

        return this;
    }

    public XAResourceImpl setCommitAction(int action) {
        self().setCommitAction(action);

        switch (action) {
        case XAException.XA_HEURCOM:
        case XAException.XA_HEURHAZ:
        case XAException.XA_HEURMIX:
        case XAException.XA_HEURRB:
            self().setHeuristic(true);
            break;

        default:
            break;
        }

        return this;
    }

    public XAResourceImpl setCommitRepeatCount(int repeat) {
        self().setCommitRepeatCount(repeat);
        return this;
    }

    public int getCommitRepeatCount() {
        return self().getCommitRepeatCount();
    }

    public XAResourceImpl setRecoverRepeatCount(int repeat) {
        self().setRecoverRepeatCount(repeat);
        return this;
    }

    public int getRecoverRepeatCount() {
        return self().getRecoverRepeatCount();
    }

    public XAResourceImpl setRollbackRepeatCount(int repeat) {
        self().setRollbackRepeatCount(repeat);
        return this;
    }

    public int getRollbackCount() {
        return self().getRollbackCount();
    }

    public int getForgetCount() {
        return self().getForgetCount();
    }

    public XAResourceImpl setForgetRepeatCount(int repeat) {
        self().setForgetRepeatCount(repeat);
        return this;
    }

    public int getForgetRepeatCount() {
        return self().getForgetRepeatCount();
    }

    public int getStatusDuringPrepare() {
        return self().getStatusDuringPrepare();
    }

    public int getStatusDuringRollback() {
        return self().getStatusDuringRollback();
    }

    public int getStatusDuringCommit() {
        return self().getStatusDuringCommit();
    }

    public UID getRM() {
        return self().getRM();
    }

    public void setRM(UID rm) {
        self().setRM(rm);
    }

    public boolean inState(int state) {
        return self().inState(state);
    }

    public static boolean allInState(int state) {
		Enumeration<Integer> e = _resources.keys();
        while (e.hasMoreElements()) {
            Integer key = e.nextElement();
            XAResourceData res = _resources.get(key);
            if (!res.inState(state)) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkForgotten() {
		Enumeration<Integer> e = _resources.keys();
        while (e.hasMoreElements()) {
            Integer key = e.nextElement();
            XAResourceData res = _resources.get(key);
            if (res.isHeuristic() && !res.isForgotten()) {
                return false;
            }
        }

        return true;
    }

    public void setHeuristic() {
        self().setHeuristic(true);
    }

    public XAResourceImpl setSleepTime(int sleepTime) {
        self().setSleepTime(sleepTime);

        return this;
    }

    private XAResourceData self() {
        return _resources.get(_key);
    }

    static public int resourceCount() {
        return _resources.size();
    }

    public static void clear() {
        _XAEvents.clear();
        _resources.clear();
        _nextKey = 0;
    }

    public static int loadState() {
        int resourceCount = 0;
        ObjectInputStream ois = null;

        try {
            final FileInputStream fos = new FileInputStream(STATE_FILE);
            ois = new ObjectInputStream(fos);

            while (true) {
                final XAResourceData xares = (XAResourceData) ois.readObject();
                _resources.put(Integer.valueOf(xares.key), xares);
                _nextKey = xares.key + 1;
                resourceCount++;
            }
        } catch (EOFException e) {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resourceCount;
    }

    public static void destroy(XAResourceImpl xaRes) {
        System.out.println("Destroying: " + xaRes._key);
    }

    public static int[] getPrepareOrder() {
        return prepareOrder;
    }

    public static int[] getCommitOrder() {
        return commitOrder;
    }

	public static ArrayList<XAEvent> getXAEvents() {
        return _XAEvents;
    }

    public class XAEvent {
        private String _event;
        private int _key;

        public XAEvent(String event, int key) {
            _event = event;
            _key = key;
        }

        public boolean isSameAs(String event, int key) {
            return event.equals(_event) && key == _key;
        }

        public String toString() {
            return _event + " " + _key;
        }
    }
}
