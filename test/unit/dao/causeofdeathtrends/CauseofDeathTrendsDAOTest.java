package edu.ncsu.csc.itrust.unit.dao.causeofdeathtrends;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.CauseofDeathTrendsDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.DBBuilder;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;


public class CauseofDeathTrendsDAOTest extends TestCase {

	private DAOFactory factory = TestDAOFactory.getTestInstance();
	TestDataGenerator gen = new TestDataGenerator();
	private CauseofDeathTrendsDAO cod = new CauseofDeathTrendsDAO(factory);

	@Before
	public void setUp() throws Exception {
		gen.clearAllTables();
		gen.deadpatient1();
		gen.deadpatient2();
		gen.deadpatient3();
		gen.deadpatient4();
		gen.deadpatient5();
		gen.deadpatient6();
		gen.deadpatient7();
		gen.deadpatient8();
		gen.deadpatient9();
		gen.deadpatient10();
		gen.icd9cmCodes();
	}

	@Test
	public void testDisplayDescriptionofDeath() throws DBException, ITrustException {
		assertTrue(cod.displayDescriptionOfDeath("72").equalsIgnoreCase("Mumps"));
	}

	@Test
    public void testGetAllReport() throws DBException, ITrustException {
        assertEquals(2, cod.viewTopTwoMostCommonCausesofDeath(0, false, "All", 1980, 2025).size());
    }
    public void testGetMyReport() throws DBException, ITrustException {
        assertEquals(2, cod.viewTopTwoMostCommonCausesofDeath(9000000000L, true, "All", 1980, 2025).size());
    }


}
