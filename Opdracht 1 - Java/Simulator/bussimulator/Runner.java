package bussimulator;

import java.util.*;

import tijdtools.TijdFuncties;

public class Runner implements Runnable {

    private static HashMap<Integer, ArrayList<Bus>> busStart = new HashMap<Integer, ArrayList<Bus>>();
    private static ArrayList<Bus> actieveBussen = new ArrayList<Bus>();
    private static int interval = 1000;
    private static int syncInterval = 5;

    private static void addBus(int starttijd, Bus bus) {
        ArrayList<Bus> bussen = new ArrayList<Bus>();
        if (busStart.containsKey(starttijd)) {
            bussen = busStart.get(starttijd);
        }
        bussen.add(bus);
        busStart.put(starttijd, bussen);
        bus.setbusID(starttijd);
    }

    private static int startBussen(int tijd) {
        for (Bus bus : busStart.get(tijd)) {
            actieveBussen.add(bus);
        }
        busStart.remove(tijd);
        return (!busStart.isEmpty()) ? Collections.min(busStart.keySet()) : -1;
    }

    public static void moveBussen(int nu) {
        Iterator<Bus> itr = actieveBussen.iterator();
        while (itr.hasNext()) {
            Bus bus = itr.next();
            boolean eindpuntBereikt = bus.move();
            if (eindpuntBereikt) {
                bus.sendLastETA(nu);
                itr.remove();
            }
        }
    }

    public static void sendETAs(int nu) {
        Iterator<Bus> itr = actieveBussen.iterator();
        while (itr.hasNext()) {
            Bus bus = itr.next();
            bus.sendETAs(nu);
        }
    }

    public static int initBussen() {
        List<BusInfo> busInfoList = new ArrayList<>();

        busInfoList.add(new BusInfo(Lijnen.LIJN1, Bedrijven.ARRIVA, 3));
        busInfoList.add(new BusInfo(Lijnen.LIJN2, Bedrijven.ARRIVA, 5));
        busInfoList.add(new BusInfo(Lijnen.LIJN3, Bedrijven.ARRIVA, 4));
        busInfoList.add(new BusInfo(Lijnen.LIJN4, Bedrijven.ARRIVA, 6));
        busInfoList.add(new BusInfo(Lijnen.LIJN5, Bedrijven.FLIXBUS, 3));
        busInfoList.add(new BusInfo(Lijnen.LIJN6, Bedrijven.QBUZZ, 5));
        busInfoList.add(new BusInfo(Lijnen.LIJN7, Bedrijven.QBUZZ, 4));
        busInfoList.add(new BusInfo(Lijnen.LIJN1, Bedrijven.ARRIVA, 6));
        busInfoList.add(new BusInfo(Lijnen.LIJN4, Bedrijven.ARRIVA, 12));
        busInfoList.add(new BusInfo(Lijnen.LIJN5, Bedrijven.FLIXBUS, 10));
        busInfoList.add(new BusInfo(Lijnen.LIJN8, Bedrijven.QBUZZ, 3));
        busInfoList.add(new BusInfo(Lijnen.LIJN8, Bedrijven.QBUZZ, 5));
        busInfoList.add(new BusInfo(Lijnen.LIJN3, Bedrijven.ARRIVA, 14));
        busInfoList.add(new BusInfo(Lijnen.LIJN4, Bedrijven.ARRIVA, 16));
        busInfoList.add(new BusInfo(Lijnen.LIJN5, Bedrijven.FLIXBUS, 13));

        for (BusInfo busInfo : busInfoList) {
            addBus(busInfo.starttijd, new Bus(busInfo.lijn, busInfo.bedrijf, 1));
            addBus(busInfo.starttijd, new Bus(busInfo.lijn, busInfo.bedrijf, -1));
        }

        return Collections.min(busStart.keySet());
    }

//    @Override
//    public void run() {
//        int tijd = 0;
//        int volgende = initBussen();
//        while ((volgende >= 0) || !actieveBussen.isEmpty()) {
//            System.out.println("De tijd is:" + tijd);
//            volgende = (tijd == volgende) ? startBussen(tijd) : volgende;
//            moveBussen(tijd);
//            sendETAs(tijd);
//            try {
//                Thread.sleep(interval);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            tijd++;
//        }
//    }

//	Om de tijdsynchronisatie te gebruiken moet de onderstaande run() gebruikt worden
//
	@Override
	public void run() {
		int tijd=0;
		int counter=0;
		TijdFuncties tijdFuncties = new TijdFuncties();
		tijdFuncties.initSimulatorTijden(interval,syncInterval);
		int volgende = initBussen();
		while ((volgende>=0) || !actieveBussen.isEmpty()) {
			counter=tijdFuncties.getCounter();
			tijd=tijdFuncties.getTijdCounter();
			System.out.println("De tijd is:" + tijdFuncties.getSimulatorWeergaveTijd());
			volgende = (counter==volgende) ? startBussen(counter) : volgende;
			moveBussen(tijd);
			sendETAs(tijd);
			try {
				tijdFuncties.simulatorStep();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

    private static class BusInfo {
        Lijnen lijn;
        Bedrijven bedrijf;
        int starttijd;

        BusInfo(Lijnen lijn, Bedrijven bedrijf, int starttijd) {
            this.lijn = lijn;
            this.bedrijf = bedrijf;
            this.starttijd = starttijd;
        }
    }
}
