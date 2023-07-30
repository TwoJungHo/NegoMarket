import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:used_market/view_models/chat_view_model.dart';
import 'package:used_market/views/board_insert.dart';
import 'package:used_market/views/chat_room_list.dart';
import 'package:used_market/views/in_range.dart';
import 'package:used_market/views/login.dart';


class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  int _selectedIndex = 0;

   set selectedIndex(int value) {
    _selectedIndex = value;
  }

  static List<Widget> _widgetOptions = <Widget>[
    Text('Home Page'),
    InRange(),
    BoardInsert(
      naviIndexCallback: (HomeIndexProvider homeIndexProvider, int index){
        homeIndexProvider.homeIndex = index;
      },
    ),
    ChatRoomList(),
    LoginPage(),
  ];

  void _onItemTapped(HomeIndexProvider homeIndexProvider, int index) {
    homeIndexProvider.homeIndex = index;
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => HomeIndexProvider(),
      child: Consumer<HomeIndexProvider>(
        builder: (context, homeIndexProvider, child) {
          return Scaffold(
            appBar: PreferredSize(
              preferredSize: Size.fromHeight(30.0),
              child: AppBar(
                leading: Image.asset('assets/images/img_appbar.png'),
                backgroundColor: Color.fromRGBO(190, 10, 10, 1),
                elevation: 0.0,
                centerTitle: true,
              ),
            ),
            body: Center(
              child: _widgetOptions.elementAt(homeIndexProvider.homeIndex),
            ),
            bottomNavigationBar: BottomNavigationBar(
              unselectedItemColor: Color.fromRGBO(255, 150, 150, 1),
                selectedItemColor: Color.fromRGBO(250, 250, 250, 1),
                backgroundColor: Color.fromRGBO(150, 5, 5, 1),
              items: <BottomNavigationBarItem>[
                BottomNavigationBarItem(
                  icon: Icon(
                      Icons.home,),
                  label: "홈",
                ),
                BottomNavigationBarItem(
                  icon: Icon(Icons.location_on_rounded),
                  label: '우리 동네',
                ),
                BottomNavigationBarItem(
                  icon: Icon(Icons.money_outlined),
                  label: '판매',
                ),
                BottomNavigationBarItem(
                  icon: Consumer<ChatViewModel>(
                    builder: (context, chatViewModel, child) {
                      return Stack(
                        children: <Widget>[
                          Icon(Icons.chat),
                          // if unread messages count is greater than 0, show badge
                          if (chatViewModel.totalUnreadMessageCount > 0)
                            Positioned(
                              left: 0,
                              child: Container(
                                padding: EdgeInsets.all(0.5),
                                decoration: BoxDecoration(
                                  color: Colors.red,
                                  borderRadius: BorderRadius.circular(6),
                                ),
                                constraints: BoxConstraints(
                                  minWidth: 12,
                                  minHeight: 12,
                                ),
                                child: Text(
                                  '${chatViewModel.totalUnreadMessageCount}',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 10,
                                  ),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                            ),
                        ],
                      );
                    }
                  ),
                  label: '채팅',
                ),
                BottomNavigationBarItem(
                  icon: Icon(Icons.person),
                  label: '마이 네고',
                ),

              ],
              currentIndex: homeIndexProvider.homeIndex,
              onTap: (index) {_onItemTapped(homeIndexProvider, index);},
              type: BottomNavigationBarType.fixed
            ),
          );
        }
      ),
    );
  }


}

class HomeIndexProvider extends ChangeNotifier {
  int _homeIndex = 0;

  int get homeIndex => _homeIndex;

  set homeIndex(int value) {
    _homeIndex = value;
    notifyListeners();
  }

}


