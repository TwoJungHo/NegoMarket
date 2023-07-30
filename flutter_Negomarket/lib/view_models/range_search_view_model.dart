import 'dart:convert';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:http/http.dart' as http;
import 'package:flutter/material.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:used_market/data_types/sell_data.dart';

class RangeListViewModel extends ChangeNotifier {
  double _longitude = 0.0;
  double _latitude = 0.0;
  Set<Marker> _markers = {};
  Set<Marker> get markers => _markers;

  set markers(Set<Marker> value) {
    _markers = value;
    notifyListeners();
  }

  double get longitude => _longitude;

  set longitude(double value) {
    _longitude = value;
    notifyListeners();

  }

  double get latitude => _latitude;

  set latitude(double value) {
    _latitude = value;
    notifyListeners();

  }

  int _page = 1;
  double _searchRange = 5.0;
  double _previousSearchRange = 5.0;
  bool _isLoading = false;
  List<Sell> _sellList = [];

  int get page => _page;

  set page(int value) {
    _page = value;
    notifyListeners();

  }

  double get searchRange => _searchRange;

  set searchRange(double value) {
    _searchRange = value;
    notifyListeners();
  }

  List<Sell> get sellList => _sellList;

  set sellList(List<Sell> value) {
    _sellList = value;
    notifyListeners();

  }

  bool get isLoading => _isLoading;

  set isLoading(bool value) {
    _isLoading = value;
    notifyListeners();

  }

  double get previousSearchRange => _previousSearchRange;

  set previousSearchRange(double value) {
    _previousSearchRange = value;
    notifyListeners();
  }

  Future<void> loadData() async {

      isLoading = true;
      Set<Marker> temporaryMarkers = {};

      String url =
          '${Constants.api_gate_url}/sell-service/sells/m/findbyrange/page?range=$searchRange&longitude=$longitude&latitude=$latitude&page=$page';
      final response = await http.get(
        Uri.parse(url),
        headers: {
          "Content-Type": "application/json",
        },
      );

      sellList.addAll(
          (jsonDecode(utf8.decode(response.bodyBytes)) as List)
              .map((data) => Sell.fromJson(data))
              .toList());


      BitmapDescriptor markerIcon = await BitmapDescriptor.fromAssetImage(
          ImageConfiguration(

          ), 'assets/images/marker.png');


      for (var sell in sellList) {
       final marker = Marker(
          markerId:MarkerId(sell.id),
          position: LatLng(sell.latitude, sell.longitude),
          icon: markerIcon
       );

       temporaryMarkers.add(marker);
      }

      page++;
      _markers = temporaryMarkers;
      isLoading = false;
      notifyListeners();

  }


}
