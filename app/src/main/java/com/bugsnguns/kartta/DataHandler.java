package com.bugsnguns.kartta;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import java.util.ArrayList;


/**
 * Created by Antonio on 14.01.2017.
 */

public class DataHandler {
    //класс содержит методы для обработки и анализа данных

    public boolean duplication(LatLng currentLocation, LatLng previousLocation) {

        //если объекты одинаковые, то и хеш-коды одинаковые
        if (currentLocation.hashCode() == previousLocation.hashCode()) {
            return true;
        } else return false;

    }

    public static void toDraw(Polyline polyline, ArrayList<LatLng> list) {
        polyline.setPoints(list);
        list.clear();
    }

    //сохраняет новую информацию о местоположении в массив
    public void receiveData(Location location, DataStorage dataStorage, boolean isRecording) {
        dataStorage.mCurrentLocation = location;

        //добавляем координаты в массив
        if (isRecording) {
            dataStorage.geoLocationList.add(new LatLng(location.getLatitude(), location.getLongitude()));
            dataStorage.geoLocationListSize++;
        }
    }

    //забирает из одного экземпляра ArrayList неповторяющиеся точки и передает их в другой экземпляр
    //ArrayList
    public void makeUniqueLocations(DataStorage dataStorage, DataHandler dataHandler) {
        if (dataStorage.geoLocationListSize > 1) {
            if (!dataHandler.duplication(dataStorage.geoLocationList.get(dataStorage.geoLocationList.size() - 1),
                    dataStorage.geoLocationList.get(dataStorage.geoLocationList.size() - 2))) {
                dataStorage.locationsForMap.add(dataStorage.geoLocationList.get(dataStorage.geoLocationList.size() - 1));
                dataStorage.locationsForMapSize++;
            }

        } else if (dataStorage.geoLocationListSize == 1) {
            dataStorage.locationsForMap.add(dataStorage.geoLocationList.get(0));
            dataStorage.locationsForMapSize++;
        }
    }

    //метод добавляет точки в экземпляр Polyline route
    public void toDrawRoute (Polyline route, ArrayList<LatLng> list, boolean isDrawing) {
        if (isDrawing) {
            route.setPoints(list);
        }
    }

    //метод устанавливает маркер в стартовой точке
    public void putStartMarker () {

    }

}
