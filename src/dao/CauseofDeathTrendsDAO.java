package edu.ncsu.csc.itrust.dao.mysql;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.StringUtils;

import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.DBException;


public class CauseofDeathTrendsDAO {

    private DAOFactory factory;

    public CauseofDeathTrendsDAO(DAOFactory factory) {
        this.factory = factory;
    }

    /*
     * Returns a List of the top two common causes of Death
     * Returns cod for all patients if singleHCP is false
     * otherwise returns cod for the patients of HCP with ID hcpId
     * Takes All, Male, and Female for gender
     */
    public List<String> viewTopTwoMostCommonCausesofDeath(long hcpId, boolean singleHCP, String gender, int startYear, int endYear)
            throws DBException {
        Connection conn = null;
        PreparedStatement ps = null;
        List<String> topTwoCausesofDeath = new ArrayList<>();
        try {
            conn = factory.getConnection();
            String statement = "SELECT CauseOfDeath, COUNT(CauseOfDeath) as CauseOfDeathCnt FROM patients WHERE ";
            if(singleHCP){statement += "MID IN (SELECT DISTINCT PatientID FROM officevisits WHERE HCPID=(?)) AND ";}
            if(!gender.equalsIgnoreCase("All")){statement += "Gender=(?) AND ";}
            statement += "? <= YEAR(DateOfDeath) AND ? >= YEAR(DateOfDeath) GROUP BY CauseOfDeath ORDER BY CauseOfDeathCnt DESC;";
            int i = 1;
            ps = conn.prepareStatement( statement );
            if(singleHCP){ps.setLong(i++, hcpId);}
            if(!gender.equalsIgnoreCase("All")){ps.setString(i++, gender);}
            ps.setInt(i++, startYear);
            ps.setInt(i++, endYear);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            while (rs.next() && count < 2) {
                String diagnosisCode = rs.getString("CauseOfDeath");
                if (!StringUtils.isNullOrEmpty(diagnosisCode)) {
                    String descriptionOfDeath = this.displayDescriptionOfDeath(diagnosisCode);
                    topTwoCausesofDeath.add("Description of Death: " + descriptionOfDeath + " | Diagnosis Code: "
                            + diagnosisCode + " | Count of Deaths: " + rs.getString("CauseOfDeathCnt"));
                    count++;
                }
            }
            rs.close();

        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }

        return topTwoCausesofDeath;
    }

   
    // Returns the Description of Cause of Death for a Given ICDCode.
    public String displayDescriptionOfDeath(String code) throws DBException {

        Connection conn = null;
        PreparedStatement ps = null;
        String deathDescription = "";

        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT Description from icdcodes WHERE Code=(?);");
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                deathDescription = rs.getString("Description");
            }
            rs.close();
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }

        return deathDescription;
    }
}
