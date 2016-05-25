/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.device.mgt.jaxrs.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.device.details.DeviceWrapper;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.common.search.SearchContext;
import org.wso2.carbon.device.mgt.core.app.mgt.ApplicationManagementProviderService;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.search.mgt.SearchManagerService;
import org.wso2.carbon.device.mgt.core.search.mgt.SearchMgtException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.jaxrs.service.api.DeviceManagementService;
import org.wso2.carbon.device.mgt.jaxrs.util.DeviceMgtAPIUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/devices")
public class DeviceManagementServiceImpl implements DeviceManagementService {

    private static final Log log = LogFactory.getLog(DeviceManagementServiceImpl.class);

    @GET
    @Override
    public Response getDevices(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        try {
            DeviceManagementProviderService dms = DeviceMgtAPIUtils.getDeviceManagementService();
            PaginationRequest request = new PaginationRequest(offset, limit);

            PaginationResult result = dms.getAllDevices(request);
            if (result == null || result.getData().size() == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("No device is currently enrolled " +
                        "with the server").build();
            }
            return Response.status(Response.Status.OK).entity(result.getData()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching all enrolled devices";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @GET
    @Override
    public Response getDevices(@QueryParam("type") String type, @QueryParam("offset") int offset,
                               @QueryParam("limit") int limit) {
        try {
            DeviceManagementProviderService dms = DeviceMgtAPIUtils.getDeviceManagementService();
            PaginationRequest request = new PaginationRequest(offset, limit);
            request.setDeviceType(type);

            PaginationResult result = dms.getAllDevices(request);
            if (result == null || result.getData().size() == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("No device of  type '" + type +
                        "' is currently enrolled with the server").build();
            }
            return Response.status(Response.Status.OK).entity(result.getData()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the devices of type '" + type + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @POST
    @Override
    public Response getDevices(List<DeviceIdentifier> deviceIds) {
        return null;
    }

    @GET
    @Override
    public Response getDeviceByUsername(@QueryParam("user") String user, @QueryParam("offset") int offset,
                                        @QueryParam("limit") int limit) {
        PaginationResult result;
        try {
            PaginationRequest request = new PaginationRequest(offset, limit);
            request.setOwner(user);
            result = DeviceMgtAPIUtils.getDeviceManagementService().getDevicesOfUser(request);
            if (result == null || result.getData().size() == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("No device has currently been " +
                        "enrolled by the user '" + user + "'").build();
            }
            return Response.status(Response.Status.OK).entity(result.getData()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the devices of user '" + user + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @GET
    @Override
    public Response getDevicesByRole(@QueryParam("roleName") String roleName, @QueryParam("offset") int offset,
                                     @QueryParam("limit") int limit) {
        List<Device> devices;
        try {
            devices = DeviceMgtAPIUtils.getDeviceManagementService().getAllDevicesOfRole(roleName);
            if (devices == null || devices.size() == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("No device has currently been " +
                        "enrolled under the role '" + roleName + "'").build();
            }
            return Response.status(Response.Status.OK).entity(devices).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the devices of the role '" + roleName + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @GET
    @Override
    public Response getDevicesByOwnership(@QueryParam("ownership") EnrolmentInfo.OwnerShip ownership,
                                          @QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        PaginationResult result;
        try {
            PaginationRequest request = new PaginationRequest(offset, limit);
            request.setOwnership(ownership.toString());
            result = DeviceMgtAPIUtils.getDeviceManagementService().getDevicesByOwnership(request);
            if (result == null || result.getData().size() == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("No device has currently been enrolled " +
                        "under the ownership scheme '" + ownership.toString() + "'").build();
            }
            return Response.status(Response.Status.OK).entity(result.getData()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the devices enrolled under the ownership scheme '" +
                    ownership.toString() + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @GET
    @Override
    public Response getDevicesByEnrollmentStatus(@QueryParam("status") EnrolmentInfo.Status status,
                                                 @QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        PaginationResult result;
        try {
            PaginationRequest request = new PaginationRequest(offset, limit);
            request.setStatus(status.toString());
            result = DeviceMgtAPIUtils.getDeviceManagementService().getDevicesByOwnership(request);
            if (result == null || result.getData().size() == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("No device is currently in enrollment " +
                        "status '" + status.toString() + "'").build();
            }
            return Response.status(Response.Status.OK).entity(result.getData()).build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the devices that carry the enrollment status '" +
                    status.toString() + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @GET
    @Override
    public Response getDevice(@QueryParam("type") String type, @QueryParam("id") String id) {
        Device device;
        try {
            DeviceManagementProviderService dms = DeviceMgtAPIUtils.getDeviceManagementService();
            device = dms.getDevice(new DeviceIdentifier(id, type));
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the device information.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        if (device == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Requested device of type '" + type +
                    "', which carries id '" + id + "' does not exist").build();
        }
        return Response.status(Response.Status.OK).entity(device).build();
    }

    @GET
    @Path("/{type}/{id}/location")
    @Override
    public Response getDeviceLocation(@PathParam("type") String type, @PathParam("id") String id) {
        DeviceInformationManager informationManager;
        DeviceLocation deviceLocation;
        try {
            informationManager = DeviceMgtAPIUtils.getDeviceInformationManagerService();
            deviceLocation = informationManager.getDeviceLocation(new DeviceIdentifier(id, type));
        } catch (DeviceDetailsMgtException e) {
            String msg = "Error occurred while getting the last updated location of the '" + type + "' device, " +
                    "which carries the id '" + id + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        return Response.status(Response.Status.OK).entity(deviceLocation).build();
    }

    @GET
    @Path("/{type}/{id}/features")
    @Override
    public Response getFeaturesOfDevice(@PathParam("type") String type, @PathParam("id") String id) {
        List<Feature> features;
        DeviceManagementProviderService dms;
        try {
            dms = DeviceMgtAPIUtils.getDeviceManagementService();
            features = dms.getFeatureManager(type).getFeatures();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the list of features of '" + type + "' device, which " +
                    "carries the id '" + id + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        if (features == null || features.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("No feature is currently associated " +
                    "with the '" + type + "' device, which carries the id '" + id + "'").build();
        }
        return Response.status(Response.Status.OK).entity(features).build();
    }

    @POST
    @Path("/search-devices")
    @Override
    public Response searchDevices(SearchContext searchContext) {
        SearchManagerService searchManagerService;
        List<DeviceWrapper> devices;
        try {
            searchManagerService = DeviceMgtAPIUtils.getSearchManagerService();
            devices = searchManagerService.search(searchContext);
        } catch (SearchMgtException e) {
            String msg = "Error occurred while searching for devices that matches the provided selection criteria";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        if (devices == null || devices.size() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("No device can be retrieved upon the provided " +
                    "selection criteria").build();
        }
        return Response.status(Response.Status.OK).entity(devices).build();
    }

    @GET
    @Path("/{type}/{id}/applications")
    @Override
    public Response getInstalledApplications(@PathParam("type") String type, @PathParam("id") String id) {
        List<Application> applications;
        ApplicationManagementProviderService amc;
        try {
            amc = DeviceMgtAPIUtils.getAppManagementService();
            applications = amc.getApplicationListForDevice(new DeviceIdentifier(id, type));
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while fetching the apps of the '" + type + "' device, which carries " +
                    "the id '" + id + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        return Response.status(Response.Status.OK).entity(applications).build();
    }

    @GET
    @Path("/{type}/{id}/operations")
    @Override
    public Response getDeviceOperations(@QueryParam("offset") int offset, @QueryParam("limit") int limit,
                                        @PathParam("type") String type, @PathParam("id") String id) {
        List<? extends Operation> operations;
        DeviceManagementProviderService dms;
        try {
            dms = DeviceMgtAPIUtils.getDeviceManagementService();
            operations = dms.getOperations(new DeviceIdentifier(id, type));
        } catch (OperationManagementException e) {
            String msg = "Error occurred while fetching the operations for the '" + type + "' device, which " +
                    "carries the id '" + id + "'";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
        return Response.status(Response.Status.OK).entity(operations).build();
    }

}