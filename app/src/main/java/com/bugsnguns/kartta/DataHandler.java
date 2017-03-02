package com.bugsnguns.kartta;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import java.util.ArrayList;


/**
 * Created by Antonio on 14.01.2017.
 */

public class DataHandler {
    //класс содержит методы для обработки и анализа данных
    private static final String TAG = "DataHandler";

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
    public void receiveData(DataStorage dataStorage, boolean isRecording, boolean onceStarted) {


        //добавляем координаты в массив
        if (isRecording) {
            dataStorage.geoLocationList.add(new LatLng(dataStorage.mCurrentLocation.getLatitude(),
                    dataStorage.mCurrentLocation.getLongitude()));
            dataStorage.geoLocationListSize++;
        }

        //если еще не была нажата кгопка Start, всегда записываем обновленную лакацию в mCurrentLocation
        if (!onceStarted) {
            dataStorage.geoLocationList.add(0, new LatLng(dataStorage.mCurrentLocation.getLatitude(),
                    dataStorage.mCurrentLocation.getLongitude()));
            dataStorage.geoLocationListSize = 1;

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
            Log.v(TAG, "toDrawRoute() is called");
        if (isDrawing) {
            Log.v(TAG, "is Drawing = true. Going to draw the route");
            route.setPoints(list);
            Log.v(TAG, "route drawn successfully");
        }
    }

    //метод удаляет маркер Finish, емли он ранее был установлен
    public void removeFinishMarker (DataStorage dataStorage) {
        if (dataStorage.finishMarker != null) {
            dataStorage.finishMarker.remove();
        }
    }

    //метод устанавливает маркер в стартовой точке
    public void putStartMarker (DataStorage dataStorage) {
        if (dataStorage.startLocation == null) {

            Log.v(TAG, "startLocation is null");

            dataStorage.startLocation = new LatLng(dataStorage.locationsForMap.get(0).latitude,
                    dataStorage.locationsForMap.get(0).longitude);
            MapsActivity.mMap.addMarker(new MarkerOptions().position(dataStorage.startLocation).title("Start"));
        }
    }

    //метод устанавливает маркер в финишной точке
    public void putFinishMarker (DataStorage dataStorage) {
        //если маркет был ранее создан, удаляем предыдущую версию
        removeFinishMarker(dataStorage);

        //отмечаем финальное местоположение на маршруте и ставим соответствующий маркер
        dataStorage.finalLocation = new LatLng(dataStorage.locationsForMap.get(dataStorage.locationsForMap.size() - 1).latitude,
                dataStorage.locationsForMap.get(dataStorage.locationsForMap.size() - 1).longitude);
        dataStorage.finishMarker = MapsActivity.mMap.addMarker(new MarkerOptions()
                .position(dataStorage.finalLocation)
                .title("Finish"));



    }

}
