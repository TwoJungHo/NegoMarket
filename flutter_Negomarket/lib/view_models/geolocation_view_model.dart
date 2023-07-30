import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';

class GeolocationViewModel extends ChangeNotifier {
  LocationPermission _permissionStatus = LocationPermission.denied;
  bool _isLoading = false;
  double _longitude = 0.0;
  double _latitude = 0.0;



  Future<void> checkPermission() async {
    _isLoading = true;

    final isLocationEnabled = await Geolocator.isLocationServiceEnabled();

    if (!isLocationEnabled) {
      throw Exception('위치 서비스를 활성화해주세요.');
    }

    _permissionStatus = await Geolocator.checkPermission();

    if (_permissionStatus == LocationPermission.denied) {
      _permissionStatus = await Geolocator.requestPermission();

      if (_permissionStatus == LocationPermission.denied) {
        throw Exception('위치 권한을 허가해주세요.');
      }
    }

    Position currentPosition = await Geolocator.getCurrentPosition();

    _longitude = currentPosition.longitude;
    _latitude = currentPosition.latitude;

    _isLoading = false;
    notifyListeners();
  }

  bool get isLoading => _isLoading;

  set isLoading(bool value) {
    _isLoading = value;
  }


  double get longitude => _longitude;

  set longitude(double value) {
    _longitude = value;
  }

  double get latitude => _latitude;

  set latitude(double value) {
    _latitude = value;
  }
}
