import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_quill/flutter_quill.dart' as quill hide Text;
import 'package:flutter_quill_extensions/flutter_quill_extensions.dart';
import 'package:provider/provider.dart';
import 'package:used_market/data_types/chat_room_data.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:used_market/view_models/geolocation_view_model.dart';
import 'package:used_market/views/chatting_room.dart';
import 'package:vsc_quill_delta_to_html/vsc_quill_delta_to_html.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'package:image_picker/image_picker.dart';
import 'package:used_market/data_types/board_data.dart';

class BoardDetail extends StatefulWidget {
  final String sellId;

  const BoardDetail({Key? key, required this.sellId}) : super(key: key);

  @override
  State<BoardDetail> createState() => _BoardDetailState();
}

class _BoardDetailState extends State<BoardDetail> {
  final imagePicker = ImagePicker();
  final FlutterSecureStorage _secureStorage = FlutterSecureStorage();
  final quill.QuillController _quillController = quill.QuillController.basic();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _productNameController = TextEditingController();
  final TextEditingController _priceController = TextEditingController();


  double longitude = 0.0;
  double latitude = 0.0;
  String username = '';


  Future<BoardResponse> getBoard() async {
    username = (await _secureStorage.read(key: 'username'))!;
    String url =
        '${Constants.api_gate_url}/board-service/boards/${widget.sellId}';
    final response = await http.get(
      Uri.parse(url),
      headers: {
        "Content-Type": "application/json",
      },
    );
    BoardResponse boardResponse =
        BoardResponse.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));

    return boardResponse;
  }

  Future<String> _pickImageAndUpload(int kind) async {
    final ImagePicker _picker = ImagePicker();

    final XFile? image = kind == 1
        ? await _picker.pickImage(source: ImageSource.gallery)
        : await _picker.pickImage(source: ImageSource.camera);

    if (image == null) {
      return "";
    }

    final http.MultipartRequest request = http.MultipartRequest(
      'POST',
      Uri.parse('${Constants.api_gate_url}/imgfile-service/uploadimg'),
    );

    request.files.add(await http.MultipartFile.fromPath(
      'file',
      image.path,
    ));

    final http.StreamedResponse streamedResponse = await request.send();

    final http.Response response =
        await http.Response.fromStream(streamedResponse);

    if (response.statusCode == 200) {
      print('Uploaded!');
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      print('Response body: $responseBody');
      print('Result: ${responseBody['result']}');
      return '${responseBody['result']}';
    } else {
      print('Failed to upload image: ${response.statusCode}');
      print('Response body: ${response.body}');
      throw Exception("업로드 실패");
    }
  }

  Future<void> _pickSellImage(int kind, ImageProvider imageProvider) async {
    imageProvider.imageFile = kind == 1
        ? await imagePicker.pickImage(source: ImageSource.gallery)
        : await imagePicker.pickImage(source: ImageSource.camera);
  }

  void insertImage(quill.QuillController controller, String imageUrl) {
    final index = controller.selection.baseOffset;
    var imageEmbeddable = quill.BlockEmbed.image(imageUrl);
    controller.document.insert(index, imageEmbeddable);
    controller.updateSelection(
        TextSelection.collapsed(offset: index + 1), quill.ChangeSource.LOCAL);
  }

  Future<BoardResponse> updateBoard(ImageProvider imageProvider) async {
    var multipartRequest = http.MultipartRequest(
        'PUT',
        Uri.parse(
            '${Constants.api_gate_url}/board-service/boards/${widget.sellId}'));
    final bearerToken = 'Bearer ${await _secureStorage.read(key: 'jwt')}';

    final deltaString =
        jsonEncode(_quillController.document.toDelta().toJson());
    final List<Map<String, dynamic>> deltaList = _quillController.document
        .toDelta()
        .toJson() as List<Map<String, dynamic>>;
    final QuillDeltaToHtmlConverter convertData =
        QuillDeltaToHtmlConverter(deltaList, ConverterOptions.forEmail());
    String htmlString = convertData.convert();

    if (imageProvider.imageFile != null) {
      multipartRequest.files.add(await http.MultipartFile.fromPath(
          'imgFile', imageProvider.imageFile!.path));
    }

    multipartRequest.headers.addAll({'Authorization': bearerToken});

    multipartRequest.fields['longitude'] = longitude.toString();
    multipartRequest.fields['latitude'] = latitude.toString();
    multipartRequest.fields['title'] = _titleController.text;
    multipartRequest.fields['price'] = _priceController.text;
    multipartRequest.fields['productName'] = _productNameController.text;
    multipartRequest.fields['htmlString'] = htmlString;
    multipartRequest.fields['deltaString'] = deltaString;

    final http.StreamedResponse streamedResponse =
        await multipartRequest.send();

    final http.Response response =
        await http.Response.fromStream(streamedResponse);

    if (response.statusCode == 200) {
      print('Uploaded!');
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      print('Response body: $responseBody');
      Navigator.pushReplacement(
          context,
          MaterialPageRoute(
              builder: (context) =>
                  BoardDetail(sellId: responseBody['sellId'])));

      return BoardResponse.fromJson(responseBody);
    } else {
      print('Failed to upload image: ${response.statusCode}');
      print('Response body: ${response.body}');
      throw Exception("업로드 실패");
    }
  }

  @override
  Widget build(BuildContext context) {
    longitude = Provider.of<GeolocationViewModel>(context).longitude;
    latitude = Provider.of<GeolocationViewModel>(context).latitude;
    return Scaffold(
      appBar: PreferredSize(
        preferredSize: Size.fromHeight(50.0),
        child: AppBar(
          leading: GestureDetector(
              onTap: () {
                Navigator.pop(context);
              },
              child: Image.asset('assets/images/img_appbar.png')),
          backgroundColor: Color.fromRGBO(190, 10, 10, 1),
          elevation: 0.0,
          centerTitle: true,
        ),
      ),
      body: MultiProvider(
        providers: [
          ChangeNotifierProvider<ImageProvider>(
            create: (_) => ImageProvider(),
          ),
          ChangeNotifierProvider<BoardProvider>(
            create: (_) => BoardProvider(),
          ),
        ],
        child: FutureBuilder<BoardResponse>(
            future: getBoard(),
            builder:
                (BuildContext context, AsyncSnapshot<BoardResponse> snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return CircularProgressIndicator(); // or other loading widget
              } else if (snapshot.hasError) {
                return Text('Error: ${snapshot.error}');
              } else {
                return Consumer<BoardProvider>(
                    builder: (context, boardProvider, child) {
                  boardProvider.boardResponse = snapshot.data;
                  boardProvider.initBoardValue(
                      _titleController,
                      _productNameController,
                      _priceController,
                      _quillController);
                  return Column(
                    children: [
                      Row(
                          children: boardProvider.readOnly
                              ? [
                                  Container(
                                    padding: EdgeInsets.all(5),
                                    width: MediaQuery.of(context).size.width *
                                        0.77,
                                    height: MediaQuery.of(context).size.height *
                                        0.08,
                                    child: TextFormField(
                                      controller: _titleController,
                                      readOnly: boardProvider.readOnly,
                                      decoration: const InputDecoration(
                                          labelText: '제목'),
                                      validator: (String? value) {
                                        if (value == null || value.isEmpty) {
                                          return '제목을 입력하세요';
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                  Consumer<ImageProvider>(
                                    builder: (context, imageProvider, child) {
                                      return ElevatedButton(
                                        onPressed: () {
                                          boardProvider.readOnly = false;
                                        },
                                        child: Text('수정'),
                                      );
                                    },
                                  )
                                ]
                              : [
                                  Container(
                                    padding: EdgeInsets.all(5),
                                    width:
                                        MediaQuery.of(context).size.width * 0.7,
                                    height: MediaQuery.of(context).size.height *
                                        0.08,
                                    child: TextFormField(
                                      controller: _titleController,
                                      readOnly: boardProvider.readOnly,
                                      decoration: const InputDecoration(
                                          labelText: '제목'),
                                      validator: (String? value) {
                                        if (value == null || value.isEmpty) {
                                          return '제목을 입력하세요';
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                  Consumer<ImageProvider>(
                                    builder: (context, imageProvider, child) {
                                      return Container(
                                        width:
                                            MediaQuery.of(context).size.width *
                                                0.15,
                                        child: ElevatedButton(
                                          style: ButtonStyle(),
                                          onPressed: () async {
                                            updateBoard(imageProvider);
                                          },
                                          child: Text('확인'),
                                        ),
                                      );
                                    },
                                  ),
                                  Consumer<ImageProvider>(
                                    builder: (context, imageProvider, child) {
                                      return Container(
                                        width:
                                            MediaQuery.of(context).size.width *
                                                0.15,
                                        child: ElevatedButton(
                                          onPressed: () {
                                            boardProvider.readOnly = true;
                                            imageProvider._imageFile = null;
                                          },
                                          child: Text('취소'),
                                        ),
                                      );
                                    },
                                  )
                                ]),
                      Container(
                        decoration: BoxDecoration(
                            color: Color.fromRGBO(190, 10, 10, 0.1)),
                        child: Column(children: [
                          Row(
                            children: [
                              Consumer<ImageProvider>(
                                builder: (context, imageProvider, child) {
                                  return GestureDetector(
                                    onTap: () {
                                      if (boardProvider.readOnly) {
                                      } else {
                                        showModalBottomSheet<void>(
                                          context: context,
                                          builder: (BuildContext context) {
                                            return Container(
                                              height: 200,
                                              color: Colors.white,
                                              child: Center(
                                                child: Column(
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.center,
                                                  mainAxisSize:
                                                      MainAxisSize.min,
                                                  children: <Widget>[
                                                    ListTile(
                                                      leading:
                                                          Icon(Icons.photo),
                                                      title: Text('Gallery'),
                                                      onTap: () async {
                                                        _pickSellImage(
                                                            1, imageProvider);
                                                        Navigator.pop(
                                                            context); // 선택 후 바닥 시트를 닫습니다.
                                                      },
                                                    ),
                                                    ListTile(
                                                      leading:
                                                          Icon(Icons.camera),
                                                      title: Text('Camera'),
                                                      onTap: () async {
                                                        _pickSellImage(
                                                            2, imageProvider);
                                                        Navigator.pop(
                                                            context); // 선택 후 바닥 시트를 닫습니다.
                                                      },
                                                    ),
                                                  ],
                                                ),
                                              ),
                                            );
                                          },
                                        );
                                      }
                                    },
                                    child: Container(
                                      decoration: BoxDecoration(
                                        border: Border.all(
                                          color:
                                              Color.fromRGBO(190, 10, 10, 0.8),
                                          width: 1,
                                        ),
                                        borderRadius: BorderRadius.circular(20),
                                      ),
                                      padding: EdgeInsets.all(5),
                                      margin: EdgeInsets.all(5),
                                      width: MediaQuery.of(context).size.width *
                                          0.35,
                                      height:
                                          MediaQuery.of(context).size.width *
                                              0.35,
                                      child: boardProvider.readOnly
                                          ? Image.network(
                                              '${Constants.api_gate_url}/sell-service/img/${widget.sellId}')
                                          : imageProvider._imageFile == null
                                              ? Image.network(
                                                  '${Constants.api_gate_url}/sell-service/img/${widget.sellId}')
                                              : Image.file(File(imageProvider
                                                  ._imageFile!.path)),
                                    ),
                                  );
                                },
                              ),
                              Form(
                                key: _formKey,
                                child: Container(
                                  width:
                                      MediaQuery.of(context).size.width * 0.6,
                                  height:
                                      MediaQuery.of(context).size.height * 0.22,
                                  child: Column(
                                    children: [
                                      boardProvider.boardResponse!.username !=
                                              username
                                          ? Row(
                                              children: [
                                                Text(
                                                    '판매자: ${boardProvider.boardResponse!.sellInfoResponse.username}'),
                                                ElevatedButton(
                                                    onPressed: () async {
                                                      String url =
                                                          '${Constants.api_gate_url}/chat-service/enter';
                                                      final bearerToken =
                                                          'Bearer ${await _secureStorage.read(key: 'jwt')}';
                                                      Map<String, String>
                                                          chatRoomRequest = {
                                                        'username2':
                                                            boardProvider
                                                                .boardResponse!
                                                                .sellInfoResponse
                                                                .username,
                                                      };
                                                      final response =
                                                          await http.post(
                                                              Uri.parse(url),
                                                              headers: {
                                                                'Content-Type':
                                                                    'application/json',
                                                                'Authorization':
                                                                    bearerToken,
                                                              },
                                                              body: jsonEncode(
                                                                  chatRoomRequest));

                                                      final roomInfoResult =
                                                          jsonDecode(utf8.decode(
                                                                  response
                                                                      .bodyBytes))[
                                                              'roomInfo'];
                                                      final chatRoom =
                                                          ChatRoom.fromJson(
                                                              roomInfoResult);

                                                      Navigator.push(
                                                          context,
                                                          MaterialPageRoute(
                                                              builder: (context) =>
                                                                  ChattingRoom(
                                                                      chatRoom:
                                                                          chatRoom)));
                                                    },
                                                    child: Text("채팅"))
                                              ],
                                            )
                                          : SizedBox(
                                              width: 0,
                                              height: 0,
                                            ),
                                      TextFormField(
                                        controller: _productNameController,
                                        readOnly: boardProvider.readOnly,
                                        decoration: const InputDecoration(
                                            labelText: '상품명'),
                                        validator: (String? value) {
                                          if (value == null || value.isEmpty) {
                                            return '상품명을 입력하세요';
                                          }
                                          return null;
                                        },
                                      ),
                                      TextFormField(
                                        controller: _priceController,
                                        readOnly: boardProvider.readOnly,
                                        decoration: const InputDecoration(
                                            labelText: '가격'),
                                        obscureText: false,
                                        validator: (String? value) {
                                          if (value == null || value.isEmpty) {
                                            return '가격을 입력하세요';
                                          }
                                          if (int.tryParse(value) == null) {
                                            return '숫자로 입력하세요';
                                          }
                                          return null;
                                        },
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ]),
                      ),
                      Expanded(
                        child: Padding(
                          padding: EdgeInsets.fromLTRB(10, 10, 10, 10),
                          child: Container(
                            child: quill.QuillEditor.basic(
                                controller: _quillController,
                                readOnly: boardProvider.readOnly,
                                embedBuilders: FlutterQuillEmbeds.builders()),
                          ),
                        ),
                      ),
                      !boardProvider.readOnly
                          ? quill.QuillToolbar.basic(
                              controller: _quillController,
                              showCodeBlock: false,
                              showFontFamily: false,
                              showFontSize: false,
                              showIndent: false,
                              showListCheck: false,
                              showListBullets: false,
                              showListNumbers: false,
                              showSearchButton: false,
                              showQuote: false,
                              showSubscript: false,
                              showInlineCode: false,
                              showSmallButton: false,
                              showDirection: false,
                              showHeaderStyle: false,
                              showLink: false,
                              showDividers: false,
                              showSuperscript: false,
                              showRedo: false,
                              showUndo: false,
                              customButtons: [
                                quill.QuillCustomButton(
                                    child: IconButton(
                                        icon: Icon(Icons.image),
                                        // child: Icon(
                                        //   Icons.image
                                        // ),
                                        onPressed: () {
                                          showModalBottomSheet<void>(
                                            context: context,
                                            builder: (BuildContext context) {
                                              return Container(
                                                height: 200,
                                                color: Colors.white,
                                                child: Center(
                                                  child: Column(
                                                    mainAxisAlignment:
                                                        MainAxisAlignment
                                                            .center,
                                                    mainAxisSize:
                                                        MainAxisSize.min,
                                                    children: <Widget>[
                                                      ListTile(
                                                        leading:
                                                            Icon(Icons.photo),
                                                        title: Text('Gallery'),
                                                        onTap: () async {
                                                          String id =
                                                              await _pickImageAndUpload(
                                                                  1);
                                                          if (id != "") {
                                                            insertImage(
                                                                _quillController,
                                                                "${Constants.api_gate_url}/imgfile-service/getimgdata?id=$id");
                                                          }
                                                          Navigator.pop(
                                                              context); // 선택 후 바닥 시트를 닫습니다.
                                                        },
                                                      ),
                                                      ListTile(
                                                        leading:
                                                            Icon(Icons.camera),
                                                        title: Text('Camera'),
                                                        onTap: () async {
                                                          String id =
                                                              await _pickImageAndUpload(
                                                                  2);
                                                          if (id != "") {
                                                            insertImage(
                                                                _quillController,
                                                                "${Constants.api_gate_url}/imgfile-service/getimgdata?id=$id");
                                                          }
                                                          Navigator.pop(
                                                              context); // 선택 후 바닥 시트를 닫습니다.
                                                        },
                                                      ),
                                                    ],
                                                  ),
                                                ),
                                              );
                                            },
                                          );
                                        }))
                              ],
                            )
                          : Text("")
                    ],
                  );
                });
              }
            }),
      ),
    );
  }
}

class ImageProvider with ChangeNotifier {
  XFile? _imageFile;

  XFile? get imageFile => _imageFile;

  set imageFile(XFile? value) {
    _imageFile = value;
    notifyListeners();
  }
}

class BoardProvider with ChangeNotifier {
  bool _readOnly = true;

  bool get readOnly => _readOnly;

  set readOnly(bool value) {
    _readOnly = value;
    notifyListeners();
  }

  BoardResponse? _boardResponse;

  BoardResponse? get boardResponse => _boardResponse;

  set boardResponse(BoardResponse? value) {
    _boardResponse = value;
  }

  void initBoardValue(
      TextEditingController titleCon,
      TextEditingController productNameCon,
      TextEditingController priceCon,
      quill.QuillController quillCon) {
    titleCon.value = TextEditingValue(text: _boardResponse!.title);
    productNameCon.value =
        TextEditingValue(text: _boardResponse!.sellInfoResponse.productName);
    priceCon.value = TextEditingValue(
        text: _boardResponse!.sellInfoResponse.price.toString());
    quill.Delta delta =
        quill.Delta.fromJson(jsonDecode(_boardResponse!.deltaString));
    quillCon.document = quill.Document.fromDelta(delta);
  }
}
