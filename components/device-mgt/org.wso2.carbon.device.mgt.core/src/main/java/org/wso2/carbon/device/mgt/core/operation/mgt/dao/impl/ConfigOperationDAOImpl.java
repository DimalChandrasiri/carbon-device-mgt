/*
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.wso2.carbon.device.mgt.core.operation.mgt.dao.impl;

import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.dao.OperationManagementDAOException;

import java.util.List;

public class ConfigOperationDAOImpl extends OperationDAOImpl {

    @Override
    public int addOperation(Operation operation) throws OperationManagementDAOException {
        return 0;
    }

    @Override
    public int updateOperation(Operation operation) throws OperationManagementDAOException {
        return 0;
    }

    @Override
    public int deleteOperation(int id) throws OperationManagementDAOException {
        return 0;
    }

    @Override
    public Operation getOperation(int id) throws OperationManagementDAOException {
        return null;
    }

    @Override
    public List<Operation> getOperations() throws OperationManagementDAOException {
        return null;
    }

    @Override
    public List<Operation> getOperations(String status) throws OperationManagementDAOException {
        return null;
    }

    @Override
    public Operation getNextOperation(DeviceIdentifier deviceId) throws OperationManagementDAOException {
        return null;
    }

}