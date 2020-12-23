package edu.ncsu.csc.itrust.action;

import java.sql.Date;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.CauseofDeathTrendsDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;

/**
 * Action class for ViewCauseOfDeathTrends.jsp
 */
public class ViewCauseOfDeathTrendsAction {
    private long loggedInMID;
    private CauseofDeathTrendsDAO codDAO;

    /**
     * Set up defaults
     */
    public ViewCauseOfDeathTrendsAction(DAOFactory factory, long loggedInMID) {
        this.loggedInMID = loggedInMID;
        this.codDAO = factory.getCauseOfDeathTrendsDAO();
    }

    // Gets top 2 causes of death for logged in HCP
    public List<String> getMyTopCausesOfDeath(int startYear, int endYear, String gender) throws DBException {
        try {
            List<String> res = codDAO.viewTopTwoMostCommonCausesofDeath(this.loggedInMID, true, gender, startYear, endYear);
            if(res.size()==0){res.add("No Deaths to Display For "+this.loggedInMID);}
            else if (res.size()==1){res.add("No Further Deaths");}
            return res;
        } catch (ITrustException e) {
            e.printStackTrace();
            List<String> errorRes = new ArrayList<>();
            errorRes.add("Error Error, Error Error, Error Error");
            errorRes.add(e.getMessage());
            return errorRes;
        }

    }

    // Get twop 2 causes of death for all patients in database
    public List<String> getAllTopCausesOfDeath(int startYear, int endYear, String gender) throws DBException {
        try {
            List<String> res = codDAO.viewTopTwoMostCommonCausesofDeath(0, false, gender, startYear, endYear);
            if(res.size()==0){res.add("No Deaths to Display");}
            else if (res.size()==1){res.add("No Further Deaths");}
            return res;
        } catch (ITrustException e) {
            e.printStackTrace();
            List<String> errorRes = new ArrayList<>();
            errorRes.add("Error Error, Error Error, Error Error");
            return errorRes;
        }
    }
}