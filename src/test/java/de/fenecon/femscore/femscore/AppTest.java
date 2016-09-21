package de.fenecon.femscore.femscore;

import java.util.ArrayList;
import java.util.List;

import io.openems.device.ess.Ess;
import io.openems.device.ess.EssCluster;
import io.openems.element.InvalidValueExcecption;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	private EssCluster storage;
	private List<Ess> storages;

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
		storages = new ArrayList<>();
		storages.add(new TestEss(15, 20000, 30, 40000, 40000, -40000));
		storages.add(new TestEss(23, 4000, 75, 12000, 9000, -9000));
		storages.add(new TestEss(40, 6300, 95, 9000, 5000, -5000));
		storages.add(new TestEss(60, 1600, 23, 3000, 2500, -2500));
		storages.add(new TestEss(20, 800, 43, 10000, 8700, -8700));
		storage = new EssCluster("cluster", "", 0, 20, storages);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	public void testDischargeSplitting() {
		try {
			int power = 0;
			storage.setActivePower(power);
			Ess e1 = storages.get(0);
			Ess e2 = storages.get(1);
			Ess e3 = storages.get(2);
			Ess e4 = storages.get(3);
			Ess e5 = storages.get(4);
			assertTrue(e1.getActivePower() == 0);
			assertTrue(e2.getActivePower() == 0);
			assertTrue(e3.getActivePower() == 0);
			assertTrue(e4.getActivePower() == 0);
			assertTrue(e5.getActivePower() == 0);
			// power > Max
			power = 70000;
			storage.setActivePower(power);
			assertTrue(e1.getActivePower() == 0);
			assertTrue(e2.getActivePower() == e2.getAllowedDischarge());
			assertTrue(e3.getActivePower() == e3.getAllowedDischarge());
			assertTrue(e4.getActivePower() == e4.getAllowedDischarge());
			assertTrue(e5.getActivePower() == e5.getAllowedDischarge());
			power = 12000;
			storage.setActivePower(power);
			assertTrue(e1.getActivePower() == 0);
			assertTrue(e2.getActivePower() == 1300);
			assertTrue(e3.getActivePower() == 1400);
			assertTrue(e4.getActivePower() == 6000);
			assertTrue(e5.getActivePower() == 3300);
			assertTrue(sumActivePower() == power);
		} catch (InvalidValueExcecption e) {
			e.printStackTrace();
		}
	}

	public void testChargeSplitting() {
		try {
			int power = 0;
			storage.setActivePower(power);
			Ess e1 = storages.get(0);
			Ess e2 = storages.get(1);
			Ess e3 = storages.get(2);
			Ess e4 = storages.get(3);
			Ess e5 = storages.get(4);
			assertTrue(e1.getActivePower() == 0);
			assertTrue(e2.getActivePower() == 0);
			assertTrue(e3.getActivePower() == 0);
			assertTrue(e4.getActivePower() == 0);
			assertTrue(e5.getActivePower() == 0);
			// power > Max
			power = -70000;
			storage.setActivePower(power);
			assertTrue(e1.getActivePower() == e1.getAllowedCharge());
			assertTrue(e2.getActivePower() == e2.getAllowedCharge());
			assertTrue(e3.getActivePower() == e3.getAllowedCharge());
			assertTrue(e4.getActivePower() == e4.getAllowedCharge());
			assertTrue(e5.getActivePower() == e5.getAllowedCharge());
			power = -12000;
			storage.setActivePower(power);
			assertTrue(e5.getActivePower() == -600);
			assertTrue(e4.getActivePower() == -1200);
			assertTrue(e3.getActivePower() == -1800);
			assertTrue(e1.getActivePower() == -1900);
			assertTrue(e2.getActivePower() == -6500);
			assertTrue(sumActivePower() == power);
		} catch (InvalidValueExcecption e) {
			e.printStackTrace();
		}
	}

	public int sumActivePower() throws InvalidValueExcecption {
		int power = 0;
		for (Ess storage : storages) {
			power += storage.getActivePower();
		}
		return power;
	}

}
