import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:used_market/views/board_detail.dart';
import 'package:used_market/view_models/range_search_view_model.dart';

class InRangeList extends StatelessWidget {
  final LatLng latLng;

  const InRangeList({super.key, required this.latLng});

  @override
  Widget build(BuildContext context) {
    final ScrollController scrollController = ScrollController();
    scrollController.addListener(() {
      if (scrollController.position.pixels ==
          scrollController.position.maxScrollExtent) {
        context.read<RangeListViewModel>().loadData();
      }
    });

    var rangeListViewModel = context.read<RangeListViewModel>();

    return Expanded(
            child: ListView.builder(
              controller: scrollController,
              itemCount: rangeListViewModel.sellList.length,
              itemBuilder: (context, index) {
                return Column(
                  children: [
                    InkWell(
                      onTap: (){
                        Navigator.push(
                            context,
                            MaterialPageRoute(
                            builder: (context) => BoardDetail(
                                sellId:rangeListViewModel.sellList[index].id)
                            )
                        );
                      },
                      child: ListTile(
                        leading: SizedBox(
                          width: 100,
                          height: 100,
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(5.0),
                            child: Image.network(
                                '${Constants.api_gate_url}/sell-service/img/${rangeListViewModel.sellList[index].id}',
                              fit: BoxFit.cover,
                            ),
                          ),

                        ),
                        title: Text(rangeListViewModel.sellList[index].productName),
                        subtitle:
                            Text(
                                '${NumberFormat('#,###').format(rangeListViewModel.sellList[index].price)} 원'
                            ),

                      ),
                    ),
                    Divider(),
                  ],
                );
              },
            ),
          );
  }
}
