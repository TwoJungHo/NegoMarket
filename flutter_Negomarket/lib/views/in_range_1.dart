import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:geolocator/geolocator.dart';
import 'package:provider/provider.dart';
import 'package:used_market/view_models/range_search_view_model.dart';
import 'package:used_market/views/in_range_list.dart';

class InRange extends StatefulWidget {
  const InRange({Key? key}) : super(key: key);

  @override
  State<InRange> createState() => _InRangeState();
}

class _InRangeState extends State<InRange> {
  Future<LocationPermission>? permissionStatus;

  @override
  void initState() {
    super.initState();
    permissionStatus = _checkPermission();
  }

  Future<LocationPermission> _checkPermission() async {
    final isLocationEnabled = await Geolocator.isLocationServiceEnabled();

    if (!isLocationEnabled) {
      throw Exception('위치 서비스를 활성화해주세요.');
    }

    LocationPermission checkedPermission = await Geolocator.checkPermission();

    if (checkedPermission == LocationPermission.denied) {
      checkedPermission = await Geolocator.requestPermission();

      if (checkedPermission == LocationPermission.denied) {
        throw Exception('위치 권한을 허가해주세요.');
      }
    }

    return checkedPermission;
  }

  Future<LatLng> _getLatLng() async {
    final currentPosition = await Geolocator.getCurrentPosition();
    final longitude = currentPosition.longitude;
    final latitude = currentPosition.latitude;
    final rangeListViewModel = context.read<RangeListViewModel>();
    rangeListViewModel.longitude = longitude;
    rangeListViewModel.latitude = latitude;

    return LatLng(latitude, longitude);
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<LocationPermission>(
      future: permissionStatus,
      builder:
          (BuildContext context, AsyncSnapshot<LocationPermission> snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return CircularProgressIndicator(); // or other loading widget
        } else if (snapshot.hasError) {
          return Text('Error: ${snapshot.error}');
        } else if (snapshot.data != LocationPermission.whileInUse &&
            snapshot.data != LocationPermission.always) {
          return Text('위치 권한을 허가해주세요.');
        } else {
          return FutureBuilder<LatLng>(
            future: _getLatLng(),
            builder: (BuildContext context, AsyncSnapshot<LatLng> snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return CircularProgressIndicator(); // or other loading widget
              } else if (snapshot.hasError) {
                return Text('Error: ${snapshot.error}');
              } else {
                return Column(
                  children: [
                    SlideBar(),
                    Container(
                      margin: EdgeInsets.all(5.0),
                      decoration: BoxDecoration(
                          border: Border.all(
                              color: Color.fromRGBO(190, 10, 10, 0.8)),
                          borderRadius: BorderRadius.circular(50.0)),
                      height: MediaQuery.of(context).size.height*0.3,
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(50.0),

                        // 구글 맵!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        child: GoogleMap(
                          initialCameraPosition: CameraPosition(
                            target: snapshot.data!,
                            zoom: 10,
                          ),
                          circles: {
                            Circle(
                              circleId: CircleId("circle_1"),
                              center: snapshot.data!,
                              radius: 1000 *
                                  Provider.of<RangeListViewModel>(context)
                                      .searchRange,
                              strokeWidth: 1,
                              strokeColor: Colors.black,
                              fillColor: Colors.black.withOpacity(0.3),
                            )
                          },
                          markers:
                              Provider.of<RangeListViewModel>(context).markers,
                        ),
                      ),
                    ),
                    InRangeList(latLng: snapshot.data!),
                  ],
                );
              }
            },
          );
        }
      },
    );
  }
}


class SlideBar extends StatelessWidget {
  const SlideBar({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<RangeListViewModel>(
      builder: (context, model, child) {
        return Row(children: [
          Padding(
            padding: EdgeInsets.all(5.0),
            child: model.isLoading
                ? CircularProgressIndicator(
                    color: Color.fromRGBO(190, 10, 10, 1),
                  )
                : ElevatedButton(
                    style: ButtonStyle(
                        backgroundColor: MaterialStateProperty.all(
                            Color.fromRGBO(190, 10, 10, 1))),
                    onPressed: () {
                      if(!model.isLoading) {
                      model.page = 1;
                      model.sellList = [];
                      model.loadData();}
                    },
                    child: SizedBox(
                        width: 30.0,
                        height: 20.0,
                        child: Image.asset("assets/images/carrot.png")),
                  ),
          ),
          SizedBox(
            width: 300,
            child: Slider(
                activeColor: Color.fromRGBO(190, 10, 10, 1),
                inactiveColor: Color.fromRGBO(190, 10, 10, 0.3),
                value: model.searchRange,
                min: 0,
                max: 20,
                divisions: 100,
                onChanged: (double value) {
                  model.searchRange = value;
                }),
          ),
        ]);
      },
    );
  }
}
