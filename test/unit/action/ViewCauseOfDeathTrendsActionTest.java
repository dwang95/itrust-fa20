package edu.ncsu.csc.itrust.unit.action;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

import edu.ncsu.csc.itrust.action.ViewCauseOfDeathTrendsAction;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.CauseofDeathTrendsDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.EvilDAOFactory;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

/**
 * ViewCauseOfDeathTrendsActionTest
 */
public class ViewCauseOfDeathTrendsActionTest extends TestCase {

	private ViewCauseOfDeathTrendsAction action;
	private ViewCauseOfDeathTrendsAction action1;
	private ViewCauseOfDeathTrendsAction evilAction;
	private DAOFactory factory;
	private DAOFactory evilFactory;
    private long hcpId1 = 9000000000L;
    private long hcpId2 = 9000000003L;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TestDataGenerator gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.standardData();

		this.factory = TestDAOFactory.getTestInstance();
		this.evilFactory = EvilDAOFactory.getEvilInstance();
		this.action = new ViewCauseOfDeathTrendsAction(this.factory, this.hcpId1);
		this.action1 = new ViewCauseOfDeathTrendsAction(this.factory, this.hcpId2);
		this.evilAction = new ViewCauseOfDeathTrendsAction(this.evilFactory, this.hcpId1);
    }
    
    public void testGetAllDeaths() throws SQLException, DBException {
		List<String> resList = action.getAllTopCausesOfDeath(1970, 2030, "All");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 5", resList.get(0));
        assertEquals("Description of Death: Echovirus | Diagnosis Code: 79.10 | Count of Deaths: 2", resList.get(1));
    }

    public void testGetAllDeathsMale() throws SQLException, DBException {
		List<String> resList = action.getAllTopCausesOfDeath(1970, 2030, "Male");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4", resList.get(0));
        assertEquals("Description of Death: Tuberculosis of vertebral column | Diagnosis Code: 15.00 | Count of Deaths: 1", resList.get(1));
    }

    public void testGetAllDeathsFemale() throws SQLException, DBException {
		List<String> resList = action.getAllTopCausesOfDeath(1970, 2030, "Female");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1", resList.get(0));
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 1", resList.get(1));
    }
    
    public void testGetAllDeathsNew() throws SQLException, DBException {
		List<String> resList = action.getAllTopCausesOfDeath(2018, 2030, "All");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4", resList.get(0));
        assertEquals("Description of Death: Echovirus | Diagnosis Code: 79.10 | Count of Deaths: 2", resList.get(1));
    }
    
    public void testGetAllDeathsHCP1() throws SQLException, DBException {
		List<String> resList = action.getMyTopCausesOfDeath(1970, 2030, "All");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4", resList.get(0));
        assertEquals("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1", resList.get(1));
    }

    public void testGetAllDeathsHCP1New() throws SQLException, DBException {
		List<String> resList = action.getMyTopCausesOfDeath(2018, 2030, "All");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 3", resList.get(0));
        assertEquals("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1", resList.get(1));
    }

    public void testGetAllDeathsHCP1Male() throws SQLException, DBException {
		List<String> resList = action.getMyTopCausesOfDeath(1970, 2030, "Male");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4", resList.get(0));
        assertEquals("Description of Death: Echovirus | Diagnosis Code: 79.10 | Count of Deaths: 1", resList.get(1));
    }

    public void testGetAllDeathsHCP1Female() throws SQLException, DBException {
		List<String> resList = action.getMyTopCausesOfDeath(1970, 2030, "Female");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1", resList.get(0));
        assertEquals("No Further Deaths", resList.get(1));
    }

    public void testGetAllDeathsHCP2() throws SQLException, DBException {
		List<String> resList = action1.getMyTopCausesOfDeath(1970, 2030, "All");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 2", resList.get(0));
        assertEquals("Description of Death: Tuberculosis of the lung | Diagnosis Code: 11.40 | Count of Deaths: 1", resList.get(1));
    }

    public void testGetAllDeathsHCP2New() throws SQLException, DBException {
		List<String> resList = action1.getMyTopCausesOfDeath(2018, 2030, "All");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 1", resList.get(0));
        assertEquals("Description of Death: Tuberculosis of the lung | Diagnosis Code: 11.40 | Count of Deaths: 1", resList.get(1));
    }

    public void testGetAllDeathsHCP2Male() throws SQLException, DBException {
		List<String> resList = action1.getMyTopCausesOfDeath(1970, 2030, "Male");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 1", resList.get(0));
        assertEquals("Description of Death: Tuberculosis of vertebral column | Diagnosis Code: 15.00 | Count of Deaths: 1", resList.get(1));
    }

    public void testGetAllDeathsHCP2Female() throws SQLException, DBException {
		List<String> resList = action1.getMyTopCausesOfDeath(1970, 2030, "Female");
        assertEquals(2, resList.size());
        assertEquals("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 1", resList.get(0));
        assertEquals("Description of Death: Tuberculosis of the lung | Diagnosis Code: 11.40 | Count of Deaths: 1", resList.get(1));
    }

    public void testDatesOutoforder() throws SQLException, DBException {
		List<String> resList = action.getAllTopCausesOfDeath(2030, 1970, "All");
        assertEquals(1, resList.size());
        assertEquals("No Deaths to Display", resList.get(0));
    }

    public void testDatesOutoforderHCP1() throws SQLException, DBException {
		List<String> resList = action.getMyTopCausesOfDeath(2030, 1970, "All");
        assertEquals(1, resList.size());
        assertEquals("No Deaths to Display For 9000000000", resList.get(0));
    }

    public void testWrongPopulation() throws SQLException, DBException {
		List<String> resList = action.getAllTopCausesOfDeath(2030, 1970, "None");
        assertEquals(1, resList.size());
        assertEquals("No Deaths to Display", resList.get(0));
    }
}