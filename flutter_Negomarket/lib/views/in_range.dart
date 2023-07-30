import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:provider/provider.dart';
import 'package:used_market/view_models/geolocation_view_model.dart';
import 'package:used_market/view_models/range_search_view_model.dart';
import 'package:used_market/views/in_range_list.dart';

class InRange extends StatefulWidget {
  const InRange({Key? key}) : super(key: key);

  @override
  State<InRange> createState() => _InRangeState();
}

class _InRangeState extends State<InRange> {

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      var geolocationViewModel = Provider.of<GeolocationViewModel>(context, listen: false);
      var rangeListViewModel = Provider.of<RangeListViewModel>(context, listen: false);

      rangeListViewModel.longitude = geolocationViewModel.longitude;
      rangeListViewModel.latitude = geolocationViewModel.latitude;
    });
  }





  @override
  Widget build(BuildContext context) {
    return Consumer<GeolocationViewModel>(
      builder: (context, geolocationViewModel, child) {
        return Consumer<RangeListViewModel>(
          builder: (context, rangeListViewModel, child) {

            return Column(
              children: [
                SlideBar(),
                Container(
                  margin: EdgeInsets.all(5.0),
                  decoration: BoxDecoration(
                      border: Border.all(color: Color.fromRGBO(190, 10, 10, 0.8)),
                      borderRadius: BorderRadius.circular(50.0)),
                  height: MediaQuery.of(context).size.height * 0.3,
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(50.0),

                    // 구글 맵!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    child: GoogleMap(
                      initialCameraPosition: CameraPosition(
                        target: LatLng(geolocationViewModel.latitude, geolocationViewModel.longitude),
                        zoom: 10,
                      ),
                      circles: {
                        Circle(
                          circleId: CircleId("circle_1"),
                          center: LatLng(geolocationViewModel.latitude, geolocationViewModel.longitude),
                          radius: 1000 *
                             rangeListViewModel.searchRange,
                          strokeWidth: 1,
                          strokeColor: Colors.black,
                          fillColor: Colors.black.withOpacity(0.3),
                        )
                      },
                      markers: rangeListViewModel.markers,
                    ),
                  ),
                ),
                InRangeList(latLng: LatLng(geolocationViewModel.latitude, geolocationViewModel.longitude)),
              ],
            );
          }
        );
      }
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
                      if (!model.isLoading) {
                        model.page = 1;
                        model.sellList = [];
                        model.loadData();
                      }
                    },
                    child: SizedBox(
                        width: 30.0,
                        height: 20.0,
                        child: Image.asset("assets/images/marker.png")),
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
