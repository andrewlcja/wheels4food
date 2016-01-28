/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.DemandDAO;
import dao.SupplyDAO;
import dao.UserDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import model.ApproveDemandRequest;
import model.ApproveDemandResponse;
import model.CreateDemandRequest;
import model.CreateDemandResponse;
import model.DeleteDemandResponse;
import model.Demand;
import model.GetUnavailableTimeslotsByDeliveryDateRequest;
import model.RejectDemandRequest;
import model.RejectDemandResponse;
import model.Supply;
import model.UpdateDemandResponse;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 *
 * @author andrew.lim.2013
 */
public class DemandService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    SupplyDAO supplyDAO;

    @Autowired
    DemandDAO demandDAO;

    public CreateDemandResponse createDemandRequest(CreateDemandRequest request) {
        int userID = request.getUserID();
        int supplyID = request.getSupplyID();
        String quantityDemandedStr = request.getQuantityDemanded().trim();
        String preferredDeliveryDateStr = request.getPreferredDeliveryDate();
        String preferredTimeslot = request.getPreferredTimeslot();
        String preferredSchedule = request.getPreferredSchedule();

        ArrayList<String> errorList = new ArrayList<String>();

        //validations
        if (userID <= 0) {
            errorList.add("Invalid user id");
        }

        if (supplyID <= 0) {
            errorList.add("Invalid supply id");
        }

        if (quantityDemandedStr.equals("")) {
            errorList.add("Quantity requested cannot be blank.");
        }

        if (preferredDeliveryDateStr.equals("")) {
            errorList.add("Preferred Delivery Date cannot be blank.");
        }

        if (preferredTimeslot.equals("")) {
            errorList.add("Preferred Timeslot requested cannot be blank.");
        }

        if (preferredSchedule.equals("")) {
            errorList.add("Preferred Schedule cannot be blank.");
        }

        //check if the errorlist is empty
        if (!errorList.isEmpty()) {
            return new CreateDemandResponse(false, errorList);
        }

        try {
            int quantityDemanded = Integer.parseInt(quantityDemandedStr);

            if (quantityDemanded <= 0) {
                errorList.add("Quantity requested must be more than 0");
            }

            if (preferredSchedule.equals("NA")) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date today = cal.getTime();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    Date preferredDeliveryDate = sdf.parse(preferredDeliveryDateStr);

                    if (today.compareTo(preferredDeliveryDate) >= 0 || (preferredDeliveryDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24) <= 3) {
                        errorList.add("Preferred Delivery Date must be a date at least 3 days after today");
                        return new CreateDemandResponse(false, errorList);
                    }
                } catch (ParseException e) {
                    errorList.add("Invalid Preferred Delivery Date");
                }
            } else {
                if (StringUtils.countOccurrencesOf(preferredSchedule, "1") < 5) {
                    errorList.add("A minimum of 5 timeslots must be selected.");
                }
            }

            try {
                User user = userDAO.getUserById(userID);
                if (user == null) {
                    errorList.add("Invalid user id");
                }

                Supply supply = supplyDAO.getSupplyById(supplyID);
                if (supply == null) {
                    errorList.add("Invalid supply id");
                }

                //check if the errorlist is empty
                if (!errorList.isEmpty()) {
                    return new CreateDemandResponse(false, errorList);
                }

                //get min and max of supply
                int minimum = supply.getMinimum();
                int maximum = supply.getMaximum();

                int quantitySupplied = supply.getQuantitySupplied();

                //check if quantity demanded is in the valid range
                if ((quantitySupplied - minimum < minimum) && quantityDemanded != quantitySupplied) {
                    errorList.add("Quantity requested must be equals to " + quantitySupplied);
                } else if (quantitySupplied - minimum == minimum && quantityDemanded != quantitySupplied && quantityDemanded != minimum) {
                    errorList.add("Quantity requested must be equals to " + minimum + " OR equals to " + maximum);
                } else if ((minimum + maximum) > quantitySupplied && (quantitySupplied - quantityDemanded) < minimum && (quantitySupplied - quantityDemanded) != 0) {
                    errorList.add("Quantity requested must be more than equals to " + minimum + " and less than equals to " + (quantitySupplied - minimum) + " OR equals to " + quantitySupplied);
                } else if (quantityDemanded > maximum || quantityDemanded < minimum) {
                    errorList.add("Quantity requested must be more than equals to " + minimum + " and less than equals to " + maximum);
                }

                //check if the errorlist is empty
                if (!errorList.isEmpty()) {
                    return new CreateDemandResponse(false, errorList);
                }

                //generate date requested
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                String dateRequested = sdf.format(today);

                demandDAO.createDemand(new Demand(user, supply, quantityDemanded, dateRequested, preferredDeliveryDateStr, preferredTimeslot, preferredSchedule, "Pending", ""));
                return new CreateDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new CreateDemandResponse(false, errorList);
            }

        } catch (NumberFormatException e) {
            errorList.add("Quantity must be an integer");
            return new CreateDemandResponse(false, errorList);
        }
    }

    public DeleteDemandResponse deleteDemandRequest(String idString) {
        ArrayList<String> errorList = new ArrayList<String>();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new DeleteDemandResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            try {
                Demand demand = demandDAO.getDemandById(id);
                Supply supply = demand.getSupply();

                if (demand.getStatus().equals("Pending")) {
                    supplyDAO.updateSupply(supply);
                }

                demandDAO.deleteDemand(id);
                return new DeleteDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new DeleteDemandResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new DeleteDemandResponse(false, errorList);
        }
    }

    public UpdateDemandResponse updateDemandRequest(Demand demand) {
        int quantityDemanded = demand.getQuantityDemanded();
        String preferredDeliveryDateStr = demand.getPreferredDeliveryDate();
        String preferredTimeslot = demand.getPreferredTimeslot();
        String preferredSchedule = demand.getPreferredSchedule();

        ArrayList<String> errorList = new ArrayList<String>();

        if (quantityDemanded <= 0) {
            errorList.add("Quantity requested must be more than 0");
        }

        if (preferredSchedule.equals("NA")) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date preferredDeliveryDate = sdf.parse(preferredDeliveryDateStr);

                if (today.compareTo(preferredDeliveryDate) >= 0 || (preferredDeliveryDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24) <= 3) {
                    errorList.add("Preferred Delivery Date must be a date at least 3 days after today");
                    return new UpdateDemandResponse(false, errorList);
                }
            } catch (ParseException e) {
                errorList.add("Invalid Preferred Delivery Date");
            }
        } else {
            if (StringUtils.countOccurrencesOf(preferredSchedule, "1") < 5) {
                errorList.add("A minimum of 5 timeslots must be selected.");
            }
        }

        try {
            //get min and max of supply
            int minimum = demand.getSupply().getMinimum();
            int maximum = demand.getSupply().getMaximum();

            int quantitySupplied = demand.getSupply().getQuantitySupplied();

            //check if quantity demanded is in the valid range
            if ((quantitySupplied - minimum < minimum) && quantityDemanded != quantitySupplied) {
                errorList.add("Quantity requested must be equals to " + quantitySupplied);
            } else if (quantitySupplied - minimum == minimum && quantityDemanded != quantitySupplied && quantityDemanded != minimum) {
                errorList.add("Quantity requested must be equals to " + minimum + " OR equals to " + maximum);
            } else if ((minimum + maximum) > quantitySupplied && (quantitySupplied - quantityDemanded) < minimum && (quantitySupplied - quantityDemanded) != 0) {
                errorList.add("Quantity requested must be more than equals to " + minimum + " and less than equals to " + (quantitySupplied - minimum) + " OR equals to " + quantitySupplied);
            } else if (quantityDemanded > maximum || quantityDemanded < minimum) {
                errorList.add("Quantity requested must be more than equals to " + minimum + " and less than equals to " + maximum);
            }

            //check if the errorlist is empty
            if (!errorList.isEmpty()) {
                return new UpdateDemandResponse(false, errorList);
            }
            
            demandDAO.updateDemand(demand);
            return new UpdateDemandResponse(true, null);
        } catch (Exception e) {
            errorList.add(e.getMessage());
            return new UpdateDemandResponse(false, errorList);
        }
    }

    public ApproveDemandResponse approveDemandRequest(String idString, ApproveDemandRequest request) {
        ArrayList<String> errorList = new ArrayList<String>();

        String comments = request.getComments();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new ApproveDemandResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            Demand demand = demandDAO.getDemandById(id);
            String status = demand.getStatus();

            if (!status.equals("Pending")) {
                errorList.add("Status of request must be pending");
                return new ApproveDemandResponse(false, errorList);
            }

            demand.setStatus("Approved");

            Supply supply = demand.getSupply();
            int quantitySupplied = supply.getQuantitySupplied();
            int quantityDemanded = demand.getQuantityDemanded();

            if (quantityDemanded <= quantitySupplied) {
                supply.setQuantitySupplied(quantitySupplied - quantityDemanded);
            } else {
                if (comments.equals("")) {
                    errorList.add("Comments cannot be blank");
                    return new ApproveDemandResponse(false, errorList);
                }

                demand.setComments(comments);
                supply.setQuantitySupplied(0);
            }

            try {
                supplyDAO.updateSupply(supply);
                demandDAO.updateDemand(demand);
                return new ApproveDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new ApproveDemandResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new ApproveDemandResponse(false, errorList);
        }
    }

    public RejectDemandResponse rejectDemandRequest(String idString, RejectDemandRequest request) {
        ArrayList<String> errorList = new ArrayList<String>();

        String comments = request.getComments();

        if (idString.equals("")) {
            errorList.add("Id cannot be blank");
            return new RejectDemandResponse(false, errorList);
        }

        if (comments.equals("")) {
            errorList.add("Comments cannot be blank");
            return new RejectDemandResponse(false, errorList);
        }

        try {
            int id = Integer.parseInt(idString);

            Demand demand = demandDAO.getDemandById(id);
            String status = demand.getStatus();
            Supply supply = demand.getSupply();

            if (!status.equals("Pending")) {
                errorList.add("Status of request must be pending");
                return new RejectDemandResponse(false, errorList);
            }

            demand.setComments(comments);
            demand.setStatus("Rejected");

            try {
                demandDAO.updateDemand(demand);
                return new RejectDemandResponse(true, null);
            } catch (Exception e) {
                errorList.add(e.getMessage());
                return new RejectDemandResponse(false, errorList);
            }
        } catch (NumberFormatException e) {
            errorList.add("Id must be an integer");
            return new RejectDemandResponse(false, errorList);
        }
    }

    public List<Demand> getDemandListByUserIdRequest(int userID) throws Exception {
        return demandDAO.getDemandListByUserId(userID);
    }

    public List<Demand> getDemandListBySupplyIdRequest(int supplyID) throws Exception {
        return demandDAO.getDemandListBySupplyId(supplyID);
    }

    public List<Demand> getPendingDemandListBySupplierIdRequest(int supplierID) throws Exception {
        return demandDAO.getPendingDemandListBySupplierId(supplierID);
    }

    public List<String> getUnavailableTimeslotsByDeliveryDateRequest(GetUnavailableTimeslotsByDeliveryDateRequest request) throws Exception {
        List<Demand> demandList = demandDAO.getDemandListByDeliveryDate(request.getSupplierID(), request.getDeliveryDate());

        List<String> unavailableTimeslots = new ArrayList<String>();

        for (Demand d : demandList) {
            unavailableTimeslots.add(d.getPreferredTimeslot());
        }

        return unavailableTimeslots;
    }

    public Demand getDemandByIdRequest(int id) throws Exception {
        return demandDAO.getDemandById(id);
    }
}
